package com.crossge.necessities.Commands;

import com.crossge.necessities.Console;
import com.crossge.necessities.Utils;
import com.crossge.necessities.GetUUID;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.UserManager;
import com.crossge.necessities.Variables;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface Cmd {
    UserManager um = new UserManager();
    RankManager rm = new RankManager();
    Utils form = new Utils();
    Variables var = new Variables();
    Console console = new Console();
    GetUUID get = new GetUUID();

    boolean commandUse(CommandSender sender, String[] args);

    default List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}