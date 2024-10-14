package org.example.cyveplay.plugins;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.cyveplay.plugins.Permission.PermissionManager;

import java.util.List;
import java.util.UUID;

public class Main extends JavaPlugin {

    private org.example.cyveplay.plugins.Money.MoneyManager moneyManager;
    private PermissionManager permissionManager;
    private DataManager dataManager;
    private ScoreboardManager scoreboardManager;
    private org.example.cyveplay.plugins.Market.MarketManager marketManager;  // Neu hinzugefügt

    @Override
    public void onEnable() {
        // Initialisiere das MoneyManager-System
        moneyManager = new org.example.cyveplay.plugins.Money.MoneyManager();
        permissionManager = new PermissionManager();

        // Lade die Daten
        dataManager = new DataManager(this);
        scoreboardManager = new ScoreboardManager(this);
        marketManager = new org.example.cyveplay.plugins.Market.MarketManager(this, moneyManager);  // Neu hinzugefügt

        // Lade die Daten für alle Spieler, auch die, die nicht online sind
        for (UUID playerUUID : dataManager.getAllPlayerUUIDs()) {  // Hier rufst du die UUIDs aus der Datei ab
            // Lade das Geld für den Spieler
            double money = dataManager.loadMoney(playerUUID);
            moneyManager.setMoney(playerUUID, money);

            // Lade und wende die Berechtigungen an
            List<String> permissions = dataManager.loadPermissions(playerUUID);
            permissionManager.addPermissions(playerUUID, permissions);

            // Hole den Player, um das Scoreboard zu erstellen, falls er online ist
            Player player = getServer().getPlayer(playerUUID);
            if (player != null) {
                scoreboardManager.createScoreboard(player);
            }
        }

        // Event-Registrierung
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new AntiReportSystem(), this);

        // Befehle und TabCompleter registrieren
        this.getCommand("money").setExecutor(new org.example.cyveplay.plugins.Money.MoneyCommand(moneyManager, permissionManager));
        this.getCommand("money").setTabCompleter(new org.example.cyveplay.plugins.Money.MoneyTabCompleter(permissionManager));
        this.getCommand("trade").setExecutor(new org.example.cyveplay.plugins.Trade.TradeCommand(moneyManager));
        this.getCommand("trade").setTabCompleter(new org.example.cyveplay.plugins.Trade.TradeTabCompleter());
        this.getCommand("permission").setExecutor(new org.example.cyveplay.plugins.Permission.PermissionCommand(permissionManager));
        this.getCommand("permission").setTabCompleter(new org.example.cyveplay.plugins.Permission.PermissionTabCompleter(permissionManager));
        this.getCommand("sell").setExecutor(new SellCommand(moneyManager));

        // Hier die Registrierung des Market-Befehls und TabCompleters
        this.getCommand("market").setExecutor(new org.example.cyveplay.plugins.Market.MarketCommand(marketManager));
        this.getCommand("market").setTabCompleter(new org.example.cyveplay.plugins.Market.MarketTabCompleter(marketManager));

        // Starte die regelmäßige Scoreboard-Aktualisierung
        scoreboardManager.startScoreboardUpdateTask();
    }

    @Override
    public void onDisable() {
        // Speichere alle Daten, wenn das Plugin deaktiviert wird
        for (UUID playerUUID : moneyManager.getAllPlayerUUIDs()) {
            dataManager.saveMoney(playerUUID, moneyManager.getMoney(playerUUID));
            dataManager.savePermissions(playerUUID, permissionManager.getPermissions(playerUUID));
            marketManager.saveShop(playerUUID.toString());
        }

        // Speichere den Marktstatus
          // Neu hinzugefügt, um Shops zu speichern
    }

    public org.example.cyveplay.plugins.Money.MoneyManager getMoneyManager() {
        return moneyManager;
    }

    public org.example.cyveplay.plugins.Permission.PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public org.example.cyveplay.plugins.Market.MarketManager getMarketManager() {
        return marketManager;
    }
}
