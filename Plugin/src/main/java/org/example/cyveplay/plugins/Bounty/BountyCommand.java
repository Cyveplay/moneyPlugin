package org.example.cyveplay.plugins.Bounty;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.cyveplay.plugins.Money.MoneyManager;

import java.util.UUID;

public class BountyCommand implements CommandExecutor {

    private BountyManager bountyManager;
    private MoneyManager moneyManager;

    public BountyCommand(BountyManager bountyManager, MoneyManager moneyManager){
        this.bountyManager = bountyManager;
        this.moneyManager = moneyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)){
            sender.sendMessage("dieser Befehl kann nur von einem Spieler ausgeführt werden!");
            return true;
        }

        Player player = (Player) sender;

        if (args[0].equals("list")){
            player.openInventory(BountyManager.openChestGUI());
            return false;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Verwende /bounty [Spielername] [Kopfgeld]");
            return true;
        }

        Player target = null;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getName().equalsIgnoreCase(args[0])) {
                target = onlinePlayer;
                break;
            }
        }

        if (target == null){
            player.sendMessage(ChatColor.RED + "Dieser Spieler ist nicht online!");
            return true;
        }

        if(!moneyManager.getAllPlayerUUIDs().contains(target.getUniqueId())){
            player.sendMessage(ChatColor.RED + "Dieser Spieler existiert nicht!");
            return true;
        }

        // Betrag parsen
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Ungültiger Betrag! Bitte eine Zahl eingeben.");
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(ChatColor.RED + "Der Betrag muss größer als 0 sein!");
            return true;
        }

        UUID targetUUID = target.getUniqueId();
        for (Bounty bounty : BountyManager.getBountyList().getListItems()){
            if (bounty.getTargetUUID().equals(targetUUID)){
                player.sendMessage(ChatColor.RED + "Auf diesen Spieler ist bereits ein Kopfgeld ausgelegt!");
                return true;
            }
        }

        if (moneyManager.getMoney(player.getUniqueId()) < amount) {
            player.sendMessage(ChatColor.RED + "Du hast nicht genug Geld!");
            return true;
        }

        bountyManager.addBounty(player.getUniqueId(), targetUUID, amount);
        player.sendMessage(ChatColor.GREEN + "Du hast ein Kopfgeld in Höhe von " + amount +
                " auf " + target.getName() + " ausgesetzt");

        return true;
    }
}