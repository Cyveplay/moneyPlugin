package org.example.cyveplay.plugins;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Main extends JavaPlugin implements Listener {

    private MoneyManager moneyManager;

    @Override
    public void onEnable() {
        // Initialisiere das MoneyManager-System
        moneyManager = new MoneyManager();

        // Event-Registrierung
        getServer().getPluginManager().registerEvents(this, this);

        // MoneyCommand und zugehörigen TabCompleter registrieren
        this.getCommand("money").setExecutor(new MoneyCommand(moneyManager));
        this.getCommand("money").setTabCompleter(new MoneyTabCompleter());

        // TradeCommand und zugehörigen TabCompleter registrieren
        this.getCommand("trade").setExecutor(new TradeCommand(moneyManager));
        this.getCommand("trade").setTabCompleter(new TradeTabCompleter());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Hol dir die UUID des Spielers
        UUID playerUUID = event.getPlayer().getUniqueId();

        // Überprüfe, ob der Spieler bereits einen Eintrag hat (bei Bedarf).
        // In unserem MoneyManager wird automatisch der Standardwert verwendet.
        double balance = moneyManager.getMoney(playerUUID);

        // Zeige dem Spieler beim Beitritt sein aktuelles Guthaben an
        event.getPlayer().sendMessage("Willkommen! Dein Guthaben beträgt: " + balance + " Münzen.");
    }

    // Beispiel einer Methode, um einem Spieler Geld zu geben
    public void givePlayerMoney(UUID playerUUID, double amount) {
        moneyManager.addMoney(playerUUID, amount);
    }
}
