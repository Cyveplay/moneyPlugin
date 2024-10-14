package org.example.cyveplay.plugins.Money;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.example.cyveplay.plugins.Permission.PermissionManager;

import java.util.ArrayList;
import java.util.List;

public class MoneyTabCompleter implements TabCompleter {

    private final PermissionManager permissionManager;

    // Konstruktor, um den PermissionManager zu initialisieren
    public MoneyTabCompleter(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        // Sicherstellen, dass der Befehl von einem Spieler kommt
        if (!(commandSender instanceof Player)) {
            return suggestions; // Keine Vorschläge, wenn der Sender kein Spieler ist
        }

        Player player = (Player) commandSender;

        // Überprüfen, ob der Spieler die Berechtigung "ManageMoney" hat
        if (permissionManager.hasPermission(player.getUniqueId(), "ManageMoney")) {

            // Vorschläge für das erste Argument
            if (args.length == 1) {
                if ("add".startsWith(args[0].toLowerCase())) {
                    suggestions.add("add");
                }
                if ("remove".startsWith(args[0].toLowerCase())) {
                    suggestions.add("remove");
                }
                if ("set".startsWith(args[0].toLowerCase())) {
                    suggestions.add("set");
                }
            }
        }

        // Vorschläge für das zweite Argument bei "pay" (Spielernamen)
        if (args.length == 1 && "pay".startsWith(args[0].toLowerCase())) {
            suggestions.add("pay");
        }

        // Vorschläge für das zweite Argument bei "pay" (Online-Spieler)
        if (args.length == 2 && args[0].equalsIgnoreCase("pay")) {
            for (Player targetPlayer : Bukkit.getOnlinePlayers()) {
                if (targetPlayer.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    suggestions.add(targetPlayer.getName());
                }
            }
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            for (Player targetPlayer : Bukkit.getOnlinePlayers()) {
                if (targetPlayer.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    suggestions.add(targetPlayer.getName());
                }
            }
        }

        return suggestions;
    }
}
