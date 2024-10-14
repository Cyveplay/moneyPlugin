package org.example.cyveplay.plugins.Money;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.example.cyveplay.plugins.Money.MoneyManager;
import org.example.cyveplay.plugins.Permission.PermissionManager;


import java.util.UUID;

public class MoneyCommand implements CommandExecutor {

    private MoneyManager moneyManager;
    private PermissionManager permissionManager;

    public MoneyCommand(MoneyManager moneyManager, PermissionManager permissionManager) {
        this.moneyManager = moneyManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID playerUUID = player.getUniqueId();

            // Geld abfragen
            if (args.length == 0) {
                double balance = moneyManager.getMoney(playerUUID);
                player.sendMessage("Dein aktuelles Guthaben beträgt: " + balance + " Münzen.");
                return true;
            }

            if (permissionManager.hasPermission(playerUUID, "ManageMoney")) {

                // Geld hinzufügen (z.B. /money add <betrag>)
                if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
                    try {
                        double amount = Double.parseDouble(args[1]);
                        moneyManager.addMoney(playerUUID, amount);
                        player.sendMessage(amount + " Münzen wurden deinem Konto gutgeschrieben.");
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Bitte gib einen gültigen Betrag an.");
                    }
                    return true;
                }

                // Geld abziehen (z.B. /money remove <betrag>)
                if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
                    try {
                        double amount = Double.parseDouble(args[1]);
                        moneyManager.removeMoney(playerUUID, amount);
                        player.sendMessage(amount + " Münzen wurden von deinem Konto abgezogen.");
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Bitte gib einen gültigen Betrag an.");
                    }
                    return true;
                }
                if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
                    try {
                        double amount = Double.parseDouble(args[1]);
                        moneyManager.setMoney(playerUUID, amount);
                        player.sendMessage(amount + " Münzen wurden von deinem Konto abgezogen.");
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Bitte gib einen gültigen Betrag an.");
                    }
                    return true;
                }
            }

            // Geld überweisen (z.B. /money pay <spieler> <betrag>)
            if (args.length == 3 && args[0].equalsIgnoreCase("pay")) {
                String receiverPlayerName = args[1];
                String amountString = args[2];

                double amount;
                try {
                    amount = Double.parseDouble(amountString);
                } catch (NumberFormatException exception) {
                    player.sendMessage("Bitte gib einen gültigen Betrag an!");
                    return true;
                }

                Player targetPlayer = Bukkit.getPlayer(receiverPlayerName);
                if (targetPlayer == null) {
                    player.sendMessage(ChatColor.RED + "Spieler ist nicht online oder konnte nicht gefunden werden!");
                    return true;
                }
                if (player == targetPlayer){
                    player.sendMessage(ChatColor.RED + "Du kannst dir selbst kein Geld senden!");
                    return true;
                }
                if (amount < 1){
                    player.sendMessage(ChatColor.RED + "Du kannst keinen negativen Betrag überweisen!");
                    return true;
                }

                if (amount <= moneyManager.getMoney(playerUUID)) {
                    // Geld überweisen
                    moneyManager.pay(playerUUID, targetPlayer.getUniqueId(), amount);
                    player.sendMessage("Du hast " + amount + " Münzen an " + targetPlayer.getName() + " überwiesen.");
                    targetPlayer.sendMessage("Du hast " + amount + " Münzen von " + player.getName() + " erhalten.");
                } else {
                    player.sendMessage(ChatColor.RED + "Du hast nicht genug Geld!");
                }
                return true;
            }

            // Unbekannter oder falsch eingegebener Befehl
            player.sendMessage(ChatColor.RED + "Falsche Verwendung des Befehls. Nutze: /money [add/remove/pay] <betrag>.");
        }
        return true;
    }
}
