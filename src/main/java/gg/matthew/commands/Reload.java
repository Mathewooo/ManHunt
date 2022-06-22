package gg.matthew.commands;

import gg.matthew.Main;
import gg.overcast.api.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Reload extends SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Reloads Manhunt plugin";
    }

    @Override
    public String getSyntax() {
        return "/manhunt reload";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (Bukkit.getPlayer(sender.getName()).isOp()) {
            Main.getInstance().reloadConfig();
        } else Bukkit.getPlayer(sender.getName()).sendMessage(ChatColor.RED + "You don't have permission to use this command!");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
