package me.eddiep.minecraft.ls.commands;

import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.ranks.RankManager;
import me.eddiep.minecraft.ls.ranks.UUIDs;
import me.eddiep.minecraft.ls.ranks.UserManager;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class Cmd {
    UserManager um = Lavasurvival.INSTANCE.getUserManager();
    RankManager rm = Lavasurvival.INSTANCE.getRankManager();
    UUIDs get = Lavasurvival.INSTANCE.getUUIDs();

    public boolean commandUse(CommandSender sender, String[] args) {
        return false;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    public abstract String getName();
}