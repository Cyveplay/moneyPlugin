package org.example.cyveplay.plugins;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

/**
 * Enthält funktionen, um die Entwicklung des Plugins zu erleichtern
 */
public class Utils {

    //Erstellt die angegebene datei, wenn sie noch nicht existiert
    public static File createFileIfMissing(File file) {
        if (!file.exists()) {
            try {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    if (parent.mkdirs()) {
                        System.out.println("Created parent directories for '" + file.getName() + "'");
                    } else {
                        System.out.println("Could not create parent directories for '" + file.getName() + "'");
                    }
                }

                if (file.createNewFile()) {
                    System.out.println("Created '" + file.getName() + "'");
                } else {
                    System.out.println("Could not create '" + file.getName() + "'");
                }
            } catch (IOException e) {
                System.err.println("Error while creating file '" + file.getName() + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
        return file;
    }


    //Sendet eine nachricht an alle Spieler
    public static void sendMessageToAllPlayers(String string) {
        for (Player player : Bukkit.getOnlinePlayers()){
            player.sendMessage(string);
        }
    }
}
