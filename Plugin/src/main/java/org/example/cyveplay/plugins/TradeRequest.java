package org.example.cyveplay.plugins;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TradeRequest {
    private Player sender;
    private Player receiver;
    private ItemStack item;
    private int amount;
    private double price;

    public TradeRequest(Player sender, Player receiver, ItemStack item, int amount, double price) {
        this.sender = sender;
        this.receiver = receiver;
        this.item = item;
        this.amount = amount;
        this.price = price;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }
}
