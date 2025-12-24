package org.example.cyveplay.plugins.Bounty;

import com.google.gson.Gson;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.cyveplay.plugins.Money.MoneyManager;
import org.example.cyveplay.plugins.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

class Bounty {
    private UUID setterUUID;
    private UUID targetUUID;
    private int amount;

    public UUID getTargetUUID() {
        return this.targetUUID;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setTargetUUID(UUID targetUUID) {
        this.targetUUID = targetUUID;
    }

    public UUID getSetterUUID() {
        return setterUUID;
    }

    public void setSetterUUID(UUID setterUUID) {
        this.setterUUID = setterUUID;
    }

    public Bounty(UUID setterUUID, UUID targetUUID, int amount) {
        this.amount = amount;
        this.targetUUID = targetUUID;
        this.setterUUID = setterUUID;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("setterUUID", setterUUID.toString());
        data.put("targetUUID", targetUUID.toString());
        data.put("amount", amount);
        return data;
    }


    public static Bounty deserialize(Map<String, Object> data) {
        UUID setterUUID = UUID.fromString((String) data.get("setterUUID"));
        UUID targetUUID = UUID.fromString((String) data.get("targetUUID"));
        int amount = (int) data.get("amount");
        return new Bounty(setterUUID, targetUUID, amount);
    }
}

public class BountyManager implements Listener {

    private static BountyList bountyList = new BountyList();
    private MoneyManager moneyManager;
    private JavaPlugin plugin;

    private static File bountyFile;
    private static String savePath;

    public BountyManager(MoneyManager moneyManager, JavaPlugin plugin) {
        this.moneyManager = moneyManager;
        this.plugin = plugin;
        bountyFile = Utils.createFileIfMissing(new File(plugin.getDataFolder(), "bounties.yml"));
        savePath = new File(plugin.getDataFolder(), "bounties.yml").getAbsolutePath();
    }

    public static void saveBountyList() {

        try {
            // bountyConfig.save(bountyFile);

            String s = new Gson().toJson(bountyList);
            Files.writeString(new File(savePath).toPath(), s);

        } catch (IOException e) {
            System.out.println("Datei konnte nicht gespeichert werden! :( ");
        }
    }

    public static void loadBountyList() {

        try {
            String s = Files.readString(new File(savePath).toPath());
           bountyList = new Gson().fromJson(s, BountyList.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public void addBounty(UUID setterUUID, UUID targetUUID, int amount) {

        for (Bounty bounty : bountyList.listItems) {
            if (targetUUID == bounty.getTargetUUID()) {
                Bukkit.getPlayer(setterUUID).sendMessage(ChatColor.RED + "Dieser Spieler ist bereits ein Ziel!");
                return;
            }
        }
        bountyList.getListItems().add(new Bounty(setterUUID, targetUUID, amount));
        moneyManager.removeMoney(setterUUID, amount);
    }

    @EventHandler
    public void rewardBounty(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Player victim = event.getEntity().getPlayer();

        plugin.getLogger().info("Opferbelohnungevent wurde initialisiert!");

        if (killer == null) {
            return;
        }
        if (victim == null) {
            return;
        }

        for (Bounty bounty : bountyList.getListItems()
         ) {
            plugin.getLogger().info("Suche nach Opfer " + victim.getName());
            if (bounty.getTargetUUID().equals(victim.getUniqueId())) {
                plugin.getLogger().info("Opfer ist in der Liste vorhanden!");
                moneyManager.addMoney(killer.getUniqueId(), bounty.getAmount());
                bountyList.listItems.remove(bounty);
                killer.sendMessage("Du hast Das Kopfgeld in Höhe von " + bounty.getAmount(), "von " + victim.getName() + " bekommen!");
            }
        }
    }

    public static Inventory openChestGUI() {
        Inventory inventory = Bukkit.createInventory(null, 27, "Bounty Board");


        int it = 0;

        for (Bounty bounty : bountyList.listItems) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(bounty.getTargetUUID());
            ItemStack curHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta curMeta = (SkullMeta) curHead.getItemMeta();
            curMeta.setOwningPlayer(player);
            curMeta.setDisplayName(player.getName());
            List lore = new ArrayList<String>();
            lore.add("Betrag: " + bounty.getAmount());
            curMeta.setLore(lore);
            curHead.setItemMeta(curMeta);
            inventory.setItem(it, curHead);
            it++;
        }
        return inventory;
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();

        ItemStack skull = inventory.getItem(0);

        if (!(skull == null)) {
            if (skull.hasItemMeta()) {
                event.setCancelled(true);
            }
        }
    }
}



