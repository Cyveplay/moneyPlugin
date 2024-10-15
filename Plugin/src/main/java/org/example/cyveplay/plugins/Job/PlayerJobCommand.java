package org.example.cyveplay.plugins.Job;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerJobCommand implements CommandExecutor {
    private final JobManager jobManager;
    private final PlayerJobManager playerJobManager;

    public PlayerJobCommand(JobManager jobManager, PlayerJobManager playerJobManager) {
        this.jobManager = jobManager;
        this.playerJobManager = playerJobManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID playerID = player.getUniqueId();

            if (args.length == 0) {
                player.sendMessage("Verfügbare Jobs: " + jobManager.getAvailableJobs());
                return true;
            }

            if (args[0].equalsIgnoreCase("annehmen") && args.length > 1) {
                Job job = jobManager.getJob(args[1]);
                if (job != null) {
                    playerJobManager.assignJobToPlayer(playerID, job);
                    player.sendMessage("Du hast den Job " + job.getTitle() + " angenommen.");
                } else {
                    player.sendMessage("Dieser Job existiert nicht.");
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("fortschritt")) {
                PlayerJob playerJob = playerJobManager.getPlayerJob(playerID);
                if (playerJob != null) {
                    player.sendMessage("Dein aktueller Fortschritt im Job " + playerJob.getJob().getTitle() + ": " + playerJob.getProgress());
                } else {
                    player.sendMessage("Du hast keinen aktiven Job.");
                }
                return true;
            }
        }
        return false;
    }
}

