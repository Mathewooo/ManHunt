package gg.matthew.event;

import gg.matthew.core.ManHunt;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        Player player = Bukkit.getPlayer(event.getWhoClicked().getName());
        player.sendMessage(String.valueOf(event.getRecipe().getResult())); //remove this afterwards
        if ( ManHunt.getInstance().hasGameStarted() && event.getRecipe().getResult().getType() == Material.COMPASS) {
            event.setCancelled(true);
        }
    }
}
