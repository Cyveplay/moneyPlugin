package org.example.cyveplay.plugins.Market;

import org.bukkit.command.Command;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.cyveplay.plugins.Market.MarketManager;

import java.util.ArrayList;
import java.util.List;

public class MarketTabCompleter implements TabCompleter {

    private final MarketManager marketManager;

    public MarketTabCompleter(MarketManager marketManager) {
        this.marketManager = marketManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("add");
            completions.add("open");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("open")) {
                for (Player player : sender.getServer().getOnlinePlayers()) {
                    completions.add(player.getName());  // Vorschlag von Online-Spielernamen
                }
            }
        }

        return completions;
    }
}
