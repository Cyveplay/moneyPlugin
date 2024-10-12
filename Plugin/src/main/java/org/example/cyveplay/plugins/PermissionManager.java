package org.example.cyveplay.plugins;

import java.lang.reflect.Array;
import java.util.*;

public class PermissionManager {

    // HashMap, um die Berechtigungen der Spieler zu speichern (UUID -> List von Berechtigungen)
    private HashMap<UUID, List<String>> playerPermissions = new HashMap<>();

    private List <String> Permissions = Arrays.asList("ManageMoney");

    // Methode, um einem Spieler eine Berechtigung hinzuzufügen
    public void addPermission(UUID playerUUID, String permission) {
        playerPermissions
                .computeIfAbsent(playerUUID, k -> new ArrayList<>())  // Falls noch nicht vorhanden, wird eine neue Liste erzeugt
                .add(permission);  // Die Berechtigung zur Liste hinzufügen
    }

    // Methode, um einem Spieler alle Berechtigungen hinzuzufügen
    public void addPermissions(UUID playerUUID, List<String> permissions) {
        playerPermissions
                .computeIfAbsent(playerUUID, k -> new ArrayList<>())  // Falls noch nicht vorhanden, wird eine neue Liste erzeugt
                .addAll(permissions);  // Alle Berechtigungen hinzufügen
    }

    // Methode, um die Berechtigungen eines Spielers zu entfernen
    public void removePermission(UUID playerUUID, String permission) {
        List<String> permissions = playerPermissions.get(playerUUID);
        if (permissions != null) {
            permissions.remove(permission);  // Die Berechtigung entfernen
        }
    }

    // Methode, um alle Berechtigungen eines Spielers zu entfernen
    public void removeAllPermissions(UUID playerUUID) {
        playerPermissions.remove(playerUUID);  // Alle Berechtigungen entfernen
    }

    // Methode, um zu prüfen, ob ein Spieler eine bestimmte Berechtigung hat
    public boolean hasPermission(UUID playerUUID, String permission) {
        List<String> permissions = playerPermissions.getOrDefault(playerUUID, new ArrayList<>());
        return permissions.contains(permission);
    }


    // Methode, um zu überprüfen, ob der Spieler eine der angegebenen Berechtigungen hat
    public boolean doesPermissionExist(String permission) {
        return Permissions.contains(permission);
    }
    // Methode, um alle Berechtigungen eines Spielers zu bekommen
    public List<String> getPermissions(UUID playerUUID) {
        return playerPermissions.getOrDefault(playerUUID, new ArrayList<>());  // Gibt die Liste der Berechtigungen zurück oder eine leere Liste
    }
    public List <String> getPossiblePermissions(){
        return Permissions;
    }
}
