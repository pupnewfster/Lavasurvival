package com.crossge.necessities;

import org.bukkit.ChatColor;

public class Variables {
    private ChatColor messages = ChatColor.GOLD, me = ChatColor.DARK_PURPLE, error = ChatColor.RED, errorMsg = ChatColor.DARK_RED, plugincolor = ChatColor.DARK_RED, promoteMsg = ChatColor.GREEN,
            demoteMsg = ChatColor.RED, objectMsg = ChatColor.RED;

    public ChatColor getMe() {
        return me;
    }

    public ChatColor getObj() {
        return objectMsg;
    }

    public ChatColor getPlugCol() {
        return plugincolor;
    }

    public ChatColor getDemote() {
        return demoteMsg;
    }

    public ChatColor getPromote() {
        return promoteMsg;
    }

    public ChatColor getMessages() {
        return messages;
    }

    public ChatColor getEr() {
        return error;
    }

    public ChatColor getErMsg() {
        return errorMsg;
    }
}