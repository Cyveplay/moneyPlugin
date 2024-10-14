package org.example.cyveplay.plugins.Market;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.cyveplay.plugins.Market.MarketManager;

public class MarketCommand implements CommandExecutor {

    private final MarketManager marketManager;

    public MarketCommand(MarketManager marketManager) {
        this.marketManager = marketManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Verwende /market add [price] oder /market open [playername]");
            return true;
        }

        if (args[0].equalsIgnoreCase("add") && args.length == 2) {
            try {
                double price = Double.parseDouble(args[1]);
                marketManager.addItemToShop(player, price);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Ungültiger Preis! Bitte gebe eine gültige Zahl an.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("open") && args.length == 2) {
            String targetPlayerName = args[1];
            marketManager.openShop(player, targetPlayerName);
            return true;
        }

        player.sendMessage(ChatColor.RED + "Ungültige Verwendung. Verwende /market add [price] oder /market open [playername]");
        return true;
    }
}
