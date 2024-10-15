package org.example.cyveplay.plugins.Auction;

import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.example.cyveplay.plugins.Main;
import org.example.cyveplay.plugins.Utils;

import java.util.UUID;

public class AuctionCommand implements CommandExecutor {
    private final AuctionManager manager;
    private final Main main;

    public AuctionCommand(Main main) {
        this.manager = main.getAuctionManager();
        this.main = main;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 0 || !(commandSender instanceof Player player)) {
            return false;
        }

        UUID playerUUID = player.getUniqueId();

        if(strings[0].equals("start") && strings.length == 3) {
            ItemStack itemHand = player.getItemInHand();
            double price = Double.parseDouble(strings[1]);
            long length = Long.parseLong(strings[2]);

            if(price < 0) {
                player.sendMessage(ChatColor.RED+"Der Preis muss positiv sein");
                return true;
            }

            if(length < 0) {
                player.sendMessage(ChatColor.RED+"Die Länge muss positiv sein");
                return true;
            }

            if(itemHand.getType() == Material.AIR) {
                player.sendMessage(ChatColor.RED+"Du kannst Luft nicht verkaufen!");
                return true;
            }

            manager.startAuction(playerUUID, new Auction(playerUUID, itemHand, price, main, length));
            player.setItemInHand(new ItemStack(Material.AIR));
        } else if(strings[0].equals("stop")) {
            manager.stopAuction(playerUUID);
        } else if(strings[0].equals("bid") && strings.length == 3) {
            manager.bid(player, strings[1], Double.parseDouble(strings[2]));
        } else if(strings[0].equals("info") && strings.length == 2) {
            Player ownerPlayer = Bukkit.getPlayer(strings[1]);
            if(ownerPlayer != null) {
                Auction auction = manager.activeAuctions.get(ownerPlayer.getUniqueId());
                if (auction != null) {
                    player.sendMessage(ChatColor.GOLD+"Auktion von " + strings[1]);
                    player.sendMessage(ChatColor.GRAY+"-----------------------------");
                    player.sendMessage(ChatColor.BLUE+"Item: " + auction.item.getTranslationKey());
                    player.sendMessage(ChatColor.BLUE+"Aktueller Preis: " + auction.highestBidderPrice);
                    player.sendMessage(ChatColor.BLUE+"Höchste Bieter: " + (auction.highestBidder != null ? Bukkit.getPlayer(auction.highestBidder).getName() : "Keiner"));
                    player.sendMessage(ChatColor.BLUE+"Verbleibende Zeit: "+ Auction.getAuctionLengthString(auction.duration - player.getWorld().getTime()));
                    player.sendMessage(ChatColor.GRAY+"-----------------------------");

                }
            }
        } else {
            player.sendMessage(ChatColor.RED+"Ungültige Verwendung. Verwende /auction start [start_price] [duration], /auction stop, /auction bid [auction_owner] [bid_price] oder /auction info [auction_owner]");
        }

        return true;
    }
}
