package gg.matthew.core.utils;

import gg.matthew.core.ManHunt;
import gg.matthew.core.players.pregame.PreGame;
import gg.matthew.core.players.pregame.model.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class Utils {
    public static Player isPlayerOnline(String name) {
        for (Player player : Bukkit.getOnlinePlayers())
            if (player.getName().equals(name)) return player;
        return null;
    }

    public static Player getNearestPlayer(Player checkNear) {
        Player nearest = null;
        for (Player player : checkNear.getWorld().getPlayers()) {
            if (player != checkNear) if (ManHunt.getInstance().returnFilteredHunters().contains(player.getUniqueId()))
                if (nearest == null) nearest = player;
                else if (player.getLocation().distance(checkNear.getLocation()) < nearest.getLocation().distance(checkNear.getLocation()))
                    nearest = player;
        }
        return nearest;
    }

    public static double[] addElement(double[] a, double b) {
        if (a != null) {
            a = Arrays.copyOf(a, a.length + 1);
            a[a.length - 1] = b;
        }
        return a;
    }

    public static void sendTitles(List<UUID> list, String title, String subtitle, boolean except, UUID exceptPlayer) {
        for (UUID uuid : list)
            if (except && exceptPlayer != null) {
                if (uuid != exceptPlayer) sendTitle(uuid, title, subtitle);
                else sendTitle(uuid, title, "");
            } else sendTitle(uuid, title, subtitle);
    }

    public static void sendTitle(UUID uuid, String title, String subtitle) {
        Bukkit.getPlayer(uuid).sendTitle(title, subtitle, 15, 40, 15);
    }

    public static List<String> returnTabCompletePlayers(UUID uuid, boolean hunters) {
        Vector<String> onlinePlayers = new Vector<>();
        Command preGameCommand = null;
        if (PreGame.getInstance().hasPreGameCommand(uuid))
            preGameCommand = PreGame.getInstance().returnPreGameCommand(uuid);
        if (preGameCommand != null) {
            if (hunters) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers())
                    if (!preGameCommand.getRunners().contains(onlinePlayer.getUniqueId()))
                        onlinePlayers.add(onlinePlayer.getName());
            } else for (Player onlinePlayer : Bukkit.getOnlinePlayers())
                if (!PreGame.getInstance().returnCommandFilteredHunters(preGameCommand).contains(onlinePlayer.getUniqueId()))
                    onlinePlayers.add(onlinePlayer.getName());
        } else for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            onlinePlayers.add(onlinePlayer.getName());
        return onlinePlayers;
    }
}
