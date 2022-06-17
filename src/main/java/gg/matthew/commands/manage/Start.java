package gg.matthew.commands.manage;

import gg.matthew.core.ManHunt;
import gg.matthew.core.players.model.Hunter;
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
        return "Starts a ManHunt game";
    }

    @Override
    public String getSyntax() {
        return "/manhunt start";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        //TODO make this done with the new system
        ManHunt.getInstance().setGameStarted();
        ManHunt.getInstance().startGame();
        Bukkit.broadcastMessage(ChatColor.WHITE + "ManHunt" + ChatColor.GRAY + " game has started" + ChatColor.WHITE + "!");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
