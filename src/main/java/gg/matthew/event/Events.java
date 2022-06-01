package gg.matthew.event;

import gg.matthew.core.ManHunt;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class Events implements Listener {
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (ManHunt.getInstance().hasGameStarted() && Objects.equals(itemStack.getItemMeta().getPersistentDataContainer().get(ManHunt.getInstance().getKey(), PersistentDataType.STRING), "tracker")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (ManHunt.getInstance().hasGameStarted() && event.getRecipe().getResult().getType() == Material.COMPASS) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        //TODO check world too
        EntityType entity = event.getEntity().getType();
        Player killer = event.getEntity().getKiller();
        if (ManHunt.getInstance().hasGameStarted() && entity == EntityType.ENDER_DRAGON) {
            if (ManHunt.getInstance().getRunners().contains(killer.getUniqueId())) {
                //TODO end the game and make particle effects(spiral or sth. like that) for the runner that won
                Bukkit.broadcastMessage(event.getEntity().getKiller().getName() + " Won as runner!");
            } else if (ManHunt.getInstance().getHunters().contains(killer.getUniqueId())) {
                Bukkit.broadcastMessage(event.getEntity().getKiller().getName() + " Won as hunter!");
                //TODO end the game and make sad particle effects for the runners and send titles that hunters won
            }
        }
    }

    //TODO fix the things when you can put compass to chest or dispensers
}
