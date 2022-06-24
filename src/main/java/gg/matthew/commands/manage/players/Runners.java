package gg.matthew.commands.manage.players;

import gg.matthew.core.ManHunt;
import gg.matthew.core.players.pregame.PreGame;
import gg.matthew.core.utils.Utils;
import gg.overcast.api.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
        if (!ManHunt.getInstance().hasGameStarted()) {
            if (args.length > 1) {
                boolean canContinue = true;
                List<UUID> argsPlayers = new ArrayList<>();
                for (String argsPlayer : args)
                    if (!Objects.equals(argsPlayer, getName()))
                        if (Utils.isPlayerOnline(Bukkit.getPlayer(argsPlayer).getName()) != null) {
                            if (!argsPlayers.contains(Bukkit.getPlayer(argsPlayer).getUniqueId()))
                                argsPlayers.add(Bukkit.getPlayer(argsPlayer).getUniqueId());
                        } else {
                            canContinue = false;
                            sender.sendMessage(ChatColor.RED + (argsPlayers.size() == 1 ? "Entered player isn't online!" : "One of the entered players isn't online!"));
                            break;
                        }
                if (canContinue) {
                    PreGame.getInstance().createPreGame(Bukkit.getPlayer(sender.getName()).getUniqueId(), argsPlayers);
                    sender.sendMessage(ChatColor.GREEN + "Successfully added runners.");
                }
            } else sender.sendMessage(ChatColor.RED + "Please reference at least one player as runner!");
        } else sender.sendMessage(ChatColor.RED + "Manhunt game is currently running!");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return Utils.returnTabCompletePlayers();
    }
}

