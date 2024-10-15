package org.example.cyveplay.plugins.Auction;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.example.cyveplay.plugins.Main;
import org.example.cyveplay.plugins.Money.MoneyManager;
import org.example.cyveplay.plugins.Utils;

public class Auction {
    private String ownerName;
    private UUID owner;
    public ItemStack item;
    public double highestBidderPrice;
    public UUID highestBidder;
    public long duration;

    private BukkitTask countdown;

    public Auction(UUID owner, ItemStack item, double startingPrice, Main plugin, long duration) {
        this.highestBidderPrice = startingPrice;
        this.highestBidder = null;
        this.item = item;
        this.owner = owner;
        this.ownerName = plugin.getServer().getOfflinePlayer(owner).getName();
        this.duration = Bukkit.getPlayer(owner).getWorld().getTime() + (duration * 60 * 20);
        this.countdown = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

            @Override
            public void run() {
                plugin.getAuctionManager().stopAuction(owner);
            }
        }, duration * 60 * 20);
    }

    public long timeUntilAuctionEnd(long worldTime) {
        return this.duration - worldTime;
    }

    public void onAuctionStart() {
        Utils.sendMessageToAllPlayers(ChatColor.GREEN+ownerName+" Hat eine Auktion gestartet ("+item.getAmount()+"x"+item.getData().getItemType().getItemTranslationKey()+" für "+highestBidderPrice+"), Sie endet in "+getAuctionLengthString(this.duration - Bukkit.getPlayer(this.ownerName).getWorld().getTime()));
    }

    public void onAuctionEnd(Plugin plugin, MoneyManager moneyManager) {
        if(!this.countdown.isCancelled()) {
            this.countdown.cancel();
        }

        Player ownerPlayer = plugin.getServer().getPlayer(this.owner);
        boolean success = false;
        if (ownerPlayer != null && ownerPlayer.isOnline()) {
            if (this.highestBidder == null) {
                Utils.sendMessageToAllPlayers(ChatColor.YELLOW+"Die Auktion von " + this.ownerName + " wurde beendet, aber die Transaktion konnte nicht durch geführt werden, da keiner geboten hat");
            } else {
                Player bidderPlayer = plugin.getServer().getPlayer(this.highestBidder);
                if (bidderPlayer != null && bidderPlayer.isOnline()) {
                    if (moneyManager.getMoney(this.highestBidder) < this.highestBidderPrice) {
                        Utils.sendMessageToAllPlayers(ChatColor.YELLOW+"Die Auktion von " + this.ownerName + " wurde beendet, aber die Transaktion konnte nicht durch geführt werden, da der höchste bieter zu wenig Geld hat");
                    } else {
                        moneyManager.setMoney(this.highestBidder, moneyManager.getMoney(this.highestBidder) - this.highestBidderPrice);
                        moneyManager.setMoney(this.owner, moneyManager.getMoney(this.owner) + this.highestBidderPrice);
                        bidderPlayer.getInventory().addItem(item);
                        Utils.sendMessageToAllPlayers(ChatColor.BLUE+"Das Item der Auktion von "+this.ownerName+" geht für "+this.highestBidderPrice+" Münzen an "+ bidderPlayer.getName());
                        success = true;
                    }
                } else {
                    Utils.sendMessageToAllPlayers(ChatColor.YELLOW+"Die Auktion von " + this.ownerName + " wurde beendet, aber die Transaktion konnte nicht durch geführt werden, da der höchste bieter nicht Online ist");
                }
            }

            if(!success) {
                ownerPlayer.getInventory().addItem(item);
            }

        } else  {
            Utils.sendMessageToAllPlayers(ChatColor.YELLOW+"Die Auktion von " + this.ownerName + " wurde beendet, aber die Transaktion konnte nicht durch geführt werden, da der besitzer nicht Online ist");
        }
    }

    public static String getAuctionLengthString(long time) {
        return Utils.addZero(Utils.ticksToMinutes(time), 2) + ":" + Utils.addZero((Utils.ticksToSeconds(time)%60), 2);
    }
}
