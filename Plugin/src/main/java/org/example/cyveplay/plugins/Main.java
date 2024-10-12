package org.example.cyveplay.plugins;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Main extends JavaPlugin {

    private MoneyManager moneyManager;
    private PermissionManager permissionManager;
    private DataManager dataManager;
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        // Initialisiere das MoneyManager-System
        moneyManager = new MoneyManager();
        permissionManager = new PermissionManager();

        // Lade die Daten
        dataManager = new DataManager(this);
        scoreboardManager = new ScoreboardManager(this);

        // Lade die Spieler-Daten beim Start und initialisiere deren Scoreboards
        for (UUID playerUUID : getServer().getOnlinePlayers().stream().map(p -> p.getUniqueId()).toList()) {
            double money = dataManager.loadMoney(playerUUID);
            moneyManager.setMoney(playerUUID, money);
            permissionManager.addPermissions(playerUUID, dataManager.loadPermissions(playerUUID));
            // Scoreboard für jeden online Spieler erstellen
            scoreboardManager.createScoreboard(getServer().getPlayer(playerUUID));
        }

        // Event-Registrierung
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // Befehle und TabCompleter registrieren
        this.getCommand("money").setExecutor(new MoneyCommand(moneyManager, permissionManager));
        this.getCommand("money").setTabCompleter(new MoneyTabCompleter(permissionManager));
        this.getCommand("trade").setExecutor(new TradeCommand(moneyManager));
        this.getCommand("trade").setTabCompleter(new TradeTabCompleter());
        this.getCommand("permission").setExecutor(new PermissionCommand(permissionManager));
        this.getCommand("permission").setTabCompleter(new PermissionTabCompleter(permissionManager));

        // Starte die regelmäßige Scoreboard-Aktualisierung
        scoreboardManager.startScoreboardUpdateTask();
    }

    @Override
    public void onDisable() {
        // Speichere alle Daten, wenn das Plugin deaktiviert wird
        for (UUID playerUUID : moneyManager.getAllPlayerUUIDs()) {
            dataManager.saveMoney(playerUUID, moneyManager.getMoney(playerUUID));
            dataManager.savePermissions(playerUUID, permissionManager.getPermissions(playerUUID));
        }
    }

    public MoneyManager getMoneyManager() {
        return moneyManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
}
