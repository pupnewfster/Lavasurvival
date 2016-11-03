package com.crossge.necessities.Commands;

import com.crossge.necessities.*;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.UserManager;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface Cmd {
    UserManager um = Necessities.getInstance().getUM();
    RankManager rm = Necessities.getInstance().getRM();
    Variables var = Necessities.getInstance().getVar();
    Console console = Necessities.getInstance().getConsole();
    GetUUID get = Necessities.getInstance().getUUID();

    boolean commandUse(CommandSender sender, String[] args);

    default List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}