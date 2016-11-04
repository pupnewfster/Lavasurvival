package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.Rank;
import com.crossge.necessities.RankManager.RankManager;
import com.crossge.necessities.RankManager.User;
import com.crossge.necessities.RankManager.UserManager;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CmdWho implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        RankManager rm = Necessities.getInstance().getRM();
        UserManager um = Necessities.getInstance().getUM();
        if (sender instanceof Player && !sender.hasPermission("Necessities.seehidden")) {
            HashMap<Rank, String> online = new HashMap<>();
            int numbOnline = 1;
            if (!rm.getOrder().isEmpty())
                online.put(rm.getRank(rm.getOrder().size() - 1), rm.getRank(rm.getOrder().size() - 1).getColor() + "Janet, ");
            if (!um.getUsers().isEmpty()) {
                for (User u : um.getUsers().values())
                    if (!Necessities.getInstance().getHide().isHidden(u.getPlayer())) {
                        online.put(u.getRank(), online.containsKey(u.getRank()) ? online.get(u.getRank()) + u.getPlayer().getDisplayName() + ", " : u.getPlayer().getDisplayName() + ", ");
                        numbOnline++;
                    }
            }
            sender.sendMessage(var.getMessages() + "There " + amount(numbOnline) + " " + var.getObj() + numbOnline + var.getMessages() + " out of a maximum " + var.getObj() + Bukkit.getMaxPlayers() +
                    var.getMessages() + " players online.");
            for (int i = rm.getOrder().size() - 1; i >= 0; i--) {
                Rank r = rm.getRank(i);
                if (online.containsKey(r))
                    sender.sendMessage(r.getColor() + r.getName() + "s: " + ChatColor.WHITE + online.get(r).trim().substring(0, online.get(r).length() - 2));
            }
            return true;
        }
        int numbOnline = Bukkit.getOnlinePlayers().size() + 1;
        sender.sendMessage(var.getMessages() + "There " + amount(numbOnline) + " " + var.getObj() + numbOnline + var.getMessages() + " out of a maximum " +
                var.getObj() + Bukkit.getMaxPlayers() + var.getMessages() + " players online.");
        HashMap<Rank, String> online = new HashMap<>();
        if (!rm.getOrder().isEmpty())
            online.put(rm.getRank(rm.getOrder().size() - 1), rm.getRank(rm.getOrder().size() - 1).getColor() + "Janet, ");
        if (!um.getUsers().isEmpty())
            for (User u : um.getUsers().values())
                if (Necessities.getInstance().getHide().isHidden(u.getPlayer()))
                    online.put(u.getRank(), online.containsKey(u.getRank()) ? online.get(u.getRank()) + "[HIDDEN]" + u.getPlayer().getDisplayName() + ", " : "[HIDDEN]" + u.getPlayer().getDisplayName() + ", ");
                else
                    online.put(u.getRank(), online.containsKey(u.getRank()) ? online.get(u.getRank()) + u.getPlayer().getDisplayName() + ", " : u.getPlayer().getDisplayName() + ", ");
        for (int i = rm.getOrder().size() - 1; i >= 0; i--) {
            Rank r = rm.getRank(i);
            if (online.containsKey(r))
                sender.sendMessage(r.getColor() + r.getName() + "s: " + ChatColor.WHITE + online.get(r).trim().substring(0, online.get(r).length() - 2));
        }
        return true;
    }

    private String amount(int a) {
        return a == 1 ? "is" : "are";
    }
}