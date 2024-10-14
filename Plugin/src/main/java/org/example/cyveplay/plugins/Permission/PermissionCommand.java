package org.example.cyveplay.plugins.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PermissionCommand implements CommandExecutor {

    private PermissionManager permissionManager;

    // Konstruktor, der den PermissionManager empfängt
    public PermissionCommand(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        // Überprüfen, ob der Sender ein Spieler ist
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Dieser Befehl kann nur von Spielern ausgeführt werden.");
            return false;
        }

        Player player = (Player) commandSender;

        // Überprüfen, ob der Spieler die richtigen Argumente übergibt
        if (args.length != 3) {
            player.sendMessage(ChatColor.RED + "Falsche Syntax! Verwende /permission [set/remove] {Spielername} {Permission}");
            return false;
        }

        // Überprüfen, ob der Spieler Admin-Rechte hat
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "Nur Admins können diesen Befehl ausführen!");
            return false;
        }

        // Zielspieler ermitteln
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Spieler nicht gefunden!");
            return false;
        }

        UUID targetUUID = target.getUniqueId();
        String permission = args[2];  // Die Berechtigung, die zugewiesen oder entfernt werden soll

        // Überprüfen, ob die Berechtigung existiert
        if (!(permissionManager.doesPermissionExist(permission))) {
            player.sendMessage(ChatColor.RED + "Diese Berechtigung existiert nicht! Berechtigungen: "+ permissionManager.getPossiblePermissions() );
            return false;
        }


            // Befehl "set" oder "remove" ausführen
            if (args[0].equalsIgnoreCase("set")) {
                permissionManager.addPermission(targetUUID, permission);
                player.sendMessage(ChatColor.GREEN + "Die Berechtigung " + permission + " wurde dem Spieler " + target.getName() + " zugewiesen.");
            } else if (args[0].equalsIgnoreCase("remove")) {
                permissionManager.removePermission(targetUUID, permission);
                player.sendMessage(ChatColor.GREEN + "Die Berechtigung " + permission + " wurde dem Spieler " + target.getName() + " entfernt.");
            } else {
                player.sendMessage(ChatColor.RED + "Ungültiger Befehl! Verwende /permission [set/remove] {Spielername} {Permission}");
                return false;
            }


        return true;
    }
}
