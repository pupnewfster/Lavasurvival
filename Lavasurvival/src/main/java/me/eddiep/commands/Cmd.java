package me.eddiep.commands;

import java.util.List;

import me.eddiep.ranks.RankManager;
import me.eddiep.ranks.UserManager;
import me.eddiep.ranks.UUIDs;
import org.bukkit.command.CommandSender;

public class Cmd {
    UserManager um = new UserManager();
    RankManager rm = new RankManager();
    UUIDs get = new UUIDs();

    public boolean commandUse(CommandSender sender, String[] args) {
        return false;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}