package gg.matthew.event;

import gg.matthew.core.ManHunt;
import gg.matthew.core.players.model.Hunter;
import gg.matthew.core.nametags.NameTags;
import gg.matthew.core.particle.armorstand.Circle;
import gg.matthew.core.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Events implements Listener {
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (ManHunt.getInstance().hasGameStarted())
            if (Objects.equals(itemStack.getItemMeta().getPersistentDataContainer().get(ManHunt.getInstance().getKey(), PersistentDataType.STRING), "tracker"))
                event.setCancelled(true);
    }

    //TODO check if it works right
    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (ManHunt.getInstance().hasGameStarted() && event.getRecipe().getResult().getType() == Material.COMPASS)
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        EntityType entity = event.getEntity().getType();
        Player killer = event.getEntity().getKiller();
        if (ManHunt.getInstance().hasGameStarted() && entity.equals(EntityType.ENDER_DRAGON))
            if (event.getEntity().getWorld().getEnvironment().equals(World.Environment.THE_END))
                if (ManHunt.getInstance().getRunners().contains(killer.getUniqueId())) endGame(killer, "runners");

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        Player killer = event.getEntity().getKiller();
        if (ManHunt.getInstance().hasGameStarted())
            if (ManHunt.getInstance().getRunners().contains(player.getUniqueId())) {
                if (ManHunt.getInstance().returnFilteredHunters().contains(killer.getUniqueId())) {
                    NameTags.getInstance().removeTag(player.getUniqueId());
                    ManHunt.getInstance().removeRunner(player.getUniqueId());
                    //TODO add the runner as spectator if the game is still running with at least one runner and hunter
                    if (ManHunt.getInstance().getRunners().isEmpty()) endGame(killer, "hunters");
                }
            } else if (ManHunt.getInstance().returnFilteredHunters().contains(player.getUniqueId())) {
                if (ManHunt.getInstance().getRunners().contains(killer.getUniqueId())) {
                    Hunter hunterObject = ManHunt.getInstance().returnHunterObject(player.getUniqueId());
                    if (hunterObject.getLives() > 0) hunterObject.updateLives(hunterObject.getLives() - 1);
                    if (hunterObject.getLives() == 0) {
                        NameTags.getInstance().removeTag(player.getUniqueId());
                        ManHunt.getInstance().removeHunter(player.getUniqueId());
                        //TODO add the hunter as spectator if he wasn't the last remaining
                        if (ManHunt.getInstance().getHunters().isEmpty()) endGame(killer, "runners");
                    }// else if (hunterObject.getLives() == 1) {} //TODO implement an active effect for hunter if he has only one live left
                }
            }
    }

    private void endGame(Player killer, String type) {
        switch (type) {
            case "runners" -> {
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 120, 255), 1.0F);
                Circle.getInstance().winnerEffect(killer, dustOptions);
                Utils.sendTitles(ManHunt.getInstance().getRunners(), ChatColor.BLUE + "You've won!", "Runner " + killer + " killed dragon!", true, killer.getUniqueId());
                Utils.sendTitles(ManHunt.getInstance().returnFilteredHunters(), ChatColor.RED + "You've lost!", "", false, null);
                Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.GRAY + killer.getName() + ChatColor.RESET + " Won as runner!");
            }
            case "hunters" -> {
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 120, 0), 1.0F);
                Circle.getInstance().winnerEffect(killer, dustOptions);
                Utils.sendTitles(ManHunt.getInstance().returnFilteredHunters(), ChatColor.BLUE + "You've won!", "Hunter " + killer + " killed last runner!", true, killer.getUniqueId());
                Utils.sendTitles(ManHunt.getInstance().getRunners(), ChatColor.RED + "You've lost!", "", false, null);
                Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.GRAY + killer.getName() + ChatColor.RESET + " Won as hunter!");
            }
        }
        ManHunt.getInstance().cancelCurrentGame();
    }

    //TODO fix the things when you can put compass to chest or dispensers
    //TODO add option to disable join event when manhunt game is currently running

    //CHECK THIS IF IT WORKS!!
    //FIX lodestone glitching in end and nether
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (ManHunt.getInstance().hasGameStarted() && ManHunt.getInstance().getRunners().contains(player.getUniqueId())) {
            Player nearestPlayer = Utils.getNearestPlayer(player);
            if (nearestPlayer != null) {
                nearestPlayer.setCompassTarget(player.getLocation());
                ItemMeta itemMeta = ManHunt.getInstance().getHuntersCompasses().get(nearestPlayer.getUniqueId()).getItemMeta();
                itemMeta.setLore(Collections.singletonList(ChatColor.WHITE + "Nearest Runner: " + ChatColor.GRAY + player.getName()));
            } else {
                for (Map.Entry<UUID, ItemStack> entry : ManHunt.getInstance().getHuntersCompasses().entrySet())
                    if (Bukkit.getPlayer(entry.getKey()) != null) {
                        ItemMeta itemMeta = entry.getValue().getItemMeta();
                        itemMeta.setLore(Collections.singletonList(ChatColor.WHITE + "Runners went to different dimension"));
                        entry.getValue().setItemMeta(itemMeta);
                    }
            }
        }
    }
}
