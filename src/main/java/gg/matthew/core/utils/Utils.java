package gg.matthew.core.utils;

import gg.matthew.core.ManHunt;
import gg.matthew.core.particle.armorstand.Circle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

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

    public static List<String> returnTabCompletePlayers() {
        Vector<String> onlinePlayers = new Vector<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            onlinePlayers.add(onlinePlayer.getName());
        return onlinePlayers;
    }

    public static void endGame(Player killer, String type) {
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

    public static ItemStack returnPlayerTracker(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents())
            if (itemStack != null)
                if (Objects.equals(itemStack.getItemMeta().getPersistentDataContainer().get(ManHunt.getInstance().getKey(), PersistentDataType.STRING), "tracker"))
                    return itemStack;
        if (player.getInventory().getItemInOffHand() != null)
            if (Objects.equals(player.getInventory().getItemInOffHand().getItemMeta().getPersistentDataContainer().get(ManHunt.getInstance().getKey(), PersistentDataType.STRING), "tracker"))
                return player.getInventory().getItemInOffHand();
        return null;
    }
}
