package org.example.cyveplay.plugins;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class SellCommand implements CommandExecutor {

    MoneyManager moneyManager;
    public SellCommand(MoneyManager moneyManager) {
        this.moneyManager = moneyManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        Player player = (Player) commandSender;
        ItemStack itemInHand = player.getItemInHand();

        if (itemInHand.getType() != Material.DIAMOND ){
            player.sendMessage(ChatColor.RED + "Man kann nur Diamanten verkaufen");
            return true;
        }
        int DiamondCount = itemInHand.getAmount();
        moneyManager.addMoney(player.getUniqueId(),DiamondCount * 100);
        player.sendMessage(ChatColor.GREEN + "Deinem Konto wurden " + DiamondCount * 100 + " Münzen zugeschrieben!");
        itemInHand.setAmount(0);

        return true;
    }
}
