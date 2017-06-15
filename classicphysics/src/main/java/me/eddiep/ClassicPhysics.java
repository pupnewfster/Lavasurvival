package me.eddiep;

import me.eddiep.handles.ClassicPhysicsHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@SuppressWarnings("unused")
public class ClassicPhysics extends JavaPlugin {
    public static final Object Sync = new Object();
    public static ClassicPhysics INSTANCE;
    private ClassicPhysicsHandler handler;

    @Override
    public void onEnable() {
        INSTANCE = this;
        handler = new ClassicPhysicsHandler(this);
        File d = getDataFolder();
        if (!d.exists())
            d.mkdir();

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
        }
        return false;
    }

    public ClassicPhysicsHandler getPhysicsHandler() {
        return handler;
    }

    public void log(String message) {
        getLogger().info("[ClassicPhysics] " + message);
    }
}