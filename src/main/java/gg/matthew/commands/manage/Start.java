package gg.matthew.commands.manage;

import gg.matthew.core.ManHunt;
import gg.matthew.core.utils.Utils;
import gg.overcast.api.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class Start extends SubCommand {
    @Override
    public String getName() {
        return "start";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Starts ManHunt game with a runners desired from a command arguments";
    }

    @Override
    public String getSyntax() {
        return "/manhunt start (player) (player) ...";
    }

    //Cancel that you can choose all players from server as runners
    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            if (!(Bukkit.getOnlinePlayers().size() == 1)) {
                if (!ManHunt.getInstance().hasGameStarted()) {
                    boolean canContinue = true;
                    List<String> argsPlayers = new ArrayList<>();
                    Arrays.stream(args).forEach(argsPlayer -> {
                        if (!argsPlayers.contains(argsPlayer) && !Objects.equals(argsPlayer, getName()))
                            argsPlayers.add(argsPlayer);
                    });
                    for (String player : argsPlayers) {
                        if (Utils.isPlayerOnline(player) == null) {
                            canContinue = false;
                            sender.sendMessage(ChatColor.RED + (argsPlayers.size() == 1 ? "Entered player isn't online!" : "One of the players entered isn't online!"));
                            break;
                        }
                    }
                    if (canContinue) {
                        List<String> hunters = new ArrayList<>();
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (!argsPlayers.contains(player.getName())) hunters.add(player.getName());
                        }
                        ManHunt.getInstance().setGameStarted();
                        ManHunt.getInstance().setRunners(argsPlayers);
                        ManHunt.getInstance().setHunters(hunters);
                        ManHunt.getInstance().startGame();
                        Bukkit.broadcastMessage(ChatColor.WHITE + "ManHunt" + ChatColor.GRAY + " game has started" + ChatColor.WHITE + "!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "ManHunt game is currently running!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You cannot start a ManHunt game just with yourself!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Please reference at least one player as runner to start game!");
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        Vector<String> onlinePlayers = new Vector<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayers.add(onlinePlayer.getName());
        }
        return onlinePlayers;
    }
}
