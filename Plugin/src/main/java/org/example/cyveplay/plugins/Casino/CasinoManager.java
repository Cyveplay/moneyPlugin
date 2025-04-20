// 1. CasinoManager.java
package org.example.cyveplay.plugins.Casino;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.example.cyveplay.plugins.Money.MoneyManager;

import java.util.*;

public class CasinoManager {

    private final JavaPlugin plugin;
    private final MoneyManager moneyManager;
    private final Map<UUID, Integer> playerBets = new HashMap<>();
    private final Map<UUID, Boolean> isSpinning = new HashMap<>();

    private final List<Material> slotSymbols = Arrays.asList(
            Material.DIAMOND,    // 5x Gewinn
            Material.EMERALD,    // 4x Gewinn
            Material.AMETHYST_SHARD, //3.5x Gewinn
            Material.GOLD_INGOT, // 3x Gewinn
            Material.IRON_INGOT, // 2x Gewinn
            Material.REDSTONE,   // 1.5x Gewinn
            Material.COPPER_INGOT, //1.25x Gewinn
            Material.APPLE       // 1x Gewinn (Standard)
    );

    public CasinoManager(JavaPlugin plugin, MoneyManager moneyManager) {
        this.plugin = plugin;
        this.moneyManager = moneyManager;
    }

    public void openBetGUI(Player player) {
        if (player == null) return;

        Inventory gui = Bukkit.createInventory(null, 27, "§6Einsatz wählen");

        gui.setItem(11, createBetItem(Material.GOLD_NUGGET, "§eSetze 20€", 20));
        gui.setItem(13, createBetItem(Material.GOLD_INGOT, "§6Setze 100€", 100));
        gui.setItem(15, createBetItem(Material.GOLD_BLOCK, "§cSetze 200€", 200));

        player.openInventory(gui);
    }

    private ItemStack createBetItem(Material material, String name, int amount) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList("Spiele mit " + amount + "€ Einsatz"));
        item.setItemMeta(meta);
        return item;
    }

    public void openSlotGUI(Player player, int bet) {
        if (!moneyManager.hasEnoughMoney(player.getUniqueId(), bet)) {
            player.sendMessage("§cDu hast nicht genug Geld!");
            return;
        }

        moneyManager.removeMoney(player.getUniqueId(), bet);
        playerBets.put(player.getUniqueId(), bet);
        isSpinning.put(player.getUniqueId(), false);

        Inventory gui = Bukkit.createInventory(null, 27, "§2Casino - Slot");

        for (int i = 0; i < 27; i++) {
            gui.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }

        gui.setItem(13, createStartButton());
        player.openInventory(gui);
    }

    private ItemStack createStartButton() {
        ItemStack start = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta meta = start.getItemMeta();
        meta.setDisplayName("§aDreh starten!");
        meta.setLore(Collections.singletonList("§7Klicke hier um zu spielen"));
        start.setItemMeta(meta);
        return start;
    }

    public void handleInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        ItemStack clicked = e.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR) return;

        if (title.equals("§6Einsatz wählen")) {
            e.setCancelled(true);

            int bet = 0;
            if (clicked.getType() == Material.GOLD_NUGGET) bet = 20;
            if (clicked.getType() == Material.GOLD_INGOT) bet = 100;
            if (clicked.getType() == Material.GOLD_BLOCK) bet = 200;

            if (bet > 0) {
                openSlotGUI(player, bet);
            }

        } else if (title.equals("§2Casino - Slot")) {
            e.setCancelled(true);

            if (clicked.getType() == Material.EMERALD_BLOCK) {
                if (!isSpinning.getOrDefault(player.getUniqueId(), false)) {
                    isSpinning.put(player.getUniqueId(), true);
                    startSlotAnimation(player);
                } else {
                    player.sendMessage("§eBitte warte bis der aktuelle Dreh beendet ist!");
                }
            }
        }
    }

    private void startSlotAnimation(Player player) {
        Inventory inv = player.getOpenInventory().getTopInventory();

        new BukkitRunnable() {
            final Random rand = new Random();
            final int[] slots = {10, 13, 16};
            int ticks = 0;

            @Override
            public void run() {
                ticks++;

                for (int slot : slots) {
                    Material symbol = slotSymbols.get(rand.nextInt(slotSymbols.size()));
                    ItemStack item = new ItemStack(symbol);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName("§f");
                    item.setItemMeta(meta);
                    inv.setItem(slot, item);
                }

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);

                if (ticks >= 20) {
                    this.cancel();
                    checkSlotResult(player, inv);
                    isSpinning.put(player.getUniqueId(), false);
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void checkSlotResult(Player player, Inventory inv) {

        Material left = inv.getItem(10).getType();
        Material mid = inv.getItem(13).getType();
        Material right = inv.getItem(16).getType();

        int bet = playerBets.getOrDefault(player.getUniqueId(), 0);
        double multiplier = 0;

        if (left == mid && mid == right) {
            multiplier = getMultiplier(left) * 3;
            player.sendMessage("§aJackpot! Dreifach " + left.name() + "! Multiplier: " + multiplier);
        } else if (left == mid || left == right || mid == right){
            player.sendMessage("§aEinsatz verdoppelt!");
            multiplier = 2;
        }else{
            player.sendMessage("§cLeider verloren...");
        }

        int payout = (int) (bet * multiplier);
        if (payout > 0) {
            player.sendMessage("§aDu gewinnst " + payout + "€!");
            moneyManager.addMoney(player.getUniqueId(), payout);
        }
    }

    private double getMultiplier(Material material) {
        return switch (material) {
            case DIAMOND -> 5.0;
            case EMERALD -> 4.0;
            case AMETHYST_SHARD -> 3.5;
            case GOLD_INGOT -> 3.0;
            case IRON_INGOT -> 2.0;
            case REDSTONE -> 1.5;
            case COPPER_INGOT -> 1.25;
            case APPLE -> 1.0;
            default -> 0;
        };
    }
}

