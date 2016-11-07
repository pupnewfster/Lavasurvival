package me.eddiep.minecraft.ubotpatcher;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class UbotPatcher extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Searching for jar files...");

        File[] jars = getDataFolder().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith("jar");
            }
        });

        if (jars == null) {
            getLogger().info("No jar files found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info(jars.length + " jar files found!");

        for (File f : jars) {
            getLogger().info("Moving " + f.getName() + "...");
            File target = new File(getDataFolder().getParentFile(), f.getName());

            try {
                Files.move(f.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        getLogger().info("Patching complete!");
        getServer().getPluginManager().disablePlugin(this);
    }
}
