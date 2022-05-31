package gg.matthew.commands;

import gg.matthew.core.ManHunt;
import gg.overcast.api.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Stop extends SubCommand {
    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Stops current manhunt game";
    }

    @Override
    public String getSyntax() {
        return "/manhunt stop";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (ManHunt.getInstance().hasGameStarted()) {
                ManHunt.getInstance().setGameStopped();
                ManHunt.getInstance().disableUtilityThingsForCurrentGame();
                Bukkit.broadcastMessage(ChatColor.RED + "ManHunt game has been stopped!");
            } else {
                sender.sendMessage(ChatColor.RED + "ManHunt game isn't running right now!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Incorrect usage of command use: " + ChatColor.GRAY + getSyntax());
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
