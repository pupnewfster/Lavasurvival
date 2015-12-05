package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.Commands.CmdHide;
import com.crossge.necessities.RankManager.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.UUID;

public class CmdWhois extends RankCmd {
    CmdHide hide = new CmdHide();

    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a player to view info of.");
            return true;
        }
        UUID uuid = get.getID(args[0]);
        if (uuid == null) {
            uuid = get.getOfflineID(args[0]);
            if (uuid == null) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That player has not joined the server. If the player is offline, please use the full and most recent name.");
                return true;
            }
        }
        User u = um.getUser(uuid);
        sender.sendMessage(var.getMessages() + "===== WhoIs: " + var.getObj() + u.getName() + var.getMessages() + " =====");
        if (u.getPlayer() != null)
            sender.sendMessage(var.getMessages() + " - Nick: " + ChatColor.RESET + u.getPlayer().getDisplayName());
        else {
            if (u.getNick() == null)
                sender.sendMessage(var.getMessages() + " - Name: " + ChatColor.RESET + u.getName());
            else
                sender.sendMessage(var.getMessages() + " - Nick: " + ChatColor.RESET + u.getNick());
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(Bukkit.getOfflinePlayer(uuid).getLastPlayed());
            String second = Integer.toString(c.get(Calendar.SECOND));
            String minute = Integer.toString(c.get(Calendar.MINUTE));
            String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
            String day = Integer.toString(c.get(Calendar.DATE));
            String month = Integer.toString(c.get(Calendar.MONTH) + 1);
            String year = Integer.toString(c.get(Calendar.YEAR));
            String date = month + "/" + day + "/" + year;
            hour = corTime(hour);
            minute = corTime(minute);
            second = corTime(second);
            String time = hour + ":" + minute + " and " + second + " second";
            if (Integer.parseInt(second) > 1)
                time = hour + ":" + minute + " and " + second + " seconds";
            sender.sendMessage(var.getMessages() + " - Seen last on " + ChatColor.RESET + date + " at " + time);
        }
        sender.sendMessage(var.getMessages() + " - Time played: " + ChatColor.RESET + u.getTimePlayed());
        sender.sendMessage(var.getMessages() + " - Rank: " + ChatColor.RESET + u.getRank().getName());
        String subranks = u.getSubranks();
        if (subranks != null)
            sender.sendMessage(var.getMessages() + " - Subranks: " + ChatColor.RESET + subranks);
        String permissions = u.getPermissions();
        if (permissions != null)
            sender.sendMessage(var.getMessages() + " - Permission nodes: " + ChatColor.RESET + permissions);
        if (u.getPlayer() != null) {
            Player p = u.getPlayer();
            sender.sendMessage(var.getMessages() + " - IP Adress: " + ChatColor.RESET + p.getAddress().toString().split("/")[1].split(":")[0]);
            String gamemode = "Survival";
            if (p.getGameMode() == GameMode.ADVENTURE)
                gamemode = "Adventure";
            else if (p.getGameMode() == GameMode.CREATIVE)
                gamemode = "Creative";
            sender.sendMessage(var.getMessages() + " - Gamemode: " + ChatColor.RESET + gamemode);
        }
        String banned = ChatColor.DARK_RED + "false";
        if (u.getPlayer() != null) {
            Player p = u.getPlayer();
            if (p.isBanned())
                banned = ChatColor.GREEN + "true";
            sender.sendMessage(var.getMessages() + " - Banned: " + banned);
            String visible = ChatColor.GREEN + "true";
            if (hide.isHidden(p))
                visible = ChatColor.DARK_RED + "false";
            sender.sendMessage(var.getMessages() + " - Visible: " + visible);
            String op = ChatColor.DARK_RED + "false";
            if (p.isOp())
                op = ChatColor.GREEN + "true";
            sender.sendMessage(var.getMessages() + " - OP: " + op);
            String flying = ChatColor.DARK_RED + "false" + ChatColor.RESET + " (not flying)";
            if (p.isFlying())
                flying = ChatColor.GREEN + "true " + ChatColor.RESET + " (flying)";
            sender.sendMessage(var.getMessages() + " - Fly mode: " + flying);
        } else {
            OfflinePlayer p = Bukkit.getOfflinePlayer(u.getUUID());
            if (p.isBanned())
                banned = ChatColor.GREEN + "true";
            sender.sendMessage(var.getMessages() + " - Banned: " + banned);
            String op = ChatColor.DARK_RED + "false";
            if (p.isOp())
                op = ChatColor.GREEN + "true";
            sender.sendMessage(var.getMessages() + " - OP: " + op);
        }
        String muted = ChatColor.DARK_RED + "false";
        if (u.isMuted())
            muted = ChatColor.GREEN + "true";
        sender.sendMessage(var.getMessages() + " - Muted: " + muted);
        return true;
    }

    private String corTime(String time) {
        if (time.length() == 1)
            return "0" + time;
        return time;
    }
}