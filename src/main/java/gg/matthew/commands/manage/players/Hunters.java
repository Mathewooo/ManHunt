package gg.matthew.commands.manage.players;

import gg.matthew.core.players.pregame.PreGame;
import gg.matthew.core.players.pregame.model.Hunter;
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

public class Hunters extends SubCommand {
    @Override
    public String getName() {
        return "hunters";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Adds hunters to the game";
    }

    @Override
    public String getSyntax() {
        return "/manhunt hunters (player) (player) ...";
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
            if (canContinue) {
                List<Hunter> hunters = new ArrayList<>();
                Hunter.Builder hunterBuilder = new Hunter.Builder();
                for (String player : argsPlayers)
                    hunters.add(hunterBuilder.setPlayerId(Bukkit.getPlayer(player).getUniqueId()).setLives(4).build());
                PreGame.getInstance().createPreGame(Bukkit.getPlayer(sender.getName()).getUniqueId(), hunters);
            }
        } else sender.sendMessage(ChatColor.RED + "Please reference at least one player as hunter!");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return Utils.returnTabCompletePlayers(player.getUniqueId(), true);
    }
}
