package gg.matthew.core.nametags;

import gg.matthew.core.ManHunt;
import gg.matthew.core.Teams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.UUID;

public class NameTags {
    private static NameTags instance;

    public static synchronized NameTags getInstance() {
        if (instance == null) instance = new NameTags();
        return instance;
    }

    public void setNameTags() {
        createNameTags(ManHunt.getInstance().getHunters(), Teams.HUNTER.getTeamName());
        createNameTags(ManHunt.getInstance().getRunners(), Teams.RUNNER.getTeamName());
    }

    public void createNameTags(List<UUID> map, String teamName) {
        for (UUID uuid : map) {
            Player player = Bukkit.getPlayer(uuid);
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            for (Teams team : Teams.values()) {
                Team scoreboardTeam = player.getScoreboard().registerNewTeam(team.getTeamName());
                if (!team.getTeamName().equals(Teams.RUNNER.getTeamName())) scoreboardTeam.setColor(ChatColor.WHITE);
                scoreboardTeam.setPrefix(team.getPrefix() + ChatColor.RESET);
            }
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (player.getUniqueId() != target.getUniqueId())
                    player.getScoreboard().getTeam(teamName).addEntry(target.getName());
            }
        }
    }

    public void newTags() {
        for (UUID uuid : ManHunt.getInstance().getMerged()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (ManHunt.getInstance().getHunters().contains(uuid))
                    target.getScoreboard().getTeam(Teams.HUNTER.getTeamName()).addEntry(Bukkit.getPlayer(uuid).getName());
                else
                    target.getScoreboard().getTeam(Teams.RUNNER.getTeamName()).addEntry(Bukkit.getPlayer(uuid).getName());
            }
        }
    }

    public void removeTags() {
        for (UUID uuid : ManHunt.getInstance().getMerged()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                Team team = target.getScoreboard().getEntryTeam(Bukkit.getPlayer(uuid).getName());
                if (team == null) {
                    Bukkit.getLogger().severe("Error: " + "team was not found! (Please contact developer)");
                    return;
                }
                team.removeEntry(Bukkit.getPlayer(uuid).getName());
            }
        }
    }
}
