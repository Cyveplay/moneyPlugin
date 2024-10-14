package org.example.cyveplay.plugins.Auction;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.example.cyveplay.plugins.Main;
import org.example.cyveplay.plugins.Utils;

public class AuctionManager implements Listener {

    public AuctionManager(Main main) {
         this.main = main;
    }

    public Main main;
    public HashMap<UUID, Auction> activeAuctions = new HashMap();

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
            return;
        }

        if(activeAuctions.get(ownerPlayer.getUniqueId()) == null) {
            return;
        }

        if(main.getMoneyManager().getMoney(player.getUniqueId()) < price) {
            return;
        }

        Auction a = activeAuctions.get(ownerPlayer.getUniqueId());

        if(a.highestBidderPrice < price) {
            a.highestBidderPrice = price;
            a.highestBidder = player.getUniqueId();
            Utils.sendMessageToAllPlayers(player.getName()+" Bietet "+price+" für die Auktion von "+owner);
        }
    }

    public void stopAuction(UUID uuid   ) {
        if(activeAuctions.get(uuid) == null) {
            return;
        }

        activeAuctions.get(uuid).onAuctionEnd(main, main.getMoneyManager());
        activeAuctions.remove(uuid);
    }

    public AuctionManager() {
    }
}
