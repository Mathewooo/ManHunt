package gg.matthew;

import gg.matthew.commands.manage.Start;
import gg.matthew.commands.manage.Stop;
import gg.matthew.core.particle.Particle;
import gg.matthew.event.Events;
import gg.overcast.api.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    //TODO implement log system of won games (with json storage)
    //TODO add interesting particle effect for winner
    //TODO implement permissions for stop and start command
    //TODO implement game freezer for configurable amount of time when one of the runnners quits
    //TODO implement config

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    private void setInstance(Main main) {
        Main.instance = main;
    }

    @Override
    public void onEnable() {
        setInstance(this);
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        try {
            CommandManager.createCoreCommand(this, "manhunt", "All commands needed for manhunt game", "/manhunt", (sender, subCommandList) -> {
                sender.sendMessage("------------");
                subCommandList.forEach(subCommand -> sender.sendMessage(ChatColor.GRAY + subCommand.getSyntax() + ChatColor.WHITE + " - " + ChatColor.BOLD + subCommand.getDescription()));
                sender.sendMessage("------------");
            }, Start.class, Stop.class);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
        Particle.getInstance().cacheWinnerEffect();
    }
}
