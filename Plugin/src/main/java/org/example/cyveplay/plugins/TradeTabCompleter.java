package org.example.cyveplay.plugins;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TradeTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Vorschläge für den ersten Parameter: send, accept, deny
            completions.add("send");
            completions.add("accept");
            completions.add("deny");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("send")) {
            // Vorschläge für den zweiten Parameter: Spielernamen bei "/trade send"
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }

        return completions;
    }
}
