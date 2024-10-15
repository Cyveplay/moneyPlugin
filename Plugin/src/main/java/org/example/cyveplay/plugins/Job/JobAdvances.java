package org.example.cyveplay.plugins.Job;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class JobAdvances implements Listener {
    private PlayerJobManager playerJobManager;
    public JobAdvances(PlayerJobManager playerJobManager){
        this.playerJobManager = playerJobManager;
    }
    @EventHandler
    public void onFarm(BlockBreakEvent event){
        Player player = event.getPlayer();
            if (playerJobManager.getPlayerJob(player.getUniqueId()) != null) {
                PlayerJob playerJob = playerJobManager.getPlayerJob(player.getUniqueId());
                playerJobManager.updatePlayerProgress(player.getUniqueId(),10);
            }

    }
}
