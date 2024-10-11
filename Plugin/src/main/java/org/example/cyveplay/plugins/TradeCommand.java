package org.example.cyveplay.plugins;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.UUID;

public class TradeCommand implements CommandExecutor {

    private HashMap<UUID, TradeRequest> pendingTrades = new HashMap<>();
    private MoneyManager moneyManager;

    public TradeCommand(MoneyManager moneyManager) {
        this.moneyManager = moneyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Handelsanfrage senden
            if (args.length == 3 && args[0].equalsIgnoreCase("send")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    String message = ChatColor.RED + "Spieler nicht gefunden.";
                    player.sendMessage(message);
                    return true;
                }
                if (player == target){
                    String message = ChatColor.RED + "Du kannst keine Anfrage an dich selbst senden!";
                    player.sendMessage(message);
                    return true;
                }

                // Erfasse das Item in der Hand des Spielers
                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                if (itemInHand == null || itemInHand.getAmount() == 0) {
                    player.sendMessage(ChatColor.RED + "Du hältst kein Item in der Hand, das gehandelt werden kann.");
                    return true;
                }

                double price;
                try {
                    price = Double.parseDouble(args[2]);  // Preis aus dem Befehl
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Bitte gib einen gültigen Preis an.");
                    return true;
                }

                // Handelsanfrage erstellen
                TradeRequest tradeRequest = new TradeRequest(player, target, itemInHand, itemInHand.getAmount(), price);
                pendingTrades.put(target.getUniqueId(), tradeRequest);

                player.sendMessage("Handelsanfrage an " + target.getName() + " gesendet.");
                target.sendMessage(player.getName() + " möchte " + itemInHand.getAmount() + " " + itemInHand.getType().name() + " für " + price + " Münzen handeln.");
                target.sendMessage("Gebe /trade accept ein, um anzunehmen oder /trade deny, um abzulehnen.");
                return true;
            }

            // Handelsanfrage akzeptieren
            if (args.length == 1 && args[0].equalsIgnoreCase("accept")) {
                TradeRequest tradeRequest = pendingTrades.get(player.getUniqueId());
                if (tradeRequest == null) {
                    player.sendMessage(ChatColor.RED + "Du hast keine ausstehende Handelsanfrage.");
                    return true;
                }

                Player senderPlayer = tradeRequest.getSender();
                if (moneyManager.getMoney(player.getUniqueId()) >= tradeRequest.getPrice()) {
                    moneyManager.pay(player.getUniqueId(), senderPlayer.getUniqueId(), tradeRequest.getPrice());

                    // Entfernen des Items aus dem Inventar des Absenders und Hinzufügen zum Inventar des Empfängers
                    ItemStack tradeItem = tradeRequest.getItem();
                    tradeItem.setAmount(tradeRequest.getAmount());
                    player.getInventory().addItem(tradeItem);
                    senderPlayer.getInventory().removeItem(tradeItem);

                    player.sendMessage("Handel abgeschlossen! Du hast " + tradeRequest.getAmount() + " " + tradeItem.getType().name() + " erhalten.");
                    senderPlayer.sendMessage("Handel abgeschlossen! Du hast " + tradeRequest.getPrice() + " Münzen erhalten.");

                    pendingTrades.remove(player.getUniqueId());
                } else {
                    player.sendMessage(ChatColor.RED + "Du hast nicht genug Geld für diesen Handel.");
                }
                return true;
            }

            // Handelsanfrage ablehnen
            if (args.length == 1 && args[0].equalsIgnoreCase("deny")) {
                TradeRequest tradeRequest = pendingTrades.get(player.getUniqueId());
                if (tradeRequest != null) {
                    Player senderPlayer = tradeRequest.getSender();
                    senderPlayer.sendMessage("Dein Handel wurde von " + player.getName() + " abgelehnt.");
                    player.sendMessage("Du hast den Handel abgelehnt.");
                    pendingTrades.remove(player.getUniqueId());
                } else {
                    player.sendMessage(ChatColor.RED + "Du hast keine ausstehende Handelsanfrage.");
                }
                return true;
            }
        }
        return false;
    }
}
