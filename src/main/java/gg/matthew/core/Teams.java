package gg.matthew.core;

import org.bukkit.ChatColor;

public enum Teams {
    HUNTER("hunter", ChatColor.RED + "HUNTER " + ChatColor.WHITE, ChatColor.RED), RUNNER("runner", ChatColor.GRAY + "RUNNER " + ChatColor.WHITE, ChatColor.GRAY);

    private final String teamName;
    private final String prefix;

    private final ChatColor color;

    Teams(String teamName, String prefix, ChatColor color) {
        this.teamName = teamName;
        this.prefix = prefix;
        this.color = color;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getPrefix() {
        return prefix;
    }

    public ChatColor getColor() {
        return color;
    }
}
