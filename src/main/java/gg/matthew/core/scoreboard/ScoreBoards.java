package gg.matthew.core.scoreboard;

import gg.matthew.core.ManHunt;
import gg.matthew.core.players.model.Hunter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;

import java.util.UUID;

public class ScoreBoards {
    private static ScoreBoards instance;
    Scoreboard board;
    String displayName = ChatColor.UNDERLINE.toString() + ChatColor.BOLD + "Manhunt Game";
    int scoreIndex;

    boolean[] booleans = new boolean[]{true, false};

    public static synchronized ScoreBoards getInstance() {
        if (instance == null) instance = new ScoreBoards();
        return instance;
    }

    private void initializeBoard(boolean hunters, UUID uuid) {
        scoreIndex = ManHunt.getInstance().getMerged().size() + 2;
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("Manhunt", "win", displayName);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        if (hunters) formatScoreboard(objective, "hunters", ChatColor.RED);
        else formatScoreboard(objective, "runners", ChatColor.WHITE);
        scoreIndex = 0;
        Bukkit.getPlayer(uuid).setScoreboard(board);
    }

    private void formatScoreboard(Objective objective, String team, ChatColor color) {
        Score score = objective.getScore(color + team.substring(0, 1).toUpperCase() + team.substring(1));
        score.setScore(scoreIndex);
        Team gameTeam = board.registerNewTeam(team + "Team");
        gameTeam.addEntry(team);
        if (team.equals("hunters")) {
            for (boolean booleanValue : booleans) {
                generateState(gameTeam, booleanValue);
            }
        } else {
            for (int index = booleans.length - 1; index >= 0; index--) {
                generateState(gameTeam, booleans[index]);
            }
        }
        objective.getScore(team).setScore(scoreIndex);
    }

    private void generateState(Team team, boolean hunters) { // state "\\lives and etc. or if player is dead"
        if (hunters) for (Hunter hunter : ManHunt.getInstance().getHunters()) {
            team.setPrefix(ChatColor.LIGHT_PURPLE.toString() + Bukkit.getPlayer(hunter.getPlayerId()) + " " + ChatColor.GRAY + ChatColor.UNDERLINE + hunter.getLives());
            scoreIndex--;
        }
        else for (UUID uuid : ManHunt.getInstance().getRunners()) {
            team.setPrefix(ChatColor.LIGHT_PURPLE.toString() + Bukkit.getPlayer(uuid) + " " + ChatColor.GRAY + ChatColor.UNDERLINE + (Bukkit.getPlayer(uuid).isDead() ? "Dead" : "Alive"));
            scoreIndex--;
        }
    }

    public void createScoreBoards() {
        ManHunt.getInstance().getMerged().forEach(player -> {
            if (ManHunt.getInstance().returnFilteredHunters().contains(player)) {
                initializeBoard(true, player);
            } else if (ManHunt.getInstance().getRunners().contains(player)) {
                initializeBoard(false, player);
            }
            Bukkit.getPlayer(player).setScoreboard(board);
        });
    }

    public void updateScoreBoards() { // state "\\lives and etc. or if player is dead"
    }
}


