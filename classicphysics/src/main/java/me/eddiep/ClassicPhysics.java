package me.eddiep;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@SuppressWarnings("unused")
public class ClassicPhysics extends JavaPlugin {
    public static ClassicPhysics INSTANCE;
    private PhysicsEngine pe;

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.getServer().getPluginManager().registerEvents(pe = new PhysicsEngine(), this);
        File d = getDataFolder();
        if (!d.exists())
            if (!d.mkdir())
                log("Failed to create directory for Classic Physics.");
    }

    @Override
    public void onDisable() {
        pe.end();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("pcl")) {
            if (!sender.hasPermission("classicphysics.pcl")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
                return true;
            }
            if (sender instanceof Player)
                pe.placeClassicBlock(((Player) sender).getLocation());
            else
                sender.sendMessage("This command can only be used in-game!");
            return true;
        }
        return false;
    }

    public static PhysicsEngine getPhysicsEngine() {
        return INSTANCE.pe;
    }

    public void log(String message) {
        getLogger().info("[ClassicPhysics] " + message);
    }
}