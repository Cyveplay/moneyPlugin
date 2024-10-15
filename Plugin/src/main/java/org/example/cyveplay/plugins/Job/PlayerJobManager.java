package org.example.cyveplay.plugins.Job;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerJobManager {
    private HashMap<UUID, PlayerJob> playerJobs = new HashMap<>(); // Zuordnung von Spielern zu Jobs

    // Einen Spieler einem Job zuordnen
    public void assignJobToPlayer(UUID playerID, Job job) {
        playerJobs.put(playerID, new PlayerJob(job));
    }

    // Den Job des Spielers abrufen
    public PlayerJob getPlayerJob(UUID playerID) {
        return playerJobs.get(playerID);
    }

    // Fortschritt eines Spielers aktualisieren
    public void updatePlayerProgress(UUID playerID, int amount) {
        PlayerJob playerJob = playerJobs.get(playerID);
        if (playerJob != null) {
            playerJob.incrementProgress(amount);
            if (playerJob.isComplete()) {
                // Spieler hat den Job abgeschlossen
                playerJob.increaseJobLevel();
                Player player = Bukkit.getPlayer(playerID);
                player.sendMessage(ChatColor.GREEN+"Du bist Jetzt Job Level: " + playerJob.getJobLevel());
                // Hier kannst du Belohnungen vergeben oder den Job zurücksetzen
            }
        }
    }
}

