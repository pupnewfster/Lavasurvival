package me.eddiep.minecraft.ubotpatcher;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class UbotPatcher extends JavaPlugin {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onEnable() {
        getLogger().info("Searching for jar files...");

        File dataFolder = new File("plugins/ubotpatcher");
        if (!dataFolder.exists())
            dataFolder.mkdir();

        File[] jars = dataFolder.listFiles(pathname -> pathname.getName().endsWith("jar"));

        if (jars == null || jars.length == 0) {
            getLogger().info("No jar files found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info(jars.length + " jar files found!");

        for (File f : jars) {
            getLogger().info("Moving " + f.getName() + "...");
            File target = new File(dataFolder.getParentFile(), f.getName());
            try {
                Files.move(f.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        getLogger().info("Patching complete!");
        //Restart server once it is finished starting
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> Bukkit.spigot().restart());
    }
}