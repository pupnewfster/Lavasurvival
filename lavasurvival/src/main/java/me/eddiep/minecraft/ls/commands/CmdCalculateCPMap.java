package me.eddiep.minecraft.ls.commands;

import com.crossge.necessities.Necessities;
import com.crossge.necessities.RankManager.User;
import me.eddiep.PhysicsEngine;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.game.LavaMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class CmdCalculateCPMap implements Cmd {
    @Override
    public boolean commandUse(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            User u = Necessities.getUM().getUser(((Player) sender).getUniqueId());
            Location left = u.getLeft();
            Location right = u.getRight();
            if (!left.getWorld().equals(right.getWorld()) || !left.getWorld().equals(Gamemode.getCurrentWorld())) {
                sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "Please select left and right corners of the current map.");
                return true;
            }
            if (!calculateMeltMap(left, right))
                sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "Map mapping failed.");
            else
                sender.sendMessage(ChatColor.GOLD + "Map mapping complete.");
        } else
            sender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "Login to perform this command because you have to click two locations.");
        return true;
    }

    @SuppressWarnings("deprecation")
    private boolean calculateMeltMap(Location left, Location right) {
        World w = left.getWorld();
        String worldName = w.getName();
        int x1 = left.getBlockX(), x2 = right.getBlockX(), y1 = left.getBlockY(), y2 = right.getBlockY(), z1 = left.getBlockZ(), z2 = right.getBlockZ();
        if (x2 < x1) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }
        if (y2 < y1)
            y1 = y2;
        if (z2 < z1) {
            int temp = z1;
            z1 = z2;
            z2 = temp;
        }
        LavaMap map = Gamemode.getCurrentMap();
        y2 = map.getLavaY() + 1;
        HashMap<Short, ArrayList<Long>> meltMap = new HashMap<>();
        for (int y = y1; y <= y2; y++) {
            for (int x = x1; x <= x2; x++) {
                for (int z = z1; z <= z2; z++) {
                    Location loc = new Location(w, x, y, z);
                    if (map.isInSafeZone(loc)) {
                        ArrayList<Long> locations = meltMap.computeIfAbsent((short) -1, k -> new ArrayList<>());
                        locations.add(PhysicsEngine.convert(x, y, z));
                        continue;
                    }
                    Block b = loc.getBlock();
                    short melt = PhysicsEngine.getMeltTime(new MaterialData(b.getType(), b.getData()));
                    if (melt == 0)
                        continue;
                    ArrayList<Long> locations = meltMap.computeIfAbsent(melt, k -> new ArrayList<>());
                    locations.add(PhysicsEngine.convert(x, y, z));
                }
            }
        }
        File fileMeltMap = new File("plugins/ClassicPhysics", worldName + ".txt");
        if (fileMeltMap.exists()) //Clear it
            if (!fileMeltMap.delete())
                return false;
        try {
            if (!fileMeltMap.createNewFile())
                return false;
        } catch (IOException ignored) {
            return false;
        }
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(fileMeltMap))) {
            os.writeObject(meltMap);
        } catch (IOException ignored) {
            return false;
        }
        return true;
    }

    @Override
    public String getName() {
        return "calculatecpmap";
    }
}