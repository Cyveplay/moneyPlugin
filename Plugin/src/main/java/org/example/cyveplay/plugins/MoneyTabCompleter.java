package org.example.cyveplay.plugins;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MoneyTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1){
            if ("add".startsWith(args[0].toLowerCase())) {
                suggestions.add("add");
            }
            if ("remove".startsWith(args[0].toLowerCase())) {
                suggestions.add("remove");
            }
            if ("pay".startsWith(args[0].toLowerCase())) {
                suggestions.add("pay");
            }
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("pay")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    suggestions.add(player.getName());
                }
            }
        }

        return suggestions;
    }
}
