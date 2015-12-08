package me.eddiep.minecraft.ls.system;

import me.eddiep.handles.ClassicPhysicsEvent;
import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
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
    private static final long DEFAULT_SPEED = 5 * 20;
    private static final HashMap<MaterialData, Integer> ticksToMelt = new HashMap<>();
    private ArrayList<Integer> tasks = new ArrayList<>();

    public PhysicsListener() {
        setup();
    }

    private static void setup() {
        if (ticksToMelt.size() > 0)
            return;
        //Default blocks
        ticksToMelt.put(new MaterialData(Material.TORCH), 20);
        ticksToMelt.put(new MaterialData(Material.WOOD), 55 * 20);
        ticksToMelt.put(new MaterialData(Material.DIRT), 100 * 20);
        ticksToMelt.put(new MaterialData(Material.GRASS), 100 * 20);
        ticksToMelt.put(new MaterialData(Material.SAND), 70 * 20);
        ticksToMelt.put(new MaterialData(Material.COBBLESTONE), 130 * 20);

        //Basic blocks
        ticksToMelt.put(new MaterialData(Material.GRAVEL), 140 * 20);
        ticksToMelt.put(new MaterialData(Material.STONE), 190 * 20);
        ticksToMelt.put(new MaterialData(Material.LOG, (byte) 0), 80 * 20);//Oak log
        ticksToMelt.put(new MaterialData(Material.LOG, (byte) 1), 80 * 20);//Spruce log
        ticksToMelt.put(new MaterialData(Material.LOG, (byte) 2), 80 * 20);//Birch log
        ticksToMelt.put(new MaterialData(Material.LOG, (byte) 3), 80 * 20);//Jungle log
        ticksToMelt.put(new MaterialData(Material.LOG_2, (byte) 0), 80 * 20);//Acacia log
        ticksToMelt.put(new MaterialData(Material.LOG_2, (byte) 1), 80 * 20);//Dark oak log
        ticksToMelt.put(new MaterialData(Material.SANDSTONE), 160 * 20);
        ticksToMelt.put(new MaterialData(Material.HARD_CLAY), 130 * 20);
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 0), 115 * 20);//White clay
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 1), 115 * 20);//Orange clay
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 2), 115 * 20);//Magenta clay
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 3), 115 * 20);//Light blue clay
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 4), 115 * 20);//Yellow clay
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 5), 115 * 20);//Lime clay
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 6), 115 * 20);//Pink clay
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 7), 115 * 20);//Gray clay
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 8), 115 * 20);//Light gray clay
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 9), 115 * 20);//Cyan clay
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 10), 115 * 20);//Purple clay
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 11), 115 * 20);//Blue clay
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 12), 115 * 20);//Brown clay
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 13), 115 * 20);//Green clay
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 14), 115 * 20);//Red clay
        ticksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 15), 115 * 20);//Black clay

        //Advanced blocks
        ticksToMelt.put(new MaterialData(Material.STEP, (byte) 0), 170 * 20);//Stone slab
        ticksToMelt.put(new MaterialData(Material.STEP, (byte) 1), 170 * 20);//Sandstone slab
        ticksToMelt.put(new MaterialData(Material.STEP, (byte) 3), 155 * 20);//Cobble slab
        ticksToMelt.put(new MaterialData(Material.STEP, (byte) 4), 170 * 20);//Brick slab
        ticksToMelt.put(new MaterialData(Material.STEP, (byte) 5), 175 * 20);//Stone brick slab
        ticksToMelt.put(new MaterialData(Material.STEP, (byte) 7), 178 * 20);//Quartz slab
        ticksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 0), 50 * 20);//Oak slab
        ticksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 1), 50 * 20);//Spruce slab
        ticksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 2), 50 * 20);//Birch slab
        ticksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 3), 50 * 20);//Jungle slab
        ticksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 4), 50 * 20);//Acacia slab
        ticksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 5), 50 * 20);//Dark oak slab
        ticksToMelt.put(new MaterialData(Material.MOSSY_COBBLESTONE), 165 * 20);
        ticksToMelt.put(new MaterialData(Material.SMOOTH_BRICK, (byte) 2), 164 * 20);//Cracked stone brick
        ticksToMelt.put(new MaterialData(Material.GLASS), 168 * 20);

        //Survivor blocks
        ticksToMelt.put(new MaterialData(Material.PACKED_ICE), 10 * 20);
        ticksToMelt.put(new MaterialData(Material.BRICK), 180 * 20);
        ticksToMelt.put(new MaterialData(Material.SMOOTH_BRICK, (byte) 0), 184 * 20);//Stone brick
        ticksToMelt.put(new MaterialData(Material.THIN_GLASS), 168 * 20);
        ticksToMelt.put(new MaterialData(Material.IRON_FENCE), 195 * 20);//Iron bars
        ticksToMelt.put(new MaterialData(Material.IRON_BLOCK), 200 * 20);

        //Trusted blocks
        ticksToMelt.put(new MaterialData(Material.FLOWER_POT_ITEM), 3 * 20);
        ticksToMelt.put(new MaterialData(Material.FLOWER_POT), 3 * 20);
        //ticksToMelt.put(new MaterialData(Material.YELLOW_FLOWER), ()) Flowers don't melt :3
        //ticksToMelt.put(new MaterialData(Material.RED_ROSE), ()); Flowers don't melt :3
        ticksToMelt.put(new MaterialData(Material.WOOD_DOOR), 55 * 20);
        ticksToMelt.put(new MaterialData(Material.WOODEN_DOOR), 55 * 20);
        ticksToMelt.put(new MaterialData(Material.BOOKSHELF), 100 * 20);
        ticksToMelt.put(new MaterialData(Material.FENCE), 53 * 20);
        ticksToMelt.put(new MaterialData(Material.WOOD_STAIRS), 65 * 20);//Oak stairs
        ticksToMelt.put(new MaterialData(Material.COBBLESTONE_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.BRICK_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.SMOOTH_STAIRS), 65 * 20);//Stone brick stairs
        ticksToMelt.put(new MaterialData(Material.SANDSTONE_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.SPRUCE_WOOD_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.BIRCH_WOOD_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.JUNGLE_WOOD_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.QUARTZ_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.ACACIA_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.DARK_OAK_STAIRS), 65 * 20);
        ticksToMelt.put(new MaterialData(Material.COBBLE_WALL, (byte) 0), 130 * 20);//Cobblestone wall
        ticksToMelt.put(new MaterialData(Material.COBBLE_WALL, (byte) 1), 130 * 20);//Mossy cobblestone wall
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 0), 168 * 20);//White glass
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 1), 168 * 20);//Orange glass
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 2), 168 * 20);//Magenta glass
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 3), 168 * 20);//Light blue glass
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 4), 168 * 20);//Yellow glass
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 5), 168 * 20);//Lime glass
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 6), 168 * 20);//Pink glass
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 7), 168 * 20);//Gray glass
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 8), 168 * 20);//Light gray glass
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 9), 168 * 20);//Cyan glass
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 10), 168 * 20);//Purple glass
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 11), 168 * 20);//Blue glass
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 12), 168 * 20);//Brown glass
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 13), 168 * 20);//Green glass
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 14), 168 * 20);//Red glass
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 15), 168 * 20);//Black glass
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 0), 168 * 20);//White glass pane
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 1), 168 * 20);//Orange glass pane
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 2), 168 * 20);//Magenta glass pane
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 3), 168 * 20);//Light blue glass pane
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 4), 168 * 20);//Yellow glass pane
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 5), 168 * 20);//Lime glass pane
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 6), 168 * 20);//Pink glass pane
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), 168 * 20);//Gray glass pane
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 8), 168 * 20);//Light gray glass pane
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 9), 168 * 20);//Cyan glass pane
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 10), 168 * 20);//Purple glass pane
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 11), 168 * 20);//Blue glass pane
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 12), 168 * 20);//Brown glass pane
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 13), 168 * 20);//Green glass pane
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 14), 168 * 20);//Red glass pane
        ticksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 15), 168 * 20);//Black glass pane


        //Elder blocks
        ticksToMelt.put(new MaterialData(Material.GLOWSTONE), 100 * 20);
        ticksToMelt.put(new MaterialData(Material.STEP, (byte) 6), 165 * 20);//Nether brick slab
        ticksToMelt.put(new MaterialData(Material.NETHER_FENCE), 300 * 20);
        ticksToMelt.put(new MaterialData(Material.NETHERRACK), 330 * 20);
        ticksToMelt.put(new MaterialData(Material.NETHER_BRICK), 370 * 20);
        ticksToMelt.put(new MaterialData(Material.NETHER_BRICK_STAIRS), 250 * 20);
        ticksToMelt.put(new MaterialData(Material.ENDER_STONE), 400 * 20);


        //Unburnable
        ticksToMelt.put(new MaterialData(Material.YELLOW_FLOWER), -1);
        ticksToMelt.put(new MaterialData(Material.RED_ROSE), -1);
        ticksToMelt.put(new MaterialData(Material.IRON_DOOR_BLOCK), -1);
        ticksToMelt.put(new MaterialData(Material.BEDROCK), -1);
        ticksToMelt.put(new MaterialData(Material.OBSIDIAN), -1);
        ticksToMelt.put(new MaterialData(Material.BARRIER), -1);

        for (Material m : Material.values())
            if (!m.equals(Material.LAVA) && !m.equals(Material.STATIONARY_LAVA) && !m.equals(Material.WATER) && !m.equals(Material.STATIONARY_WATER) && !m.equals(Material.AIR) &&
                    !ticksToMelt.containsKey(new MaterialData(m)))
                ticksToMelt.put(new MaterialData(m), 30 * 20);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClassicPhysics(final ClassicPhysicsEvent event) {
        if (!event.isClassicEvent() || Gamemode.getCurrentGame().hasEnded())
            return;

        final Block blockChecking = event.getOldBlock();
        if (blockChecking.getType().equals(Material.AIR))
            return;

        MaterialData dat = new MaterialData(blockChecking.getType(), blockChecking.getData());
        if (!ticksToMelt.containsKey(dat))
            dat = new MaterialData(blockChecking.getType());

        if (ticksToMelt.containsKey(dat)) {
            event.setCancelled(true);
            long tickCount = ticksToMelt.get(dat);
            if (tickCount == -1) //It's unburnable
                return;

            int task = Bukkit.getScheduler().scheduleSyncDelayedTask(Lavasurvival.INSTANCE, new Runnable() {
                @Override
                public void run() {
                    Lavasurvival.INSTANCE.getPhysicsHandler().placeClassicBlockAt(event.getLocation(), event.getLogicContainer().logicFor());
                }
            }, tickCount);
            tasks.add(task);
        }
    }

    public static int getMeltTime(MaterialData data) {//seconds
        return ticksToMelt.containsKey(data) ? ticksToMelt.get(data) / 20 : 0;
    }

    public void cancelAllTasks() {
        tasks.clear();
    }

    public void cleanup() {
        cancelAllTasks();
        HandlerList.unregisterAll(this);
    }
}