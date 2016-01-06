package com.crossge.necessities.Commands.RankManager;

import com.crossge.necessities.Commands.CmdHide;
import com.crossge.necessities.RankManager.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Calendar;
import java.util.UUID;

public class CmdWhois extends RankCmd {
    private File configFile = new File("plugins/Necessities", "config.yml");
    CmdHide hide = new CmdHide();

    public boolean commandUse(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a player to view info of.");
            return true;
        }
        UUID uuid = get.getID(args[0]);
        if (uuid == null)
            uuid = get.getOfflineID(args[0]);
        if (uuid == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "That player has not joined the server. If the player is offline, please use the full and most recent name.");
            return true;
        }
        User u = um.getUser(uuid);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        sender.sendMessage(var.getMessages() + "===== WhoIs: " + var.getObj() + u.getName() + var.getMessages() + " =====");
        if (u.getPlayer() != null)
            sender.sendMessage(var.getMessages() + " - Nick: " + ChatColor.RESET + u.getPlayer().getDisplayName());
        else {
            sender.sendMessage(var.getMessages() + (u.getNick() == null ? " - Name: " + ChatColor.RESET + u.getName() : " - Nick: " + ChatColor.RESET + u.getNick()));
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(Bukkit.getOfflinePlayer(uuid).getLastPlayed());
            String second = Integer.toString(c.get(Calendar.SECOND));
            String minute = Integer.toString(c.get(Calendar.MINUTE));
            String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
            String day = Integer.toString(c.get(Calendar.DATE));
            String month = Integer.toString(c.get(Calendar.MONTH) + 1);
            String year = Integer.toString(c.get(Calendar.YEAR));
            hour = corTime(hour);
            minute = corTime(minute);
            second = corTime(second);
            sender.sendMessage(var.getMessages() + " - Seen last on " + ChatColor.RESET + month + "/" + day + "/" + year + " at " + hour + ":" + minute + " and " + second + " " +
                    (Integer.parseInt(second) > 1 ? "seconds" : "second"));
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
            sender.sendMessage(var.getMessages() + " - Health: " + ChatColor.RESET + (int) p.getHealth() + "/" + (int) p.getMaxHealth());
            sender.sendMessage(var.getMessages() + " - Hunger: " + ChatColor.RESET + p.getFoodLevel() + "/20 (+" + (int) p.getSaturation() + " saturation)");
            sender.sendMessage(var.getMessages() + " - Exp: " + ChatColor.RESET + form.addCommas(p.getTotalExperience()) + " (Level " + p.getLevel() + ")");
            String location = "(" + p.getWorld().getName() + ", " + p.getLocation().getBlockX() + ", " + p.getLocation().getBlockY() + ", " + p.getLocation().getBlockZ() + ")";
            sender.sendMessage(var.getMessages() + " - Location: " + ChatColor.RESET + location);
        }
        if (u.getPlayer() != null) {
            Player p = u.getPlayer();
            sender.sendMessage(var.getMessages() + " - IP Adress: " + ChatColor.RESET + p.getAddress().toString().split("/")[1].split(":")[0]);
            String gamemode = "Survival";
            if (p.getGameMode().equals(GameMode.ADVENTURE))
                gamemode = "Adventure";
            else if (p.getGameMode().equals(GameMode.CREATIVE))
                gamemode = "Creative";
            else if (p.getGameMode().equals(GameMode.SPECTATOR))
                gamemode = "Spectator";
            sender.sendMessage(var.getMessages() + " - Gamemode: " + ChatColor.RESET + gamemode);
        }
        if (u.getPlayer() != null) {
            Player p = u.getPlayer();
            sender.sendMessage(var.getMessages() + " - Banned: " + (p.isBanned() ? ChatColor.GREEN + "true" : ChatColor.DARK_RED + "false"));
            sender.sendMessage(var.getMessages() + " - Visible: " + (hide.isHidden(p) ? ChatColor.DARK_RED + "false" : ChatColor.GREEN + "true"));
            sender.sendMessage(var.getMessages() + " - OP: " + (p.isOp() ? ChatColor.GREEN + "true" : ChatColor.DARK_RED + "false"));
            sender.sendMessage(var.getMessages() + " - Fly mode: " + (p.isFlying() ? ChatColor.GREEN + "true " + ChatColor.RESET + " (flying)" : ChatColor.DARK_RED + "false" + ChatColor.RESET + " (not flying)"));
        } else {
            OfflinePlayer p = Bukkit.getOfflinePlayer(u.getUUID());
            sender.sendMessage(var.getMessages() + " - Banned: " + (p.isBanned() ? ChatColor.GREEN + "true" : ChatColor.DARK_RED + "false"));
            sender.sendMessage(var.getMessages() + " - OP: " + (p.isOp() ? ChatColor.GREEN + "true" : ChatColor.DARK_RED + "false"));
        }
        sender.sendMessage(var.getMessages() + " - Muted: " + (u.isMuted() ? ChatColor.GREEN + "true" : ChatColor.DARK_RED + "false"));
        return true;
    }

    private String corTime(String time) {
        return time.length() == 1 ? "0" + time : time;
    }
}