package org.example.cyveplay.plugins.Market;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.cyveplay.plugins.Money.MoneyManager;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MarketManager implements Listener {

    private final JavaPlugin plugin;
    private final MoneyManager moneyManager;
    private final Map<String, Inventory> playerShops = new HashMap<>(); // Speichert die Shops der Spieler
    private File shopFile;
    private YamlConfiguration shopConfig;

    public MarketManager(JavaPlugin plugin, MoneyManager moneyManager) {
        this.plugin = plugin;
        this.moneyManager = moneyManager;

        // Datei laden/erstellen, die die Shops speichert
        shopFile = new File(plugin.getDataFolder(), "shops.yml");
        if (!shopFile.exists()) {
            try {
                shopFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        shopConfig = YamlConfiguration.loadConfiguration(shopFile);

        // Event Registrierung
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // Fügt das Item des Spielers in den Shop hinzu
    public void addItemToShop(Player player, double price) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "Du hast kein Item in der Hand!");
            return;
        }

        String playerName = player.getName();
        Inventory shopInventory = playerShops.getOrDefault(playerName, Bukkit.createInventory(null, 27, ChatColor.GREEN + playerName + "'s Shop"));

        //Überprüfe, ob im Shop noch Platz ist
        if(!this.itemFitsMarket(item, shopInventory)) {
            player.sendMessage(ChatColor.RED + "Im Shop ist kein Platz mehr für dieses Item!");
            return;
        }

        // Setze die Preis-Lore
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Preis: " + price + " Münzen");
        meta.setLore(lore);
        item.setItemMeta(meta);

        // Entferne das Item aus der Hand des Spielers
        player.getInventory().setItemInMainHand(null);

        // Füge das Item dem Shop des Spielers hinzu
        shopInventory.addItem(item);
        playerShops.put(playerName, shopInventory);

        // Speichere den Shop in der Datei
        saveShop(playerName);

        player.sendMessage(ChatColor.GREEN + "Dein Item wurde erfolgreich zu deinem Shop hinzugefügt!");
    }

    //Überprüft, ob das Item in dem Markt passt
    private boolean itemFitsMarket(ItemStack item, Inventory shopInventory) {
        int spaceForItemStackType = 0;
        for(int i = 0; i < shopInventory.getSize(); i++) {
            ItemStack shopItem = shopInventory.getItem(i);
            if(shopItem != null) {
                Material shopItemMat = shopItem.getType();
                if(shopItemMat == Material.AIR) {
                    spaceForItemStackType += shopItem.getMaxStackSize();
                } else if(shopItemMat == item.getType()) {
                    spaceForItemStackType += shopItem.getMaxStackSize()-shopItem.getAmount();
                }
            } else spaceForItemStackType += item.getMaxStackSize();
        }
        return spaceForItemStackType >= item.getAmount();
    }

    // Öffnet den Shop eines anderen Spielers
    public void openShop(Player viewer, String ownerName) {
        Inventory shopInventory = loadShop(ownerName);

        if (shopInventory == null) {
            viewer.sendMessage(ChatColor.RED + "Dieser Spieler hat keinen Shop oder existiert nicht.");
        } else {
            viewer.openInventory(shopInventory);
        }
    }

    // Event, um den Kauf von Items zu handhaben
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null || !playerShops.containsValue(inventory)) {
            return; // Kein Shop-Inventar
        }

        event.setCancelled(true); // Keine Bewegung der Items im Shop erlauben

        Player buyer = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            return;
        }

        // Extrahiere den Preis aus der Lore
        String priceLine = item.getItemMeta().getLore().get(0);
        double price = Double.parseDouble(ChatColor.stripColor(priceLine).replace("Preis: ", "").replace(" Münzen", ""));

        if (moneyManager.getMoney(buyer.getUniqueId()) >= price) {
            moneyManager.removeMoney(buyer.getUniqueId(), price);

            String ownerName = ChatColor.stripColor(event.getView().getTitle().replace("'s Shop", ""));
            Player owner = Bukkit.getPlayer(ownerName);
            UUID ownerUUID = (owner != null) ? owner.getUniqueId() : UUID.fromString(shopConfig.getString(ownerName + ".uuid"));

            // Geld an den Besitzer geben
            moneyManager.addMoney(ownerUUID, price);

            // Kopiere die Metadaten und gebe das Item an den Käufer
            ItemStack itemForBuyer = new ItemStack(item.getType(), item.getAmount());
            ItemMeta originalMeta = item.getItemMeta();
            if (originalMeta != null) {
                // Entferne nur die Preis-Lore
                List<String> newLore = new ArrayList<>();
                for (String loreLine : originalMeta.getLore()) {
                    if (!loreLine.startsWith(ChatColor.GOLD + "Preis:")) {
                        newLore.add(loreLine);
                    }
                }
                originalMeta.setLore(newLore);

                // Setze die neuen Metadaten
                itemForBuyer.setItemMeta(originalMeta.clone()); // Metadaten (z.B. Verzauberungen, Name, andere Lore) beibehalten
            }

            buyer.getInventory().addItem(itemForBuyer);
            buyer.sendMessage(ChatColor.GREEN + "Du hast " + item.getType() + " für " + price + " Münzen gekauft!");

            // Entferne das Item aus dem Shop
            //inventory.remove(item);
            inventory.setItem(event.getSlot(), new ItemStack(Material.AIR));
            saveShop(ownerName); // Speicher das Inventar des Besitzers nach dem Verkauf
        } else {
            buyer.sendMessage(ChatColor.RED + "Du hast nicht genug Geld!");
        }
    }

    // Speichert den Shop eines Spielers in der Datei
    public void saveShop(String playerName) {
        Inventory inventory = playerShops.get(playerName);

        // Überprüfen, ob das Inventar null ist
        if (inventory == null) {
            System.out.println("Kein Inventar gefunden für Spieler: " + playerName);
            return; // Beende die Methode, wenn kein Inventar vorhanden ist
        }

        shopConfig.set(playerName + ".items", null); // Altes Inventar löschen

        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                shopConfig.set(playerName + ".items." + UUID.randomUUID(), item);
            }
        }
        shopConfig.set(playerName + ".uuid", Bukkit.getOfflinePlayer(playerName).getUniqueId().toString());

        try {
            shopConfig.save(shopFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Lädt den Shop eines Spielers aus der Datei
    private Inventory loadShop(String playerName) {
        if (!shopConfig.contains(playerName + ".items")) {
            return null; // Kein Shop vorhanden
        }

        Inventory shopInventory = Bukkit.createInventory(null, 27, ChatColor.GREEN + playerName + "'s Shop");

        for (String key : shopConfig.getConfigurationSection(playerName + ".items").getKeys(false)) {
            ItemStack item = shopConfig.getItemStack(playerName + ".items." + key);
            shopInventory.addItem(item);
        }

        playerShops.put(playerName, shopInventory);
        return shopInventory;
    }
}
