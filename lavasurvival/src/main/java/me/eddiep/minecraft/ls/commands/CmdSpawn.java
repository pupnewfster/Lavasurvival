package me.eddiep.minecraft.ls.commands;

import me.eddiep.ClassicPhysics;
import me.eddiep.PhysicsEngine;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.game.LavaMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSpawn implements Cmd {
    @Override
    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            LavaMap map = Gamemode.getCurrentMap();
            if (map == null) {
                sender.sendMessage(ChatColor.DARK_RED + "Error: there is no game running.");
                return true;
            }
            PhysicsEngine pe = ClassicPhysics.getPhysicsEngine();
            if (Gamemode.getCurrentGame().isAlive(player) && (pe.isClassicBlock(player.getLocation()) || pe.isClassicBlock(player.getEyeLocation()))) {
                sender.sendMessage(ChatColor.DARK_RED + "Error: you are in the lava.");
                return true;
            }
            player.teleport(map.getMapSpawn().toLocation(map.getWorld()));
        } else
            sender.sendMessage(ChatColor.DARK_RED + "This command can only be used in game..");
        return true;
    }

    @Override
    public String getName() {
        return "spawn";
    }
}
