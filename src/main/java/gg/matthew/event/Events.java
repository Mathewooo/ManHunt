package gg.matthew.event;

import gg.matthew.core.ManHunt;
import gg.matthew.core.particle.Particle;
import gg.matthew.core.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
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

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (ManHunt.getInstance().hasGameStarted() && event.getRecipe().getResult().getType() == Material.COMPASS)
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        //TODO check world too
        EntityType entity = event.getEntity().getType();
        Player killer = event.getEntity().getKiller();
        if (ManHunt.getInstance().hasGameStarted() && entity == EntityType.ENDER_DRAGON)
            if (event.getEntity().getWorld().getEnvironment().equals(World.Environment.THE_END))
                if (ManHunt.getInstance().getRunners().contains(killer.getUniqueId())) {
                    //TODO end the game and make particle effects(spiral or sth. like that) for the runner that won
                    Bukkit.broadcastMessage(event.getEntity().getKiller().getName() + " Won as runner!");
                } else if (ManHunt.getInstance().getHunters().contains(killer.getUniqueId())) {
                    Bukkit.broadcastMessage(event.getEntity().getKiller().getName() + " Won as hunter!");
                    //TODO end the game and make sad particle effects for the runners and send titles that hunters won
                }
    }

    //TODO fix the things when you can put compass to chest or dispensers

    //CHECK THIS IF IT WORKS!!
    //FIX lodestone glitching in end and nether
    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Particle.getInstance().spawnWinnerEffect(player);
        if (ManHunt.getInstance().hasGameStarted() && ManHunt.getInstance().getRunners().contains(player.getUniqueId())) {
            Player nearestPlayer = Utils.getNearestPlayer(player);
            if (nearestPlayer != null) {
                nearestPlayer.setCompassTarget(player.getLocation());
                ItemMeta itemMeta = ManHunt.getInstance().getHuntersCompasses().get(nearestPlayer.getUniqueId()).getItemMeta();
                itemMeta.setLore(Collections.singletonList(ChatColor.WHITE + "Nearest Runner: " + ChatColor.GRAY + player.getName()));
            } else {
                for (Map.Entry<UUID, ItemStack> entry : ManHunt.getInstance().getHuntersCompasses().entrySet()) {
                    if (Bukkit.getPlayer(entry.getKey()) != null) {
                        ItemMeta itemMeta = entry.getValue().getItemMeta();
                        itemMeta.setLore(Collections.singletonList(ChatColor.WHITE + "Runners went to different dimension"));
                        entry.getValue().setItemMeta(itemMeta);
                    }
                }
            }
        }
    }
}
