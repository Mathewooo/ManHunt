package gg.matthew.core.scoreboard;

import gg.matthew.core.ManHunt;
import gg.matthew.core.players.model.Hunter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//TODO !!!!fix all of the errors with the help of the lectures (lectures: 60,61)
public class ScoreBoards {
    //!!! Scoreboards don't update and these are the problems: hunter's view(https://gyazo.com/abad3c76bb79ae3a1aa9ffd01042107a); runner's view(https://gyazo.com/35573a71d22c470fc65b968af6dc0f29)
    private static ScoreBoards instance;
    Map<String, Scoreboard> scoreboards = new HashMap<>();
    String displayName = ChatColor.UNDERLINE.toString() + ChatColor.BOLD + "Manhunt Game";
    int scoreIndex;
    boolean[] booleans = new boolean[]{true, false};
    Map<String, ChatColor> teamTypes = new HashMap<>();

    public static synchronized ScoreBoards getInstance() {
        if (instance == null) instance = new ScoreBoards();
        return instance;
    }

    private void initializeBoards() {
        for (Map.Entry<String, ChatColor> type : teamTypes.entrySet()) {
            scoreIndex = ManHunt.getInstance().getMerged().size() + 2;
            Bukkit.getLogger().info(String.valueOf(scoreIndex)); //
            scoreboards.put(type.getKey(), Bukkit.getScoreboardManager().getNewScoreboard());
            Objective objective = scoreboards.get(type.getKey()).registerNewObjective("Manhunt", "win", displayName);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            formatScoreboard(objective, scoreboards.get(type.getKey()), type.getKey(), type.getValue());
            scoreIndex = 0;
        }
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
            setPrefixes(objective, returnTeam(board, team, hunter.getPlayerId()), "hunters", Bukkit.getPlayer(hunter.getPlayerId()).getName() + " " + ChatColor.GRAY + hunter.getLives());
            Bukkit.getLogger().info(String.valueOf(scoreIndex)); //
            scoreIndex--;
        }
        else for (UUID uuid : ManHunt.getInstance().getRunners()) {
            setPrefixes(objective, returnTeam(board, team, uuid), "runners", ChatColor.LIGHT_PURPLE + Bukkit.getPlayer(uuid).getName() + ChatColor.GRAY + " Alive");
            Bukkit.getLogger().info(String.valueOf(scoreIndex)); //
            scoreIndex--;
        }
    }

    private void setPrefixes(Objective objective, Team team, String teamEntry, String prefix) {
        objective.getScore(teamEntry).setScore(scoreIndex);
        team.setPrefix(prefix);
    }

    private void setPrefix(Team team, String prefix) {
        team.setPrefix(prefix);
    }

    private Team returnTeam(Scoreboard board, String team, UUID uuid) {
        Team gameTeam = board.registerNewTeam(team + ":" + uuid);
        gameTeam.addEntry(team);
        return gameTeam;
    }

    private void setScoreboard(UUID uuid, boolean hunters) {
        if (hunters) Bukkit.getPlayer(uuid).setScoreboard(scoreboards.get("hunters"));
        else Bukkit.getPlayer(uuid).setScoreboard(scoreboards.get("runners"));
    }

    public void createScoreBoards() {
        teamTypes = initializeTypes();
        initializeBoards();
        ManHunt.getInstance().getMerged().forEach(player -> {
            if (ManHunt.getInstance().returnFilteredHunters().contains(player)) {
                setScoreboard(player, true);
            } else if (ManHunt.getInstance().getRunners().contains(player)) {
                setScoreboard(player, false);
            }
        });
    }

    public void removeScoreBoards() { //TODO scoreboards don't remove !!!
        ManHunt.getInstance().getMerged().forEach(player -> {
            if (Bukkit.getPlayer(player).getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null)
                Bukkit.getPlayer(player).getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
        });
        scoreboards.clear();
    }

    private Map<String, ChatColor> initializeTypes() { // make the colors and stuff hex and configurable later
        Map<String, ChatColor> types = new HashMap<>();
        types.put("hunters", ChatColor.RED);
        types.put("runners", ChatColor.WHITE);
        return types;
    }

    public void updateScoreBoards(Player player) { // state "\\lives and etc. or if player is dead - left or freezed"
        UUID uuid = player.getUniqueId();
        for (Map.Entry<String, Scoreboard> scoreboard : scoreboards.entrySet()) {
            if (ManHunt.getInstance().returnFilteredHunters().contains(uuid)) {
                int lives = ManHunt.getInstance().returnHunterObject(uuid).getLives();
                setPrefix(scoreboard.getValue().getTeam("hunters" + uuid), ChatColor.LIGHT_PURPLE.toString() + Bukkit.getPlayer(ManHunt.getInstance().returnHunterObject(uuid).getPlayerId()) + " " + ChatColor.GRAY + (lives == 0 ? "Dead" : lives));
            } else if (ManHunt.getInstance().getRunners().contains(uuid))
                setPrefix(scoreboard.getValue().getTeam("runners" + uuid), ChatColor.LIGHT_PURPLE.toString() + Bukkit.getPlayer(uuid) + " " + ChatColor.GRAY + "Dead");
        }
    }
}


