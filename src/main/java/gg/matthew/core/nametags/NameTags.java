package gg.matthew.core.nametags;

import gg.matthew.core.ManHunt;
import gg.matthew.core.Teams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
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
        createNameTags(ManHunt.getInstance().returnFilteredHunters(), Teams.HUNTER.getTeamName());
        createNameTags(ManHunt.getInstance().getRunners(), Teams.RUNNER.getTeamName());
    }

    //!!! nametags doens't work because player cannot have multiple scoreboards set to them!! potential fix is where the code's marked with "//"
    public void createNameTags(List<UUID> map, String teamName) {
        for (UUID uuid : map) {
            Player player = Bukkit.getPlayer(uuid);
            Scoreboard scoreBoard = Bukkit.getScoreboardManager().getNewScoreboard();//
            Objective objective = scoreBoard.registerNewObjective(String.valueOf(uuid), "none", "");//
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);//
            player.setScoreboard(scoreBoard);//
            for (Teams team : Teams.values()) {
                Team scoreboardTeam = player.getScoreboard().registerNewTeam(team.getTeamName());
                if (!team.getTeamName().equals(Teams.RUNNER.getTeamName())) scoreboardTeam.setColor(ChatColor.WHITE);
                scoreboardTeam.setPrefix(team.getPrefix() + ChatColor.RESET);
            }
            for (Player target : Bukkit.getOnlinePlayers())
                if (player.getUniqueId() != target.getUniqueId())
                    player.getScoreboard().getTeam(teamName).addEntry(target.getName());
        }
    }

    public void newTags() {
        for (UUID uuid : ManHunt.getInstance().getMerged())
            for (Player target : Bukkit.getOnlinePlayers())
                if (ManHunt.getInstance().returnFilteredHunters().contains(uuid))
                    target.getScoreboard().getTeam(Teams.HUNTER.getTeamName()).addEntry(Bukkit.getPlayer(uuid).getName());
                else
                    target.getScoreboard().getTeam(Teams.RUNNER.getTeamName()).addEntry(Bukkit.getPlayer(uuid).getName());
    }

    public void removeTags() {
        for (UUID uuid : ManHunt.getInstance().getMerged()) {
            Player player = Bukkit.getPlayer(uuid);
            for (Player target : Bukkit.getOnlinePlayers()) {
                Team team = target.getScoreboard().getEntryTeam(player.getName());
                if (team == null) {
                    teamNullError();
                    return;
                }
                team.removeEntry(player.getName());
            }
        }
    }

    public void removeTag(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        for (Player target : Bukkit.getOnlinePlayers()) {
            Team team = target.getScoreboard().getEntryTeam(player.getName());
            if (team == null) {
                teamNullError();
                return;
            }
            team.removeEntry(player.getName());
        }
    }

    private void teamNullError() {
        Bukkit.getLogger().severe("Error: " + "team was not found! (Please contact developer)");
    }
}
