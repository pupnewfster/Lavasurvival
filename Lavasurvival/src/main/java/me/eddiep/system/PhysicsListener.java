package me.eddiep.system;

import me.eddiep.Lavasurvival;
import me.eddiep.handles.ClassicPhysicsEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;

public class PhysicsListener implements Listener {
    private static final HashMap<Material, Integer> ticksToMelt = new HashMap<Material, Integer>();

    private ArrayList<Integer> tasks = new ArrayList<Integer>();
    public PhysicsListener() {
        setup();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClassicPhysics(final ClassicPhysicsEvent event) {
        final Block blockChecking = event.getOriginalEvent().getBlock();
        Material material = blockChecking.getType();

        if (ticksToMelt.containsKey(material)) {
            event.setCancelled(true);
            int task = Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
                @Override
                public void run() {
                    blockChecking.setType(event.getNewBlock());
                }
            }, ticksToMelt.get(material));
            tasks.add(task);
        }
    }

    public void cancelAllTasks() {
        for (Integer task : tasks) {
            Bukkit.getScheduler().cancelTask(task);
        }
        tasks.clear();
    }

    public void cleanup() {
        cancelAllTasks();
        HandlerList.unregisterAll(this);
    }

    private static void setup() {
        if (ticksToMelt.size() > 0)
            return;

        ticksToMelt.put(Material.STONE, (60 * 3) * 20);
        ticksToMelt.put(Material.WOOD, 45 * 20);
        ticksToMelt.put(Material.DIRT, (60 * 2) * 20);
        ticksToMelt.put(Material.GRASS, (60 * 2) * 20);
        ticksToMelt.put(Material.SAND, 60 * 20);
        ticksToMelt.put(Material.COBBLESTONE, (55 * 3) * 20);
    }
}
