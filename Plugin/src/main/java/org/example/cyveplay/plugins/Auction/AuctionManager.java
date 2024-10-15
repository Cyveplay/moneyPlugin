package org.example.cyveplay.plugins.Auction;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.example.cyveplay.plugins.Main;
import org.example.cyveplay.plugins.Utils;

public class AuctionManager implements Listener {

    public Main main;
    public HashMap<UUID, Auction> activeAuctions = new HashMap();

    public AuctionManager(Main main) {
        this.main = main;
    }

    public void startAuction(UUID uuid, Auction auction) {
        if(activeAuctions.get(uuid) != null) {
            return;
        }

        activeAuctions.put(uuid, auction);
        auction.onAuctionStart();
    }

    public void bid(Player player, String owner, double price) {

        Player ownerPlayer = Bukkit.getPlayer(owner);

        if(ownerPlayer == null) {
            player.sendMessage(ChatColor.RED+"Der angegebene Spieler ist Offline");
            return;
        }

        if(activeAuctions.get(ownerPlayer.getUniqueId()) == null) {
            player.sendMessage(ChatColor.RED+"Dieser Spieler hat keine Auktion gestartet");
            return;
        }

        Auction a = activeAuctions.get(ownerPlayer.getUniqueId());

        if(main.getMoneyManager().getMoney(player.getUniqueId()) < price) {
            player.sendMessage(ChatColor.RED+"Du hast zu wenig geld");
            return;
        }

        if(a.highestBidderPrice < price) {
            a.highestBidderPrice = price;
            a.highestBidder = player.getUniqueId();
            Utils.sendMessageToAllPlayers(ChatColor.GOLD+player.getName()+" Bietet "+price+" für die Auktion von "+owner);
        } else {
            player.sendMessage(ChatColor.RED+"Du musst einen angebot eingeben das höher ist als "+a.highestBidderPrice);
        }
    }

    public void stopAuction(UUID uuid   ) {
        if(activeAuctions.get(uuid) == null) {
            return;
        }

        activeAuctions.get(uuid).onAuctionEnd(main, main.getMoneyManager());
        activeAuctions.remove(uuid);
    }
}
