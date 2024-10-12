package org.example.cyveplay.plugins;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

public class ScoreboardManager {

    private final Main plugin;

    // Konstruktor, um das Plugin zu referenzieren
    public ScoreboardManager(Main plugin) {
        this.plugin = plugin;
        startScoreboardUpdateTask(); // Startet das regelmäßige Update
    }

    // Methode, die das Scoreboard für einen Spieler erstellt
    public void createScoreboard(Player player) {
        // Hole den ScoreboardManager von Bukkit
        org.bukkit.scoreboard.ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

        // Erstelle ein neues Scoreboard
        Scoreboard scoreboard = null;
        if (scoreboardManager != null) {
            scoreboard = scoreboardManager.getNewScoreboard();
        }

        // Erstelle ein neues Objective für das Scoreboard
        Objective objective = scoreboard.registerNewObjective("Geld", "dummy", ChatColor.GOLD + "Dein Geld");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Setze den Score (das Guthaben des Spielers)
        Score score = objective.getScore(ChatColor.GREEN + "Guthaben:");
        score.setScore((int) plugin.getMoneyManager().getMoney(player.getUniqueId()));

        // Weise dem Spieler das erstellte Scoreboard zu
        player.setScoreboard(scoreboard);
    }

    // Startet einen regelmäßigen Task, der das Scoreboard für jeden Spieler aktualisiert
    public void startScoreboardUpdateTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updateScoreboard(player);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Task wird jede Sekunde (20 Ticks) ausgeführt
    }

    // Methode, um das Scoreboard eines Spielers zu aktualisieren
    public void updateScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("Geld");

        if (objective != null) {
            Score score = objective.getScore(ChatColor.GREEN + "Guthaben:");
            score.setScore((int) plugin.getMoneyManager().getMoney(player.getUniqueId()));
        }
    }
}
