package gg.matthew.core.players.pregame;

import gg.matthew.core.players.pregame.model.Command;
import gg.matthew.core.players.pregame.model.Hunter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class PreGame {
    private static PreGame instance;
    public Vector<Command> preGameCommands = new Vector<>();

    public static synchronized PreGame getInstance() {
        if (instance == null) instance = new PreGame();
        return instance;
    }

    @SuppressWarnings("unchecked")
    public <Type> void createPreGame(UUID uuid, List<Type> list) {
        if (!hasPreGameCommand(uuid)) {
            Command.Builder commandBuilder = new Command.Builder();
            preGameCommands.add(commandBuilder.setPlayerId(uuid).build());
        }
        for (Command command : preGameCommands)
            if (command.getPlayerId().equals(uuid))
                if (list.get(0) instanceof Hunter) command.setHunters((List<Hunter>) list);
                else command.setRunners((List<UUID>) list);
    }

    public void removePreGames(UUID uuid) {
        for (Command command : preGameCommands)
            if (!command.getPlayerId().equals(uuid) && returnPreGameCommand(command.getPlayerId()) != null)
                Bukkit.getPlayer(command.getPlayerId()).sendMessage(ChatColor.RED + "Your command storage for manhunt game was erased because game has started");
        preGameCommands.clear();
    }

    //TODO check if this works!!!
    public boolean areInterfering(UUID uuid) {
        boolean bool = false;
        Command currentCommand = null;
        for (Command command : preGameCommands)
            if (command.getPlayerId().equals(uuid)) currentCommand = command;
        int index = 0;
        for (Hunter hunter : currentCommand.getHunters())
            if (currentCommand.getRunners().contains(hunter.getPlayerId())) {
                bool = true;
                index++;
            }
        if (index != 0) {
            Bukkit.getPlayer(uuid).sendMessage(ChatColor.RED + "Couldn't start the game because you've set " + (index == 1 ? index + "player" : index + "players") + " as both runners and hunters!");
            preGameCommands.remove(currentCommand);
        }
        return bool;
    }

    public boolean hasPreGameCommand(UUID uuid) {
        for (Command command : preGameCommands)
            if (command.getPlayerId().equals(uuid)) return true;
        return false;
    }

    public Command returnPreGameCommand(UUID uuid) {
        for (Command command : preGameCommands)
            if (command.getPlayerId().equals(uuid))
                if (!command.getHunters().isEmpty() && !command.getRunners().isEmpty()) return command;
        return null;
    }
}
