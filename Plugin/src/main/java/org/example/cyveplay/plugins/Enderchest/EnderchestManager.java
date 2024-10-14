package org.example.cyveplay.plugins.Enderchest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.cyveplay.plugins.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EnderchestManager implements Listener {

    private final JavaPlugin plugin;
    private File enderchestFile;
    private FileConfiguration enderchestConfig;
    private final HashMap<UUID, Inventory> enderchests = new HashMap<>();

    public EnderchestManager(JavaPlugin plugin) {
        this.plugin = plugin;
        createEnderChestFile();
    }

    // Methode zum Öffnen der Enderchest
    public void openEnderChest(Player player) {
        UUID uuid = player.getUniqueId();
        Inventory enderchest;

        if (enderchests.containsKey(uuid)) {
            enderchest = enderchests.get(uuid);
        } else {
            // Enderchest aus der Datei laden
            enderchest = loadEnderChest(uuid);
            enderchests.put(uuid, enderchest);
        }


        // Enderchest dem Spieler öffnen
        player.openInventory(enderchest);
        player.playSound(player.getLocation(),Sound.BLOCK_ENDER_CHEST_OPEN,1.0f,1.0f);
    }


    private Inventory loadEnderChest(UUID uuid) {
        Inventory inventory = Bukkit.createInventory(null, 27, "Enderchest");

        if (enderchestConfig.contains(uuid.toString())) {
            for (int i = 0; i < 27; i++) {
                if (enderchestConfig.contains(uuid.toString() + "." + i)) {
                    inventory.setItem(i, enderchestConfig.getItemStack(uuid.toString() + "." + i));
                }
            }
        }

        return inventory;
    }
    public void loadAllEnderchests(List<UUID> playerUUIDs){
        for (UUID playerUUID : playerUUIDs){
            enderchests.put(playerUUID,loadEnderChest(playerUUID));
        }
    }



    private void saveEnderChest(UUID uuid, Inventory inventory) {
        for (int i = 0; i < 27; i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                enderchestConfig.set(uuid.toString() + "." + i, item);
            } else {
                enderchestConfig.set(uuid.toString() + "." + i, null);
            }
        }

        try {
            enderchestConfig.save(enderchestFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void saveAllEnderChests() {
        for (UUID uuid : enderchests.keySet()) {
            saveEnderChest(uuid, enderchests.get(uuid));
        }
    }


    private void createEnderChestFile() {
        enderchestFile = Utils.createFileIfMissing(new File(plugin.getDataFolder(), "enderchests.yml"));
        enderchestConfig = YamlConfiguration.loadConfiguration(enderchestFile);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (enderchests.containsKey(player.getUniqueId())) {
            saveEnderChest(player.getUniqueId(), enderchests.get(player.getUniqueId()));
            player.playSound(player.getLocation(),Sound.BLOCK_ENDER_CHEST_CLOSE,1.0f,1.0f);
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Nur reagieren, wenn der Spieler eine Enderchest öffnet
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            if (block != null && block.getType() == Material.ENDER_CHEST) {
                Player player = event.getPlayer();

                // Das Öffnen der normalen Enderchest verhindern
                event.setCancelled(true);

                // Künstliche Enderchest öffnen
                event.getPlayer().performCommand("enderchest");

                // Optional: Sound abspielen, wie wenn eine Enderchest geöffnet würde
                player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 1.0f);
            }
        }
    }
}
