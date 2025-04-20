package org.example.cyveplay.plugins;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.cyveplay.plugins.Auction.AuctionCommand;
import org.example.cyveplay.plugins.Auction.AuctionManager;
import org.example.cyveplay.plugins.Casino.CasinoManager;
import org.example.cyveplay.plugins.Enderchest.EnderchestManager;
import org.example.cyveplay.plugins.Job.*;
import org.example.cyveplay.plugins.Market.MarketManager;
import org.example.cyveplay.plugins.Permission.PermissionManager;

import java.util.List;
import java.util.UUID;

public class Main extends JavaPlugin {

    private org.example.cyveplay.plugins.Money.MoneyManager moneyManager;
    private PermissionManager permissionManager;
    private DataManager dataManager;
    private ScoreboardManager scoreboardManager;
    private org.example.cyveplay.plugins.Market.MarketManager marketManager;  // Neu hinzugefügt
    private EnderchestManager enderchestManager;
    private JobManager jobManager;
    private PlayerJobManager playerJobManager;
    private CasinoManager casinoManager;

    private AuctionManager auctionManager;

    @Override
    public void onEnable() {
        moneyManager = new org.example.cyveplay.plugins.Money.MoneyManager();
        permissionManager = new PermissionManager();
        enderchestManager = new EnderchestManager(this);
        auctionManager = new AuctionManager(this);
        // Lade die Daten
        dataManager = new DataManager(this);
        scoreboardManager = new ScoreboardManager(this);
        marketManager = new org.example.cyveplay.plugins.Market.MarketManager(this, moneyManager);  // Neu hinzugefügt
        jobManager = new JobManager();
        playerJobManager = new PlayerJobManager();
        casinoManager = new CasinoManager(this, moneyManager);
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @org.bukkit.event.EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                casinoManager.handleInventoryClick(event);
            }
        }, this);


        // JobEvents Listener für Fortschritt und andere Job-Events registrieren
        getServer().getPluginManager().registerEvents(new JobAdvances(playerJobManager), this);

        // Command für Jobs registrieren
        this.getCommand("job").setExecutor(new PlayerJobCommand(jobManager, playerJobManager));

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
        getServer().getPluginManager().registerEvents(new EnderchestManager(this), this);

        getServer().getPluginManager().registerEvents(new MarketManager(this, moneyManager), this);

        // Befehle und TabCompleter registrieren
        this.getCommand("money").setExecutor(new org.example.cyveplay.plugins.Money.MoneyCommand(moneyManager, permissionManager));
        this.getCommand("money").setTabCompleter(new org.example.cyveplay.plugins.Money.MoneyTabCompleter(permissionManager));
        this.getCommand("trade").setExecutor(new org.example.cyveplay.plugins.Trade.TradeCommand(moneyManager));
        this.getCommand("trade").setTabCompleter(new org.example.cyveplay.plugins.Trade.TradeTabCompleter());
        this.getCommand("permission").setExecutor(new org.example.cyveplay.plugins.Permission.PermissionCommand(permissionManager));
        this.getCommand("permission").setTabCompleter(new org.example.cyveplay.plugins.Permission.PermissionTabCompleter(permissionManager));
        this.getCommand("sell").setExecutor(new SellCommand(moneyManager));
        this.getCommand("market").setExecutor(new org.example.cyveplay.plugins.Market.MarketCommand(marketManager));
        this.getCommand("market").setTabCompleter(new org.example.cyveplay.plugins.Market.MarketTabCompleter(marketManager));
        this.getCommand("enderchest").setExecutor(new org.example.cyveplay.plugins.Enderchest.EnderchestCommand(enderchestManager));
        this.getCommand("auction").setExecutor(new AuctionCommand(this));
        this.getCommand("gambling").setExecutor(new org.example.cyveplay.plugins.Casino.CasinoCommand(casinoManager));

        enderchestManager.loadAllEnderchests(dataManager.getAllPlayerUUIDs());

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
        enderchestManager.saveAllEnderChests();
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

    public AuctionManager getAuctionManager() {
        return auctionManager;
    }
}
