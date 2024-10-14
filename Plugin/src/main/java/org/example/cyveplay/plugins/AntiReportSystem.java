package org.example.cyveplay.plugins;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AntiReportSystem implements Listener {
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){
        String Message = event.getMessage();
        String PlayerName = event.getPlayer().getName();
        event.setCancelled(true);
        Utils.sendMessageToAllPlayers("<" + PlayerName + "> " + Message);
    }
}
