package gg.matthew;

import gg.matthew.commands.Reload;
import gg.matthew.commands.manage.Start;
import gg.matthew.commands.manage.Stop;
import gg.matthew.commands.manage.players.Hunters;
import gg.matthew.commands.manage.players.Runners;
import gg.matthew.core.particle.ParticleEffects;
import gg.overcast.api.command.CommandManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    //TODO implement log system of won games (with json storage)
    //TODO add interesting particle effect for winner
    //TODO implement permissions for stop and start command
    //TODO implement game freezer for configurable amount of time when one of the runnners quits
    //TODO implement config
    //TODO implement worlds resetting
    //TODO implement countdown before before game starts at least 20 seconds default

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
        try {
            CommandManager.createCoreCommand(this, "manhunt", "All commands needed for manhunt game", "/manhunt", (sender, subCommandList) -> {
                sender.sendMessage("------------");
                subCommandList.forEach(subCommand -> sender.sendMessage(ChatColor.GRAY + subCommand.getSyntax() + ChatColor.WHITE + " - " + ChatColor.BOLD + subCommand.getDescription()));
                sender.sendMessage("------------");
            }, Hunters.class, Runners.class, Start.class, Stop.class, Reload.class);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
        ParticleEffects.getInstance().cacheEffects();
    }
}
