package me.eddiep;

import me.eddiep.handles.ClassicPhysicsHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

@SuppressWarnings("unused")
public class ClassicPhysics extends JavaPlugin {
    public static final Object Sync = new Object();
    public static ClassicPhysics INSTANCE;
    public static PhysicsType TYPE = PhysicsType.CLASSIC;
    private ClassicPhysicsHandler handler;

    @Override
    public void onEnable() {
        INSTANCE = this;
        handler = new ClassicPhysicsHandler(this);
        File d = getDataFolder();
        if (!d.exists())
            d.mkdir();

        List<World> worlds = getServer().getWorlds();
        for (World world : worlds) {
            if (!handler.hasPhysicsLevel(world))
                handler.setPhysicLevel(world, 1);
            if (!handler.hasPhysicsSpeed(world))
                handler.setPhysicSpeed(world, 800L);
        }

        handler.enable();
    }

    @Override
    public void onDisable() {
        handler.disable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("pcl")) {
            if (!sender.hasPermission("classicphysics.pcl")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
                return true;
            }
            if (sender instanceof Player)
                handler.forcePlaceClassicBlockAt(((Player) sender).getLocation(), Material.LAVA);
            else
                sender.sendMessage("This command can only be used in-game!");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("pcw")) {
            if (!sender.hasPermission("classicphysics.pcw")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
                return true;
            }
            if (sender instanceof Player)
                handler.forcePlaceClassicBlockAt(((Player) sender).getLocation(), Material.WATER);
            else
                sender.sendMessage("This command can only be used in-game!");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("setphysics")) {
            if (!sender.hasPermission("classicphysics.settings")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
                return true;
            }
            if (args[0].equalsIgnoreCase("speed")) {
                if (args.length <= 1)
                    sender.sendMessage(ChatColor.RED + "Please specify a speed!");

                World world = null;
                if (sender instanceof Player)
                    world = ((Player) sender).getWorld();

                if (args.length >= 3) {
                    world = getServer().getWorld(args[2]);
                    if (world == null) {
                        sender.sendMessage(ChatColor.RED + "World not found!");
                        return true;
                    }
                }

                if (world == null) {
                    sender.sendMessage(ChatColor.RED + "Please specify a world!");
                    return true;
                }

                handler.setPhysicSpeed(world, Long.parseLong(args[1]));
                sender.sendMessage(ChatColor.GREEN + "Speed set to " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + "ms!");
            } else if (args[0].equalsIgnoreCase("level")) {
                if (args.length <= 1)
                    sender.sendMessage(ChatColor.RED + "Please specify a level!");

                World world = null;
                if (sender instanceof Player)
                    world = ((Player) sender).getWorld();

                if (args.length >= 3) {
                    world = getServer().getWorld(args[2]);
                    if (world == null) {
                        sender.sendMessage(ChatColor.RED + "World not found!");
                        return true;
                    }
                }

                if (world == null) {
                    sender.sendMessage(ChatColor.RED + "Please specify a world!");
                    return true;
                }
                handler.setPhysicLevel(world, Integer.parseInt(args[1]));
                sender.sendMessage(ChatColor.GREEN + "Level set to " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + "!");
            }
        }
        return false;
    }

    public void setPhysicsType(PhysicsType t) {
        TYPE = t;
    }

    public ClassicPhysicsHandler getPhysicsHandler() {
        return handler;
    }

    public void log(String message) {
        getLogger().info("[ClassicPhysics] " + message);
    }
}