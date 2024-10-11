package org.example.cyveplay.plugins;

import java.util.HashMap;
import java.util.UUID;

public class MoneyManager {

    // Speichert das Geld der Spieler in einer HashMap (UUID des Spielers -> Geldwert)
    private HashMap<UUID, Double> playerBalances = new HashMap<>();

    // Setzt den Standardwert für Geld (z.B. 100)
    private final double DEFAULT_MONEY = 100.0;

    // Gibt das Geld eines Spielers zurück oder den Standardwert, wenn der Spieler noch keinen Eintrag hat
    public double getMoney(UUID playerUUID) {
        return playerBalances.getOrDefault(playerUUID, DEFAULT_MONEY);
    }

    // Setzt das Geld eines Spielers
    public void setMoney(UUID playerUUID, double amount) {
        playerBalances.put(playerUUID, amount);
    }

    // Füge einem Spieler Geld hinzu
    public void addMoney(UUID playerUUID, double amount) {
        double currentBalance = getMoney(playerUUID);
        playerBalances.put(playerUUID, currentBalance + amount);
    }

    // Ziehe einem Spieler Geld ab
    public void removeMoney(UUID playerUUID, double amount) {
        double currentBalance = getMoney(playerUUID);
        playerBalances.put(playerUUID, currentBalance - amount);
    }
    public void pay(UUID playerUUID, UUID receiverUUID, double amount) {
        double senderBalance = getMoney(playerUUID);
        double receiverBalance = getMoney(receiverUUID);


        if (senderBalance >= amount) {
            playerBalances.put(playerUUID, senderBalance - amount);
            playerBalances.put(receiverUUID, receiverBalance + amount);
        } else {
            System.out.println("Nicht genug Guthaben für diese Transaktion.");
        }
    }
}
