package gg.matthew.core;

import gg.matthew.Main;
import gg.matthew.core.nametags.NameTags;
import gg.matthew.core.players.pregame.PreGame;
import gg.matthew.core.players.pregame.model.Command;
import gg.matthew.core.players.pregame.model.Hunter;
import gg.matthew.core.scoreboard.ScoreBoards;
import gg.matthew.core.utils.Utils;
import gg.matthew.event.Events;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ManHunt {
    //TODO !!! glowing doesn't work
    //TODO implement countdown on game start for hunters so runners can have time to hide
    //TODO cache hunters position when they die and if they still have lives left after subtracting one and then when they'll respawn teleport them to the location
    private static ManHunt instance;
    private final Vector<Hunter> hunters = new Vector<>();
    private final Vector<UUID> runners = new Vector<>();
    private final LinkedHashMap<UUID, ItemStack> huntersCompasses = new LinkedHashMap<>();
    private final Vector<UUID> merged = new Vector<>();
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

    public void setRunners(List<UUID> players) {
        runners.addAll(players);
    }

    public void startGame(UUID uuid) {
        Command preGame = PreGame.getInstance().returnPreGameCommand(uuid);
        setHunters(preGame.getHunters());
        setRunners(preGame.getRunners());
        Events.getInstance().registerEvents();
        setMerged();
        closeInventories();
        createCompasses();
        NameTags.getInstance().setNameTags();
        NameTags.getInstance().newTags();
        setGlowing();
        ScoreBoards.getInstance().createScoreBoards();
        merged.clear();
    }

    public void cancelCurrentGame() {
        Events.getInstance().unRegisterEvents();
        setMerged();
        NameTags.getInstance().removeTags();
        disableGlowing();
        ScoreBoards.getInstance().removeScoreBoards();
        merged.clear();
        removeCompasses();
        hunters.clear();
        runners.clear();
    }

    private void closeInventories() {
        merged.forEach(uuid -> {
            Bukkit.getPlayer(uuid).closeInventory();
            Bukkit.getPlayer(uuid).getInventory().clear();
        });
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

    //TODO lore doesn't work !!
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
            if (Objects.equals(Bukkit.getPlayer(uuid).getInventory().getItemInOffHand().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING), "tracker")) {
                Bukkit.getLogger().severe("Successfully registered tracker!");
                huntersCompasses.put(uuid, Bukkit.getPlayer(uuid).getInventory().getItemInOffHand());
            }
        }
    }

    public Map<UUID, ItemStack> getHuntersCompasses() {
        return Collections.unmodifiableMap(huntersCompasses);
    }

    private void setGlowing() {
        for (UUID uuid : returnFilteredHunters())
            Bukkit.getPlayer(uuid).setGlowing(true);
    }

    private void disableGlowing() {
        for (UUID uuid : returnFilteredHunters())
            Bukkit.getPlayer(uuid).setGlowing(false);
    }

    public List<UUID> returnFilteredHunters() {
        List<UUID> filteredHunters = new ArrayList<>();
        hunters.forEach(hunter -> filteredHunters.add(hunter.getPlayerId()));
        return Collections.unmodifiableList(filteredHunters);
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
