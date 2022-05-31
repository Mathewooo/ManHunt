package gg.matthew.core;

import gg.matthew.Main;
import gg.matthew.core.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ManHunt {
    private static ManHunt instance;
    private final Vector<UUID> hunters = new Vector<>();
    private final Vector<UUID> runners = new Vector<>();
    private final LinkedHashMap<UUID, ItemStack> huntersCompasses = new LinkedHashMap<>();
    BukkitTask task;
    NamespacedKey key = new NamespacedKey(Main.getInstance(), "hunter_compass");
    private boolean gameStarted = false;

    public static synchronized ManHunt getInstance() {
        if (instance == null) {
            instance = new ManHunt();
        }
        return instance;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public boolean hasGameStarted() {
        return gameStarted;
    }

    public void setGameStarted() {
        gameStarted = true;
    }

    public void setGameStopped() {
        gameStarted = false;
    }

    public Vector<UUID> getHunters() {
        return hunters;
    }

    public void setHunters(List<String> players) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!players.contains(player.getName())) {
                hunters.add(player.getUniqueId());
            }
        }
    }

    public Vector<UUID> getRunners() {
        return runners;
    }

    public void setRunners(List<String> players) {
        for (String runner : players) {
            runners.add(Bukkit.getPlayer(runner).getUniqueId());
        }
    }

    public void enableUtilityThingsForStartedGame() {
        //enable these: (sth. as glowing, nametags, compasses, lives for hunters and runners and scoreboards)
        generateCompassForHunters();
        startSettingLodestone();
    }

    public void disableUtilityThingsForCurrentGame() {
        //disable these: (sth. as glowing, nametags, compasses, lives for hunters and runners and scoreboards)
        for (UUID uuid : hunters) {
            for (ItemStack itemStack : Bukkit.getPlayer(uuid).getInventory().getStorageContents()) {
                if (itemStack != null) {
                    if (Objects.equals(itemStack.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("hunter_compass"), PersistentDataType.STRING), "tracker")) {
                        Bukkit.getPlayer(uuid).getInventory().remove(itemStack);
                        break;
                    }
                }
            }
        }
        hunters.clear();
        runners.clear();
        cancelTask();
    }

    private void cancelTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void generateCompassForHunters() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
        compassMeta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        compassMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "tracker");
        compass.setItemMeta(compassMeta);
        for (UUID uuid : hunters) {
            Bukkit.getPlayer(uuid).getInventory().setItemInOffHand(compass);
            if (Objects.equals(Bukkit.getPlayer(uuid).getInventory().getItemInOffHand().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING), "tracker")) {
                huntersCompasses.put(uuid, Bukkit.getPlayer(uuid).getInventory().getItemInOffHand());
            }
        }
    }

    private void startSettingLodestone() {
        //TODO make check for the change in worlds
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
            for (Map.Entry<UUID, ItemStack> entry : huntersCompasses.entrySet()) {
                Player nearestPlayer = Utils.getNearestPlayer(Bukkit.getPlayer(entry.getKey()));
                Bukkit.getPlayer(entry.getKey()).setCompassTarget(nearestPlayer.getLocation());
                CompassMeta compassMeta = (CompassMeta) entry.getValue().getItemMeta();
                compassMeta.setLore(Collections.singletonList(ChatColor.WHITE + "Nearest Runner: " + ChatColor.GRAY + nearestPlayer.getName()));
                entry.getValue().setItemMeta(compassMeta);
            }
        }, 0, 4);
    }
}
