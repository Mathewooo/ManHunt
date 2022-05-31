package gg.matthew.core.utils;

import gg.matthew.core.ManHunt;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Utils {
    public static Player isPlayerOnline(String name) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equals(name)) return player;
        }
        return null;
    }

    public static Player getNearestPlayer(Player checkNear) {
        Player nearest = null;
        for (Player player : checkNear.getWorld().getPlayers()) {
            if (ManHunt.getInstance().getRunners().contains(player.getUniqueId())) {
                if (nearest == null) nearest = player;
                else if (player.getLocation().distance(checkNear.getLocation()) < nearest.getLocation().distance(checkNear.getLocation()))
                    nearest = player;
            }
        }
        return nearest;
    }
}
