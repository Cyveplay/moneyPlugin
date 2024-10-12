package org.example.cyveplay.plugins;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Main extends JavaPlugin implements Listener {

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

        // Lade die Spieler-Daten beim Start
        for (UUID playerUUID : getServer().getOnlinePlayers().stream().map(p -> p.getUniqueId()).toList()) {
            double money = dataManager.loadMoney(playerUUID);
            moneyManager.setMoney(playerUUID, money);

            permissionManager.addPermissions(playerUUID, dataManager.loadPermissions(playerUUID));
        }

        // Event-Registrierung
        getServer().getPluginManager().registerEvents(this, this);

        // Befehle und TabCompleter registrieren
        this.getCommand("money").setExecutor(new MoneyCommand(moneyManager, permissionManager));
        this.getCommand("money").setTabCompleter(new MoneyTabCompleter(permissionManager));
        this.getCommand("trade").setExecutor(new TradeCommand(moneyManager));
        this.getCommand("trade").setTabCompleter(new TradeTabCompleter());
        this.getCommand("permission").setExecutor(new PermissionCommand(permissionManager));
        this.getCommand("permission").setTabCompleter(new PermissionTabCompleter(permissionManager));
    }

    @Override
    public void onDisable() {
        // Speichere alle Daten, wenn das Plugin deaktiviert wird
        for (UUID playerUUID : moneyManager.getAllPlayerUUIDs()) {
            dataManager.saveMoney(playerUUID, moneyManager.getMoney(playerUUID));
            dataManager.savePermissions(playerUUID, permissionManager.getPermissions(playerUUID));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        // Lade die Daten des Spielers beim Joinen
        double money = dataManager.loadMoney(playerUUID);
        moneyManager.setMoney(playerUUID, money);

        permissionManager.addPermissions(playerUUID, dataManager.loadPermissions(playerUUID));

        event.getPlayer().sendMessage("Willkommen! Dein Guthaben beträgt: " + money + " Münzen.");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        // Speichere die Daten des Spielers beim Verlassen des Spiels
        dataManager.saveMoney(playerUUID, moneyManager.getMoney(playerUUID));
        dataManager.savePermissions(playerUUID, permissionManager.getPermissions(playerUUID));
    }

    public MoneyManager getMoneyManager() {
        return moneyManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }
    public ScoreboardManager getScoreboardManager(){
        return scoreboardManager;
    }
}
