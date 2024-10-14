package org.example.cyveplay.plugins.Auction;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.example.cyveplay.plugins.Main;

public class AuctionCommand implements CommandExecutor {
    private AuctionManager manager;
    private Main main;

    public AuctionCommand(Main main) {
        this.manager = main.getAuctionManager();
        this.main = main;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 0 || !(commandSender instanceof Player)) {
            return false;
        }

        if(strings[0].equals("start")) {
            if(((Player) commandSender).getPlayer().getItemInHand().getType() == Material.AIR) {
                return false;
            }
            manager.startAuction(((Player) commandSender).getUniqueId(), new Auction(((Player) commandSender).getUniqueId(), ((Player) commandSender).getItemInHand(), Double.parseDouble(strings[1]), main));
            ((Player) commandSender).setItemInHand(new ItemStack(Material.AIR));
        }

        if(strings[0].equals("stop")) {
            manager.stopAuction(((Player) commandSender).getUniqueId());
        }

        if(strings[0].equals("bid")) {
            manager.bid(((Player) commandSender).getPlayer(), strings[1], Double.parseDouble(strings[2]));
        }

        return true;
    }
}
