package gg.matthew.commands.manage.players;

import gg.matthew.core.players.pregame.PreGame;
import gg.matthew.core.utils.Utils;
import gg.overcast.api.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Runners extends SubCommand {
    @Override
    public String getName() {
        return "runners";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Adds runners to the game";
    }

    @Override
    public String getSyntax() {
        return "/manhunt runners (player) (player) ...";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            boolean canContinue = true;
            List<String> argsPlayers = new ArrayList<>();
            Arrays.stream(args).forEach(argsPlayer -> {
                if (!argsPlayers.contains(argsPlayer) && !Objects.equals(argsPlayer, getName()))
                    argsPlayers.add(argsPlayer);
            });
            for (String player : argsPlayers)
                if (Utils.isPlayerOnline(player) == null) {
                    canContinue = false;
                    sender.sendMessage(ChatColor.RED + (argsPlayers.size() == 1 ? "Entered player isn't online!" : "One of the entered players isn't online!"));
                    break;
                }
            if (canContinue)
                PreGame.getInstance().createPreGame(Bukkit.getPlayer(sender.getName()).getUniqueId(), argsPlayers);
        } else sender.sendMessage(ChatColor.RED + "Please reference at least one player as runner!");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return Utils.returnTabCompletePlayers(player.getUniqueId(), false);
    }
}

