package com.crossge.necessities;

import org.bukkit.ChatColor;

public class Variables {
    private ChatColor messages = ChatColor.GOLD;
    private ChatColor me = ChatColor.DARK_PURPLE;
    private ChatColor error = ChatColor.RED;
    private ChatColor errorMsg = ChatColor.DARK_RED;
    private ChatColor plugincolor = ChatColor.DARK_RED;
    private ChatColor promoteMsg = ChatColor.GREEN;
    private ChatColor demoteMsg = ChatColor.RED;
    private ChatColor objectMsg = ChatColor.RED;

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