package gg.matthew.core.scoreboard;

import gg.matthew.core.ManHunt;
import gg.matthew.core.players.pregame.model.Hunter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.*;
import java.util.UUID;

//TODO !!!!fix all of the errors with the help of the lectures (lectures: 60,61)
public class ScoreBoards {
    //!!! Scoreboards don't update and these are the problems: hunter's view(https://gyazo.com/abad3c76bb79ae3a1aa9ffd01042107a); runner's view(https://gyazo.com/35573a71d22c470fc65b968af6dc0f29)
    private static ScoreBoards instance;
    String displayName = ChatColor.UNDERLINE.toString() + ChatColor.BOLD + "Manhunt Game";
    int scoreIndex;
    boolean[] booleans = new boolean[]{true, false};
    String splitter = ":";

    public static synchronized ScoreBoards getInstance() {
        if (instance == null) instance = new ScoreBoards();
        return instance;
    }

    private void initializePlayerBoards() { // make the colors and stuff hex and configurable later
        ManHunt.getInstance().returnFilteredHunters().forEach(hunter -> initialize(Bukkit.getPlayer(hunter).getScoreboard(), "hunters", ChatColor.RED));
        ManHunt.getInstance().getRunners().forEach(runner -> initialize(Bukkit.getPlayer(runner).getScoreboard(), "runners", ChatColor.WHITE));
    }

    private void initialize(Scoreboard board, String team, ChatColor color) {
        scoreIndex = ManHunt.getInstance().getMerged().size() + 2;
        Bukkit.getLogger().info(String.valueOf(scoreIndex)); //
        Objective objective = board.registerNewObjective("Manhunt", "win", displayName);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        formatScoreboard(objective, board, team, color);
        scoreIndex = 0;
    }

    private void formatScoreboard(Objective objective, Scoreboard board, String team, ChatColor color) { //TODO !!! fix this problem with scoreboards https://gyazo.com/ac41d92082364447927f3e3c8ff0918d
        Score score = objective.getScore(color + team.substring(0, 1).toUpperCase() + team.substring(1));
        Bukkit.getLogger().info(String.valueOf(scoreIndex)); //
        score.setScore(scoreIndex);
        Team gameTeam = board.registerNewTeam(team + "Team");
        gameTeam.addEntry(team);
        if (team.equals("hunters")) for (boolean booleanValue : booleans)
            generateState(board, objective, team, booleanValue);
        else for (int index = booleans.length - 1; index >= 0; index--)
            generateState(board, objective, team, booleans[index]);
        objective.getScore(team).setScore(scoreIndex);
    }

    private void generateState(Scoreboard board, Objective objective, String team, boolean hunters) { // state "\\lives and etc. or if player is dead"
        if (hunters) for (Hunter hunter : ManHunt.getInstance().getHunters()) {
            setPrefixes(objective, returnTeam(board, team, hunter.getPlayerId()), team + hunter.getPlayerId(), Bukkit.getPlayer(hunter.getPlayerId()).getName() + " " + ChatColor.GRAY + hunter.getLives());
            Bukkit.getLogger().info(String.valueOf(scoreIndex)); //
            scoreIndex--;
        } else for (UUID uuid : ManHunt.getInstance().getRunners()) {
            setPrefixes(objective, returnTeam(board, team, uuid), team + uuid, ChatColor.LIGHT_PURPLE + Bukkit.getPlayer(uuid).getName() + ChatColor.GRAY + " Alive");
            Bukkit.getLogger().info(String.valueOf(scoreIndex)); //
            scoreIndex--;
        }
    }

    private void setPrefixes(Objective objective, Team team, String teamEntry, String prefix) {
        setPrefix(team, prefix);
        objective.getScore(teamEntry).setScore(scoreIndex);
    }

    private void setPrefix(Team team, String prefix) {
        team.setPrefix(prefix);
    }

    private Team returnTeam(Scoreboard board, String team, UUID uuid) {
        Team gameTeam = board.registerNewTeam(team + splitter + uuid);
        gameTeam.addEntry(team + uuid);
        return gameTeam;
    }

    public void createScoreBoards() {
        initializePlayerBoards();
    }

    public void removeScoreBoards() { //TODO scoreboards don't remove !!!
        ManHunt.getInstance().getMerged().forEach(player -> {
            if (Bukkit.getPlayer(player).getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null)
                Bukkit.getPlayer(player).getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
        });
    }

    //!!! GO by this: https://www.spigotmc.org/threads/how-do-i-update-the-scoreboard.398009/
    private void updateStates(UUID uuid) {
        updateScore(true, uuid);
        updateScore(false, uuid);
    }

    private void updateScore(boolean hunters, UUID player) {
        if (hunters) for (UUID uuid : ManHunt.getInstance().returnFilteredHunters()) {
            int lives = ManHunt.getInstance().returnHunterObject(uuid).getLives();
            Score score = Bukkit.getPlayer(player).getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.LIGHT_PURPLE.toString() + Bukkit.getPlayer(ManHunt.getInstance().returnHunterObject(uuid).getPlayerId()) + " " + ChatColor.GRAY + (lives == 0 ? "Dead" : lives));
            score.setScore(findScore(uuid, player, "hunters"));
        } else for (UUID uuid : ManHunt.getInstance().getRunners()) {
            Score score = Bukkit.getPlayer(player).getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.LIGHT_PURPLE.toString() + Bukkit.getPlayer(uuid) + " " + ChatColor.GRAY + "Dead");
            score.setScore(findScore(uuid, player, "runners"));
        }
    }

    private int findScore(UUID uuid, UUID player, String type) {
        String locator = type + splitter + player;
        Scoreboard board = Bukkit.getPlayer(uuid).getScoreboard();
        Score score = board.getObjective(DisplaySlot.SIDEBAR).getScore(locator);
        board.resetScores(score.getEntry());
        return score.getScore();
    }
}