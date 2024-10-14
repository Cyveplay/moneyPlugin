package org.example.cyveplay.plugins.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionTabCompleter implements TabCompleter {

    private PermissionManager permissionManager;

    // Konstruktor für den PermissionManager
    public PermissionTabCompleter(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        // Argument 0: set/remove
        if (args.length == 1) {
            if ("set".startsWith(args[0].toLowerCase())) {
                completions.add("set");
            }
            if ("remove".startsWith(args[0].toLowerCase())) {
                completions.add("remove");
            }
        }

        // Argument 1: Zielspieler (nur online Spieler anzeigen)
        if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }

        // Argument 2: Berechtigungen (Liste von Berechtigungen)
        if (args.length == 3) {
            List<String> availablePermissions = permissionManager.getPossiblePermissions();
            for (String permission : availablePermissions) {
                if (permission.toLowerCase().startsWith(args[2].toLowerCase())) {
                    completions.add(permission);
                }
            }
        }

        return completions;
    }
}
