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
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.HashMap;

public class PhysicsListener implements Listener {
    private static final HashMap<MaterialData, Integer> ticksToMelt = new HashMap<MaterialData, Integer>();

    private ArrayList<Integer> tasks = new ArrayList<Integer>();

    public PhysicsListener() {
        setup();
    }

    private static void setup() {
        if (ticksToMelt.size() > 0)
            return;
        //Default blocks
        ticksToMelt.put(new MaterialData(Material.TORCH), 20);
        ticksToMelt.put(new MaterialData(Material.WOOD), 55 * 20);
        ticksToMelt.put(new MaterialData(Material.DIRT), (100) * 20);
        ticksToMelt.put(new MaterialData(Material.GRASS), (100) * 20);
        ticksToMelt.put(new MaterialData(Material.SAND), 70 * 20);
        ticksToMelt.put(new MaterialData(Material.COBBLESTONE), (165) * 20);
        //Calculate the rest of buyable items melt values then remove this comment
        //Basic blocks
        ticksToMelt.put(new MaterialData(Material.GRAVEL), (140) * 20);
        ticksToMelt.put(new MaterialData(Material.STONE), (190) * 20);
        ticksToMelt.put(new MaterialData(Material.SANDSTONE), (180) * 20);
        ticksToMelt.put(new MaterialData(Material.BRICK), (180) * 20);

        //Advanced blocks
        ticksToMelt.put(new MaterialData(Material.STEP, (byte) 0), (170) * 20);//Stone slab
        ticksToMelt.put(new MaterialData(Material.STEP, (byte) 3), (155) * 20);//Cobble slab
        ticksToMelt.put(new MaterialData(Material.STEP, (byte) 4), (170) * 20);//Brick slab
        ticksToMelt.put(new MaterialData(Material.STEP, (byte) 5), (175) * 20);//Stone brick slab
        ticksToMelt.put(new MaterialData(Material.STEP, (byte) 7), (178) * 20);//Quartz slab
        ticksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 0), (50) * 20);//Oak slab
        ticksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 1), (50) * 20);//Spruce slab
        ticksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 2), (50) * 20);//Birch slab
        ticksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 3), (50) * 20);//Jung;e slab
        ticksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 4), (50) * 20);//Acacia slab
        ticksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 5), (50) * 20);//Dark oak slab
        ticksToMelt.put(new MaterialData(Material.MOSSY_COBBLESTONE), (165) * 20);
        ticksToMelt.put(new MaterialData(Material.SMOOTH_BRICK), (184) * 20);
        ticksToMelt.put(new MaterialData(Material.GLASS), (188) * 20);

        //Survivor blocks
        //ticksToMelt.put(new MaterialData(Material.GLASS), (55 * 3) * 20);//Place holder block

        //Trusted blocks
        //ticksToMelt.put(new MaterialData(Material.GLASS), (55 * 3) * 20);//Place holder block

        //Elder blocks
        ticksToMelt.put(new MaterialData(Material.STEP, (byte) 6), (55 * 3) * 20);//Nether brick slab
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClassicPhysics(final ClassicPhysicsEvent event) {
        final Block blockChecking = event.getOriginalEvent().getBlock();
        MaterialData dat = new MaterialData(blockChecking.getType(), blockChecking.getData());
        if(blockChecking.getData() == 0 && !ticksToMelt.containsKey(dat))
            dat = new MaterialData(blockChecking.getType());

        if (ticksToMelt.containsKey(dat)) {
            event.setCancelled(true);
            int task = Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
                @Override
                public void run() {
                    blockChecking.setType(event.getNewBlock());
                }
            }, ticksToMelt.get(dat));
            tasks.add(task);
        }
    }

    public void cancelAllTasks() {
        for (Integer task : tasks)
            Bukkit.getScheduler().cancelTask(task);
        tasks.clear();
    }

    public void cleanup() {
        cancelAllTasks();
        HandlerList.unregisterAll(this);
    }
}