package gg.matthew.core;

import org.bukkit.ChatColor;

public enum Teams {
    HUNTER("hunter", ChatColor.RED + "Hunter "), RUNNER("runner", ChatColor.GRAY + "Runner ");

    private final String teamName;
    private final String prefix;

    Teams(String teamName, String prefix) {
        this.teamName = teamName;
        this.prefix = prefix;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getPrefix() {
        return prefix;
    }
}
