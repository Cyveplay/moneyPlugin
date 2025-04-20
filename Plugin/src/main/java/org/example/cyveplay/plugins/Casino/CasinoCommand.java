
// 2. CasinoCommand.java
package org.example.cyveplay.plugins.Casino;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CasinoCommand implements CommandExecutor {

    private final CasinoManager casinoManager;

    public CasinoCommand(CasinoManager manager) {
        this.casinoManager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl ausführen.");
            return true;
        }

        Player player = (Player) sender;
        casinoManager.openBetGUI(player);
        return true;
    }
}