package org.example.cyveplay.plugins;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class PlayerJoinListener implements Listener {

    private final Main plugin;

    public PlayerJoinListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Scoreboard für den Spieler erstellen
        plugin.getScoreboardManager().createScoreboard(player);

        // Begrüßungsnachricht mit dem aktuellen Guthaben
        double balance = plugin.getMoneyManager().getMoney(player.getUniqueId());
        player.sendMessage("Willkommen! Dein Guthaben beträgt: " + balance + " Münzen.");
    }
}
