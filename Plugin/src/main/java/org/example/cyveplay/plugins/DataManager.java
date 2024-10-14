package org.example.cyveplay.plugins;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataManager {

    private File moneyFile;
    private FileConfiguration moneyConfig;

    private File permissionFile;
    private FileConfiguration permissionConfig;

    private final JavaPlugin plugin;

    public DataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadFiles(); // Lädt oder erstellt die Dateien beim Start des Plugins
    }

    // Methode zum Laden oder Erstellen der YAML-Dateien
    public void loadFiles() {
        // Geld-Datei laden oder erstellen
        moneyFile = Utils.createFileIfMissing(new File(plugin.getDataFolder(), "money.yml"));
        moneyConfig = YamlConfiguration.loadConfiguration(moneyFile);

        // Berechtigungen-Datei laden oder erstellen
        permissionFile = Utils.createFileIfMissing(new File(plugin.getDataFolder(), "permissions.yml"));
        permissionConfig = YamlConfiguration.loadConfiguration(permissionFile);
    }

    // Methode zum Speichern des Geldbetrags eines Spielers
    public void saveMoney(UUID playerUUID, double amount) {
        moneyConfig.set(playerUUID.toString(), amount);
        saveFile(moneyFile, moneyConfig);
    }

    // Methode zum Abrufen des Geldbetrags eines Spielers
    public double loadMoney(UUID playerUUID) {
        return moneyConfig.getDouble(playerUUID.toString(), 0.0); // 0.0 ist der Standardwert, falls nichts gefunden wird
    }

    // Methode zum Speichern der Berechtigungen eines Spielers
    public void savePermissions(UUID playerUUID, List<String> permissions) {
        permissionConfig.set(playerUUID.toString(), permissions);
        saveFile(permissionFile, permissionConfig);
    }

    // Methode zum Abrufen der Berechtigungen eines Spielers
    public List<String> loadPermissions(UUID playerUUID) {
        return permissionConfig.getStringList(playerUUID.toString());
    }

    // Hilfsmethode zum Speichern der Konfiguration
    private void saveFile(File file, FileConfiguration config) {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<UUID> getAllPlayerUUIDs() {
        List<UUID> uuids = new ArrayList<>();
        for (String key : moneyConfig.getKeys(false)) {
            uuids.add(UUID.fromString(key)); // Holt alle UUIDs aus der money.yml
        }
        return uuids;
    }

}
