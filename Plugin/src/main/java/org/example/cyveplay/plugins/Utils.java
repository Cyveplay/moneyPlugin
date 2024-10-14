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
        if(!file.exists()) {
            try {
                if(file.createNewFile()) {
                    System.out.println("Created '"+file.getName()+"'");
                } else {
                    System.out.println("Could not create '"+file.getName()+"'");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
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
