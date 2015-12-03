package com.crossge.necessities.Commands;

import com.crossge.necessities.Console;
import com.crossge.necessities.GetUUID;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.UserManager;
import com.crossge.necessities.Variables;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Cmd {
    protected Variables var = new Variables();
    protected Console console = new Console();
    protected GetUUID get = new GetUUID();
    protected UserManager um = new UserManager();
    RankManager rm = new RankManager();

    public boolean commandUse(CommandSender sender, String[] args) {
        return false;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}