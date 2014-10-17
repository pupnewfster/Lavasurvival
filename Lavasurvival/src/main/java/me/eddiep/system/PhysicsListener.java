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
        ticksToMelt.put(new MaterialData(Material.COBBLESTONE), (130) * 20);

        //Basic blocks
        ticksToMelt.put(new MaterialData(Material.GRAVEL), (140) * 20);
        ticksToMelt.put(new MaterialData(Material.STONE), (190) * 20);
        ticksToMelt.put(new MaterialData(Material.LOG, (byte)0), (80) * 20);
        ticksToMelt.put(new MaterialData(Material.LOG, (byte)1), (80) * 20);
        ticksToMelt.put(new MaterialData(Material.LOG, (byte)2), (80) * 20);
        ticksToMelt.put(new MaterialData(Material.LOG, (byte)3), (80) * 20);
        ticksToMelt.put(new MaterialData(Material.LOG_2, (byte)0), (80) * 20);
        ticksToMelt.put(new MaterialData(Material.LOG_2, (byte)1), (80) * 20);
        ticksToMelt.put(new MaterialData(Material.SANDSTONE), (160) * 20);
        ticksToMelt.put(new MaterialData(Material.HARD_CLAY), (130) * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte)0), (115) * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte)1), (115) * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte)2), (115) * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte)3), (115) * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte)4), (115) * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte)5), (115) * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte)6), (115) * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte)7), (115) * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte)8), (115) * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte)9), (115) * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte)10), (115) * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte)11), (115) * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte)12), (115) * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte)13), (115) * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte)14), (115) * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte)15), (115) * 20);

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
        ticksToMelt.put(new MaterialData(Material.SMOOTH_BRICK, (byte)2), (164) * 20);
        ticksToMelt.put(new MaterialData(Material.GLASS), (168) * 20);

        //Survivor blocks
        ticksToMelt.put(new MaterialData(Material.PACKED_ICE), (10) * 20);
        ticksToMelt.put(new MaterialData(Material.BRICK), (180) * 20);
        ticksToMelt.put(new MaterialData(Material.SMOOTH_BRICK), (184) * 20);
        ticksToMelt.put(new MaterialData(Material.THIN_GLASS), (168) * 20);
        ticksToMelt.put(new MaterialData(Material.IRON_FENCE), (195) * 20);
        ticksToMelt.put(new MaterialData(Material.IRON_BLOCK), (200) * 20);

        //Trusted blocks
        ticksToMelt.put(new MaterialData(Material.FLOWER_POT_ITEM), (3) * 20);
        //ticksToMelt.put(new MaterialData(Material.YELLOW_FLOWER), ()) Flowers don't melt :3
        //ticksToMelt.put(new MaterialData(Material.RED_ROSE), ()); Flowers don't melt :3
        ticksToMelt.put(new MaterialData(Material.WOOD_DOOR), (55) * 20);
        ticksToMelt.put(new MaterialData(Material.FENCE), (53) * 20);
        ticksToMelt.put(new MaterialData(Material.WOOD_STAIRS), (65) * 20);
        ticksToMelt.put(new MaterialData(Material.COBBLESTONE_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.BRICK_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.SMOOTH_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.SANDSTONE_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.SPRUCE_WOOD_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.BIRCH_WOOD_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.JUNGLE_WOOD_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.QUARTZ_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.ACACIA_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.DARK_OAK_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.COBBLE_WALL, (byte)0), 130 * 20);
        ticksToMelt.put(new MaterialData(Material.COBBLE_WALL, (byte)1), 130 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte)0), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte)1), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte)2), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte)3), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte)4), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte)5), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte)6), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte)7), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte)8), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte)9), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte)10), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte)11), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte)12), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte)13), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte)14), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte)15), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte)0), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte)1), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte)2), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte)3), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte)4), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte)5), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte)6), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte)7), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte)8), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte)9), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte)10), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte)11), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte)12), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte)13), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte)14), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte)15), 168 * 20);


        //Elder blocks
        ticksToMelt.put(new MaterialData(Material.GLOWSTONE), (100) * 20);
        ticksToMelt.put(new MaterialData(Material.STEP, (byte) 6), (55 * 3) * 20);//Nether brick slab
        ticksToMelt.put(new MaterialData(Material.NETHER_FENCE), (300) * 20);
        ticksToMelt.put(new MaterialData(Material.NETHERRACK), 330 * 20);
        ticksToMelt.put(new MaterialData(Material.NETHER_BRICK), 370 * 20);
        ticksToMelt.put(new MaterialData(Material.NETHER_BRICK_STAIRS), 250 * 20);
        ticksToMelt.put(new MaterialData(Material.ENDER_STONE), 400 * 20);


        //Unburnable
        ticksToMelt.put(new MaterialData(Material.YELLOW_FLOWER), -1);
        ticksToMelt.put(new MaterialData(Material.RED_ROSE), -1);
        ticksToMelt.put(new MaterialData(Material.BEDROCK), -1);
        ticksToMelt.put(new MaterialData(Material.OBSIDIAN), -1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClassicPhysics(final ClassicPhysicsEvent event) {
        final Block blockChecking = event.getOriginalEvent().getBlock();
        MaterialData dat = new MaterialData(blockChecking.getType(), blockChecking.getData());
        if(blockChecking.getData() == 0 && !ticksToMelt.containsKey(dat))
            dat = new MaterialData(blockChecking.getType());

        if (ticksToMelt.containsKey(dat)) {
            event.setCancelled(true);
            long tickCount = ticksToMelt.get(dat);
            if (tickCount == -1) //It's unburnable
                return;

            int task = Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
                @Override
                public void run() {
                    blockChecking.setType(event.getNewBlock());
                }
            }, tickCount);
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