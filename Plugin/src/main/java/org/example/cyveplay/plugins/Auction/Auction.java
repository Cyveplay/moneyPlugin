package org.example.cyveplay.plugins.Auction;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.example.cyveplay.plugins.Money.MoneyManager;
import org.example.cyveplay.plugins.Utils;

public class Auction {
    private String ownerName;
    private UUID owner;
    private ItemStack item;
    public double highestBidderPrice;
    public UUID highestBidder;

    public Auction(UUID owner, ItemStack item, double startingPrice, Plugin plugin) {
        this.highestBidderPrice = startingPrice;
        this.highestBidder = null;
        this.item = item;
        this.owner = owner;
        this.ownerName = plugin.getServer().getOfflinePlayer(owner).getName();
    }

    public void onAuctionStart() {
        Utils.sendMessageToAllPlayers(ownerName+" Hat eine Auktion gestartet ("+item.getAmount()+"x"+item.getData().getItemType().getItemTranslationKey()+" für "+highestBidderPrice+")");
    }

    public void onAuctionEnd(Plugin plugin, MoneyManager moneyManager) {
        Player ownerPlayer = plugin.getServer().getPlayer(this.owner);
        boolean success = false;
        if (ownerPlayer != null && ownerPlayer.isOnline()) {
            if (this.highestBidder == null) {
                Utils.sendMessageToAllPlayers("Die Auktion von " + this.ownerName + " wurde beendet, aber die Transaktion konnte nicht durch geführt werden, da keiner geboten hat");
            } else {
                Player bidderPlayer = plugin.getServer().getPlayer(this.highestBidder);
                if (bidderPlayer != null && bidderPlayer.isOnline()) {
                    if (moneyManager.getMoney(this.highestBidder) < this.highestBidderPrice) {
                        Utils.sendMessageToAllPlayers("Die Auktion von " + this.ownerName + " wurde beendet, aber die Transaktion konnte nicht durch geführt werden, da der höchste bieter zu wenig Geld hat");
                    } else {
                        moneyManager.setMoney(this.highestBidder, moneyManager.getMoney(this.highestBidder) - this.highestBidderPrice);
                        moneyManager.setMoney(this.owner, moneyManager.getMoney(this.owner) + this.highestBidderPrice);
                        bidderPlayer.getInventory().addItem(item);
                        Utils.sendMessageToAllPlayers("Das Item der Auktion von "+this.ownerName+" geht für "+this.highestBidderPrice+" Münzen an "+ bidderPlayer.getName());
                        success = true;
                    }
                } else {
                    Utils.sendMessageToAllPlayers("Die Auktion von " + this.ownerName + " wurde beendet, aber die Transaktion konnte nicht durch geführt werden, da der höchste bieter nicht Online ist");
                }
            }

            if(!success) {
                ownerPlayer.getInventory().addItem(item);
            }

        } else  {
            Utils.sendMessageToAllPlayers("Die Auktion von " + this.ownerName + " wurde beendet, aber die Transaktion konnte nicht durch geführt werden, da der besitzer nicht Online ist");
        }
    }
}
