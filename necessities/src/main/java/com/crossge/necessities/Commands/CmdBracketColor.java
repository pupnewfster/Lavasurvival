package com.crossge.necessities.Commands;

import com.crossge.necessities.GetUUID;
import com.crossge.necessities.Necessities;
import com.crossge.necessities.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CmdBracketColor implements Cmd {
    private File configFileTitles = new File("plugins/Necessities", "titles.yml");

    public boolean commandUse(CommandSender sender, String[] args) {
        Variables var = Necessities.getInstance().getVar();
        if (args.length == 0 || (args.length > 1 && args[1].length() > 1) || (args.length == 1 && args[0].length() > 1)) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "You must enter a player and the color for their brackets.");
            sender.sendMessage(var.getMessages() + "Valid colors are: " + ChatColor.translateAlternateColorCodes('&', "&00&11&22&33&44&55&66&77&88&99&aa&bb&cc&dd&ee&ff"));
            return true;
        }
        GetUUID get = Necessities.getInstance().getUUID();
        UUID uuid = get.getID(args[0]);
        if (uuid == null) {
            sender.sendMessage(var.getEr() + "Error: " + var.getErMsg() + "Invalid player.");
            return true;
        }
        Player target = Bukkit.getPlayer(uuid);
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (target != p && !p.hasPermission("Necessities.bracketOthers"))
                target = p;
        }
        YamlConfiguration configTitles = YamlConfiguration.loadConfiguration(configFileTitles);
        if (args.length == 1) {
            configTitles.set(target.getUniqueId() + ".color", "r");
            try {
                configTitles.save(configFileTitles);
            } catch (Exception ignored) {
            }
            sender.sendMessage(var.getMessages() + "Bracket color reset for player " + var.getObj() + target.getName());
            return true;
        }
        ChatColor color = ChatColor.getByChar(args[1]);
        configTitles.set(target.getUniqueId() + ".color", args[1]);
        try {
            configTitles.save(configFileTitles);
        } catch (Exception ignored) {
        }
        sender.sendMessage(var.getMessages() + "Bracket color changed to " + color + "this" + var.getMessages() + " for player " + var.getObj() + target.getName());
        return true;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> complete = new ArrayList<>();
        String search = "";
        if (args.length > 0)
            search = args[args.length - 1];
        if (args.length == 1) {
            if (sender instanceof Player) {
                for (Player p : Bukkit.getOnlinePlayers())
                    if (p.getName().startsWith(search) && ((Player) sender).canSee(p))
                        complete.add(p.getName());
            } else
                for (Player p : Bukkit.getOnlinePlayers())
                    if (p.getName().startsWith(search))
                        complete.add(p.getName());
        } else {
            search = search.replaceAll("_", "").toLowerCase();
            if (search.equals("") || search.equals("0") || search.equals("black"))
                complete.add("0");
            if (search.equals("") || search.equals("1") || search.equals("darkblue") || search.equals("navy"))
                complete.add("1");
            if (search.equals("") || search.equals("2") || search.equals("darkgreen") || search.equals("green"))
                complete.add("2");
            if (search.equals("") || search.equals("3") || search.equals("darkaqua") || search.equals("cyan"))
                complete.add("3");
            if (search.equals("") || search.equals("4") || search.equals("darkred") || search.equals("maroon"))
                complete.add("4");
            if (search.equals("") || search.equals("5") || search.equals("darkpurple") || search.equals("purple"))
                complete.add("5");
            if (search.equals("") || search.equals("6") || search.equals("gold") || search.equals("orange"))
                complete.add("6");
            if (search.equals("") || search.equals("7") || search.equals("lightgray"))
                complete.add("7");
            if (search.equals("") || search.equals("8") || search.equals("darkgray") || search.equals("gray"))
                complete.add("8");
            if (search.equals("") || search.equals("9") || search.equals("blue") || search.equals("lightblue"))
                complete.add("9");
            if (search.equals("") || search.equals("a") || search.equals("lime") || search.equals("lightgreen"))
                complete.add("a");
            if (search.equals("") || search.equals("b") || search.equals("aqua"))
                complete.add("b");
            if (search.equals("") || search.equals("c") || search.equals("red") || search.equals("lightred"))
                complete.add("c");
            if (search.equals("") || search.equals("d") || search.equals("pink") || search.equals("lightpurple"))
                complete.add("d");
            if (search.equals("") || search.equals("e") || search.equals("yellow"))
                complete.add("e");
            if (search.equals("") || search.equals("f") || search.equals("white"))
                complete.add("f");
        }
        return complete;
    }
}