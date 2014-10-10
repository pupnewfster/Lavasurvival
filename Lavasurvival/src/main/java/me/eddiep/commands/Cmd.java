package me.eddiep.commands;

import java.util.List;

import me.eddiep.Lavasurvival;
import me.eddiep.ranks.RankManager;
import me.eddiep.ranks.UserManager;
import me.eddiep.ranks.UUIDs;
import org.bukkit.command.CommandSender;

public class Cmd {
    UserManager um = Lavasurvival.INSTANCE.getUserManager();
    RankManager rm = Lavasurvival.INSTANCE.getRankManager();
    UUIDs get = Lavasurvival.INSTANCE.getUUIDs();

    public boolean commandUse(CommandSender sender, String[] args) {
        return false;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}