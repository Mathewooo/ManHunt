package gg.matthew.core;

import gg.matthew.Main;
import gg.matthew.core.nametags.NameTags;
import gg.matthew.core.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ManHunt {
    private static ManHunt instance;
    private final Vector<UUID> hunters = new Vector<>();
    private final Vector<UUID> runners = new Vector<>();
    private final LinkedHashMap<UUID, ItemStack> huntersCompasses = new LinkedHashMap<>();
    private final Vector<UUID> merged = new Vector<>();
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

    public List<UUID> getHunters() {
        return Collections.unmodifiableList(hunters);
    }

    public void setHunters(List<String> players) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!players.contains(player.getName())) {
                hunters.add(player.getUniqueId());
            }
        }
        Bukkit.getLogger().info(String.valueOf(hunters));
    }

    public List<UUID> getRunners() {
        return Collections.unmodifiableList(runners);
    }

    public void setRunners(List<String> players) {
        for (String runner : players) {
            runners.add(Bukkit.getPlayer(runner).getUniqueId());
        }
        Bukkit.getLogger().info(String.valueOf(runners));
    }

    public void startGame() {
        setMerged();
        for (UUID uuid : merged) {
            Bukkit.getPlayer(uuid).closeInventory();
            Bukkit.getPlayer(uuid).getInventory().clear();
        }
        createCompasses();
        settingLodestone();
        NameTags.getInstance().setNameTags();
        NameTags.getInstance().newTags();
        setGlowing();
        merged.clear();
    }

    public void cancelCurrentGame() {
        setMerged();
        NameTags.getInstance().removeTags();
        disableGlowing();
        merged.clear();
        for (UUID uuid : hunters) {
            for (ItemStack itemStack : Bukkit.getPlayer(uuid).getInventory().getContents()) {
                if (itemStack != null && Objects.equals(itemStack.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING), "tracker")) {
                    Bukkit.getPlayer(uuid).getInventory().remove(itemStack);
                    break;
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

    private void createCompasses() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
        compassMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "tracker");
        compass.setItemMeta(compassMeta);
        for (UUID uuid : hunters) {
            Bukkit.getPlayer(uuid).getInventory().setItemInOffHand(compass);
            if (Objects.equals(Bukkit.getPlayer(uuid).getInventory().getItemInOffHand().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING), "tracker")) {
                huntersCompasses.put(uuid, Bukkit.getPlayer(uuid).getInventory().getItemInOffHand());
            }
        }
    }

    //FIX lodestone glitching in end and nether
    private void settingLodestone() {
        //TODO make check for the change in worlds
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
            for (Map.Entry<UUID, ItemStack> entry : huntersCompasses.entrySet()) {
                if (Bukkit.getPlayer(entry.getKey()) != null) {
                    Player nearestPlayer = Utils.getNearestPlayer(Bukkit.getPlayer(entry.getKey()));
                    if (nearestPlayer != null) {
                        Bukkit.getPlayer(entry.getKey()).setCompassTarget(nearestPlayer.getLocation());
                    }
                    ItemMeta itemMeta = entry.getValue().getItemMeta();
                    itemMeta.setLore(Collections.singletonList(nearestPlayer != null ? ChatColor.WHITE + "Nearest Runner: " + ChatColor.GRAY + nearestPlayer.getName() : ChatColor.WHITE + "Players went to different dimension"));
                    entry.getValue().setItemMeta(itemMeta);
                }
            }
        }, 0, 4);
    }

    private void setGlowing() {
        for (UUID uuid : hunters) {
            Bukkit.getPlayer(uuid).setGlowing(true);
        }
    }

    private void disableGlowing() {
        for (UUID uuid : hunters) {
            Bukkit.getPlayer(uuid).setGlowing(false);
        }
    }

    public Vector<UUID> getMerged() {
        return merged;
    }

    private void setMerged() {
        merged.addAll(hunters);
        merged.addAll(runners);
        Bukkit.getLogger().info(String.valueOf(merged));
    }
}
