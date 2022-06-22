package gg.matthew.commands.manage;

import gg.matthew.core.ManHunt;
import gg.matthew.core.players.pregame.PreGame;
import gg.overcast.api.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

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
        Player player = Bukkit.getPlayer(sender.getName());
        if (PreGame.getInstance().hasPreGameCommand(player.getUniqueId()) && PreGame.getInstance().returnPreGameCommand(player.getUniqueId()) != null) {
            if (!PreGame.getInstance().areInterfiereing(player.getUniqueId())) {
                ManHunt.getInstance().setGameStarted();
                ManHunt.getInstance().startGame();
                PreGame.getInstance().removePreGames(player.getUniqueId());
                Bukkit.broadcastMessage(ChatColor.WHITE + "ManHunt" + ChatColor.GRAY + " game has started" + ChatColor.WHITE + "!");
            }
        } else player.sendMessage(ChatColor.RED + "You haven't set both hunters and runners!");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
