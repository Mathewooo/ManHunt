package gg.matthew.core.players;

import org.bukkit.entity.Player;

public class Spectating {
    private static Spectating instance;

    public static synchronized Spectating getInstance() {
        if (instance == null) instance = new Spectating();
        return instance;
    }

    public void startSpectate(Player player) {

    }
}
