package org.example.cyveplay.plugins.Enderchest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderchestCommand implements CommandExecutor {

    private final EnderchestManager enderChestManager;

    public EnderchestCommand(EnderchestManager enderchestManager) {
        this.enderChestManager = enderchestManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            enderChestManager.openEnderChest(player);
        } else {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler verwendet werden.");
        }
        return true;
    }
}
