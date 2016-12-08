package com.crossge.necessities.Commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.Utils;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Map;

public class CmdHelp implements Cmd {
    public boolean commandUse(CommandSender sender, String[] args) {
        ArrayList<String> helpList = new ArrayList<>();
        int page = 0;
        String search = "";
        if (args.length == 1) {
            if (!Utils.legalInt(args[0]))
                search = args[0];
            else
                page = Integer.parseInt(args[0]);
        }
        Variables var = Necessities.getInstance().getVar();
        if (args.length > 1) {
            if (!Utils.legalInt(args[1])) {
                sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a valid help page.");
                return true;
            }
            search = args[0];
            page = Integer.parseInt(args[1]);
        }
        if (args.length == 0 || page == 0)
            page = 1;
        int time = 0;
        ArrayList<String> plugins = new ArrayList<>(), commands = new ArrayList<>();
        boolean isConsole = !(sender instanceof Player);
        for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
            if (search.equals("") || p.getName().equalsIgnoreCase(search)) {
                if (p.getName().equalsIgnoreCase(search))
                    plugins.add(var.getMessages() + "Commands from " + p.getName() + ":");
                try {
                    Map<String, Map<String, Object>> cmds = p.getDescription().getCommands();
                    if (cmds != null)
                        for (String k : cmds.keySet()) {
                            PluginCommand pc = Bukkit.getPluginCommand(p.getName() + ":" + k);
                            if (pc != null && (isConsole || pc.testPermissionSilent(sender))) {
                                commands.add(var.getMessages() + "/" + pc.getName() + ChatColor.WHITE + ": " + pc.getDescription());
                                if (!plugins.contains(var.getPlugCol() + p.getName() + ChatColor.WHITE + ": Plugin Help: /help " + p.getName().toLowerCase()) && search.equals(""))
                                    plugins.add(var.getPlugCol() + p.getName() + ChatColor.WHITE + ": Plugin Help: /help " + p.getName().toLowerCase());
                            }
                        }
                } catch (Exception ignored) {
                }
            } else if (Bukkit.getPluginManager().getPlugin(search) == null) {
                if (p.getName().contains(search))
                    plugins.add(var.getPlugCol() + p.getName() + ChatColor.WHITE + ": Plugin Help: /help " + p.getName().toLowerCase());
                try {
                    Map<String, Map<String, Object>> cmds = p.getDescription().getCommands();
                    if (cmds != null)
                        for (String k : cmds.keySet()) {
                            PluginCommand pc = Bukkit.getPluginCommand(p.getName() + ":" + k);
                            if (pc != null && (isConsole || pc.testPermissionSilent(sender)) && (pc.getName().toLowerCase().contains(search.toLowerCase()) || pc.getDescription().toLowerCase().contains(search.toLowerCase()))) {
                                commands.add(var.getMessages() + "/" + pc.getName() + ChatColor.WHITE + ": " + pc.getDescription());
                                if (pc.getName().equalsIgnoreCase(search))
                                    commands.add(var.getMessages() + "Usage" + ChatColor.WHITE + ": " + pc.getUsage().replaceFirst("<command>", pc.getName()));
                            }
                        }
                } catch (Exception ignored) {
                }
            }
        }
        helpList.addAll(plugins);
        helpList.addAll(commands);
        int rounder = 0;
        if (helpList.size() % 10 != 0)
            rounder = 1;
        int totalPages = (helpList.size() / 10) + rounder;
        if (page > totalPages) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Input a number from 1 to " + Integer.toString(totalPages));
            return true;
        }
        if (search.equals(""))
            sender.sendMessage(ChatColor.YELLOW + " ---- " + var.getMessages() + "Help" + ChatColor.YELLOW + " -- " + var.getMessages() + "Page " +
                    ChatColor.RED + Integer.toString(page) + var.getMessages() + "/" + ChatColor.RED + Integer.toString(totalPages) + ChatColor.YELLOW + " ----");
        else
            sender.sendMessage(ChatColor.YELLOW + " ---- " + var.getMessages() + "Help: " + search + ChatColor.YELLOW + " -- " + var.getMessages() + "Page " +
                    ChatColor.RED + Integer.toString(page) + var.getMessages() + "/" + ChatColor.RED + Integer.toString(totalPages) + ChatColor.YELLOW + " ----");
        page = page - 1;
        String message = getHelp(page, time, helpList);
        while (message != null) {
            sender.sendMessage(message);
            time++;
            message = getHelp(page, time, helpList);
        }
        if (page + 1 < totalPages)
            sender.sendMessage(var.getMessages() + "Type " + ChatColor.RED + "/help " + Integer.toString(page + 2) + var.getMessages() + " to read the next page.");
        return true;
    }

    private String getHelp(int page, int time, ArrayList<String> helpList) {
        page *= 10;
        if (helpList.size() < time + page + 1 || time == 10)
            return null;
        return helpList.get(page + time);
    }
}