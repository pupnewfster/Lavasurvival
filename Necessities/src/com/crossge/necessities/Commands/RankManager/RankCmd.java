package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.Commands.Cmd;
import com.crossge.necessities.Formatter;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.UserManager;
import org.bukkit.command.CommandSender;

public class RankCmd extends Cmd {
    RankManager rm = new RankManager();
    UserManager um = new UserManager();
    Formatter form = new Formatter();

    public boolean commandUse(CommandSender sender, String[] args) {
        return false;
    }
}