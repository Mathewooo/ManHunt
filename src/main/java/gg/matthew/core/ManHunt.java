package gg.matthew.core;

import gg.matthew.Main;
import gg.matthew.core.nametags.NameTags;
import gg.matthew.core.players.model.Hunter;
import gg.matthew.core.scoreboard.ScoreBoards;
import gg.matthew.core.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ManHunt {
    private static ManHunt instance;
    private final Vector<Hunter> hunters = new Vector<>();
    private final Vector<UUID> runners = new Vector<>();
    private final LinkedHashMap<UUID, ItemStack> huntersCompasses = new LinkedHashMap<>();
    private final Vector<UUID> merged = new Vector<>();
    BukkitTask task;
    NamespacedKey key = new NamespacedKey(Main.getInstance(), "hunter_compass");
    private boolean gameStarted = false;

    public static synchronized ManHunt getInstance() {
        if (instance == null) instance = new ManHunt();
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

    public List<Hunter> getHunters() {
        return Collections.unmodifiableList(hunters);
    }

    public void setHunters(List<Hunter> players) {
        hunters.addAll(players);
    }

    public void removeRunner(UUID uuid) {
        runners.remove(uuid);
    }

    public void removeHunter(UUID uuid) {
        hunters.forEach(hunter -> {
            if (hunter.getPlayerId().equals(uuid)) hunters.remove(hunter);
        });
    }

    public List<UUID> getRunners() {
        return Collections.unmodifiableList(runners);
    }

    public void setRunners(List<String> players) {
        players.forEach(runner -> runners.add(Bukkit.getPlayer(runner).getUniqueId()));
    }

    public void startGame() {
        setMerged();
        for (UUID uuid : merged) {
            Bukkit.getPlayer(uuid).closeInventory();
            Bukkit.getPlayer(uuid).getInventory().clear();
        }
        createCompasses();
        NameTags.getInstance().setNameTags();
        NameTags.getInstance().newTags();
        setGlowing();
        ScoreBoards.getInstance().createScoreBoards();
        merged.clear();
    }

    public void cancelCurrentGame() {
        setMerged();
        NameTags.getInstance().removeTags();
        disableGlowing();
        ScoreBoards.getInstance().removeScoreBoards();
        merged.clear();
        removeCompasses();
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

    private void removeCompasses() {
        for (UUID uuid : returnFilteredHunters()) {
            Player player = Bukkit.getPlayer(uuid);
            boolean shouldContinue = true;
            for (ItemStack itemStack : player.getInventory().getContents())
                if (itemStack != null)
                    if (Objects.equals(itemStack.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING), "tracker")) {
                        player.getInventory().remove(itemStack);
                        shouldContinue = false;
                        break;
                    }
            //FIX THIS!!
            if (shouldContinue && player.getInventory().getItemInOffHand() != null)
                if (Objects.equals(player.getInventory().getItemInOffHand().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING), "tracker"))
                    player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }
    }

    private void createCompasses() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
        compassMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "tracker");
        for (UUID uuid : returnFilteredHunters()) {
            Player nearestPlayer = Utils.getNearestPlayer(Bukkit.getPlayer(uuid));
            if (nearestPlayer != null)
                compassMeta.setLore(Collections.singletonList(ChatColor.WHITE + "Nearest Runner: " + ChatColor.GRAY + nearestPlayer.getName()));
            compass.setItemMeta(compassMeta);
            Bukkit.getPlayer(uuid).getInventory().setItemInOffHand(compass);
            if (Objects.equals(Bukkit.getPlayer(uuid).getInventory().getItemInOffHand().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING), "tracker"))
                huntersCompasses.put(uuid, Bukkit.getPlayer(uuid).getInventory().getItemInOffHand());
        }
    }

    public Map<UUID, ItemStack> getHuntersCompasses() {
        return Collections.unmodifiableMap(huntersCompasses);
    }

    //TODO At some point implement configurable feature where only runners can see the glowing hunters not hunters seeing glowing hunter (can be done through nms) (implement in version 1.1)
    private void setGlowing() {
        for (UUID uuid : returnFilteredHunters())
            Bukkit.getPlayer(uuid).setGlowing(true);
    }

    private void disableGlowing() {
        for (UUID uuid : returnFilteredHunters())
            Bukkit.getPlayer(uuid).setGlowing(false);
    }

    public List<UUID> returnFilteredHunters() {
        List<UUID> hunters = new ArrayList<>();
        ManHunt.getInstance().getHunters().forEach(hunter -> hunters.add(hunter.getPlayerId()));
        return Collections.unmodifiableList(hunters);
    }

    public Vector<UUID> getMerged() {
        return merged;
    }

    private void setMerged() {
        merged.addAll(returnFilteredHunters());
        merged.addAll(runners);
    }

    public Hunter returnHunterObject(UUID uuid) {
        var ref = new Object() {
            Hunter hunter;
        };
        hunters.forEach(hunter -> {
            if (hunter.getPlayerId().equals(uuid)) ref.hunter = hunter;
        });
        if (ref.hunter != null) return ref.hunter;
        return null;
    }
}
