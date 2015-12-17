package me.eddiep.minecraft.ls.system;

import me.eddiep.handles.ClassicPhysicsEvent;
import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PhysicsListener implements Listener {
    private static final long DEFAULT_SPEED = 5 * 20;
    private static final HashMap<MaterialData, Integer> ticksToMelt = new HashMap<>();
    private static final ConcurrentHashMap<Location, ConcurrentLinkedQueue<BlockTaskInfo>> toTasks = new ConcurrentHashMap<>();

    public PhysicsListener() {
        setup();
        PHYSICS_TICK.runTaskTimerAsynchronously(Lavasurvival.INSTANCE, 0, 1);
    }

    private static void setup() {
        if (ticksToMelt.size() > 0)
            return;
        //Default blocks
        ticksToMelt.put(new MaterialData(Material.TORCH), 20);
        ticksToMelt.put(new MaterialData(Material.WOOD, (byte) 0), 55 * 20);//Oak plank
        ticksToMelt.put(new MaterialData(Material.DIRT), 100 * 20);
        ticksToMelt.put(new MaterialData(Material.GRASS), 100 * 20);
        ticksToMelt.put(new MaterialData(Material.SAND, (byte) 0), 70 * 20);//Sand
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
        ticksToMelt.put(new MaterialData(Material.LADDER), 30 * 20);

        //Trusted blocks
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
        ticksToMelt.put(new MaterialData(Material.PRISMARINE, (byte) 0), 195 * 20);//Prismarine
        ticksToMelt.put(new MaterialData(Material.PRISMARINE, (byte) 1), 195 * 20);//Prismarine Bricks
        ticksToMelt.put(new MaterialData(Material.PRISMARINE, (byte) 2), 195 * 20);//Dark Prismarine


        //Elder blocks
        ticksToMelt.put(new MaterialData(Material.GLOWSTONE), 100 * 20);
        ticksToMelt.put(new MaterialData(Material.STEP, (byte) 6), 165 * 20);//Nether brick slab
        ticksToMelt.put(new MaterialData(Material.NETHER_FENCE), 300 * 20);
        ticksToMelt.put(new MaterialData(Material.NETHERRACK), 330 * 20);
        ticksToMelt.put(new MaterialData(Material.NETHER_BRICK), 370 * 20);
        ticksToMelt.put(new MaterialData(Material.NETHER_BRICK_STAIRS), 250 * 20);
        ticksToMelt.put(new MaterialData(Material.ENDER_STONE), 400 * 20);


        //Donnor blocks Instant melt)
        ticksToMelt.put(new MaterialData(Material.WOOD, (byte) 1), 0);//Spruce planks
        ticksToMelt.put(new MaterialData(Material.WOOD, (byte) 2), 0);//Birch planks
        ticksToMelt.put(new MaterialData(Material.WOOD, (byte) 3), 0);//Jungle planks
        ticksToMelt.put(new MaterialData(Material.WOOD, (byte) 4), 0);//Acacia planks
        ticksToMelt.put(new MaterialData(Material.WOOD, (byte) 5), 0);//Dark oak planks
        ticksToMelt.put(new MaterialData(Material.SAND, (byte) 1), 70 * 20);//Red sand
        ticksToMelt.put(new MaterialData(Material.SPRUCE_FENCE), 0);
        ticksToMelt.put(new MaterialData(Material.BIRCH_FENCE), 0);
        ticksToMelt.put(new MaterialData(Material.JUNGLE_FENCE), 0);
        ticksToMelt.put(new MaterialData(Material.ACACIA_FENCE), 0);
        ticksToMelt.put(new MaterialData(Material.DARK_OAK_FENCE), 0);
        ticksToMelt.put(new MaterialData(Material.FENCE_GATE), 0);
        ticksToMelt.put(new MaterialData(Material.SPRUCE_FENCE_GATE), 0);
        ticksToMelt.put(new MaterialData(Material.BIRCH_FENCE_GATE), 0);
        ticksToMelt.put(new MaterialData(Material.JUNGLE_FENCE_GATE), 0);
        ticksToMelt.put(new MaterialData(Material.ACACIA_FENCE_GATE), 0);
        ticksToMelt.put(new MaterialData(Material.DARK_OAK_FENCE_GATE), 0);
        ticksToMelt.put(new MaterialData(Material.SPRUCE_DOOR), 0);
        ticksToMelt.put(new MaterialData(Material.SPRUCE_DOOR_ITEM), 0);
        ticksToMelt.put(new MaterialData(Material.BIRCH_DOOR), 0);
        ticksToMelt.put(new MaterialData(Material.BIRCH_DOOR_ITEM), 0);
        ticksToMelt.put(new MaterialData(Material.JUNGLE_DOOR), 0);
        ticksToMelt.put(new MaterialData(Material.JUNGLE_DOOR_ITEM), 0);
        ticksToMelt.put(new MaterialData(Material.ACACIA_DOOR), 0);
        ticksToMelt.put(new MaterialData(Material.ACACIA_DOOR_ITEM), 0);
        ticksToMelt.put(new MaterialData(Material.DARK_OAK_DOOR), 0);
        ticksToMelt.put(new MaterialData(Material.DARK_OAK_DOOR_ITEM), 0);
        ticksToMelt.put(new MaterialData(Material.SEA_LANTERN), 0);
        ticksToMelt.put(new MaterialData(Material.CARPET, (byte) 0), 0);//White carpet
        ticksToMelt.put(new MaterialData(Material.CARPET, (byte) 1), 0);//Orange carpet
        ticksToMelt.put(new MaterialData(Material.CARPET, (byte) 2), 0);//Magenta carpet
        ticksToMelt.put(new MaterialData(Material.CARPET, (byte) 3), 0);//Light blue carpet
        ticksToMelt.put(new MaterialData(Material.CARPET, (byte) 4), 0);//Yellow carpet
        ticksToMelt.put(new MaterialData(Material.CARPET, (byte) 5), 0);//Lime carpet
        ticksToMelt.put(new MaterialData(Material.CARPET, (byte) 6), 0);//Pink carpet
        ticksToMelt.put(new MaterialData(Material.CARPET, (byte) 7), 0);//Gray carpet
        ticksToMelt.put(new MaterialData(Material.CARPET, (byte) 8), 0);//Light gray carpet
        ticksToMelt.put(new MaterialData(Material.CARPET, (byte) 9), 0);//Cyan carpet
        ticksToMelt.put(new MaterialData(Material.CARPET, (byte) 10), 0);//Purple carpet
        ticksToMelt.put(new MaterialData(Material.CARPET, (byte) 11), 0);//Blue carpet
        ticksToMelt.put(new MaterialData(Material.CARPET, (byte) 12), 0);//Brown carpet
        ticksToMelt.put(new MaterialData(Material.CARPET, (byte) 13), 0);//Green carpet
        ticksToMelt.put(new MaterialData(Material.CARPET, (byte) 14), 0);//Red carpet
        ticksToMelt.put(new MaterialData(Material.CARPET, (byte) 15), 0);//Black carpet
        ticksToMelt.put(new MaterialData(Material.FLOWER_POT_ITEM), 0);
        ticksToMelt.put(new MaterialData(Material.FLOWER_POT), 0);


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
    public synchronized void onClassicPhysics(final ClassicPhysicsEvent event) {
        if (!event.isClassicEvent() || Gamemode.getCurrentGame().hasEnded())
            return;

        Block blockChecking = event.getOldBlock();
        if (blockChecking.getType().equals(Material.AIR))
            return;

        MaterialData dat = new MaterialData(blockChecking.getType(), blockChecking.getData());
        if (!ticksToMelt.containsKey(dat))
            dat = new MaterialData(blockChecking.getType());

        if (ticksToMelt.containsKey(dat)) {
            event.setCancelled(true);
            long meltTicks = ticksToMelt.get(dat);
            if (meltTicks == -1) //It's unburnable
                return;
            if (!blockChecking.hasMetadata("player_placed"))
                meltTicks *= 0.5;
            ConcurrentLinkedQueue<BlockTaskInfo> temp = new ConcurrentLinkedQueue<>();
            Location location = event.getLocation();
            if (toTasks.containsKey(location) && toTasks.get(location) != null && toTasks.get(location).size() > 0)
                temp = toTasks.get(location);
            temp.add(new BlockTaskInfo(event.getLocation(), event.getLogicContainer().logicFor(), event.getFrom(), blockChecking, meltTicks));
            toTasks.put(event.getLocation(), temp);
        }
    }

    private long tickCount;
    private final BukkitRunnable PHYSICS_TICK = new BukkitRunnable() {
        @Override
        public void run() {
            tickCount++;
            for (Location loc : toTasks.keySet()) {
                ConcurrentLinkedQueue<BlockTaskInfo> queue = toTasks.get(loc);
                if (queue != null)
                    for (BlockTaskInfo b : queue)
                        if (b != null) {
                            if (tickCount - b.getStartTick() >= b.getTicksToMelt()) {
                                synchronized (toTasks) {
                                    final Block blockChecking = b.getOldBlock();
                                    if (blockChecking.getType().equals(Material.AIR))
                                        return;
                                    if (blockChecking.hasMetadata("player_placed"))
                                        blockChecking.removeMetadata("player_placed", Lavasurvival.INSTANCE);
                                    Lavasurvival.INSTANCE.getPhysicsHandler().placeClassicBlockAt(b.getLocation(), b.getLogicFor(), b.getFrom());
                                }
                                cancelLocation(b.getLocation());
                                break;
                            }
                        }
            }
        }
    };

    public static void cancelLocation(Location loc) {
        if (toTasks.containsKey(loc))
            toTasks.remove(loc);
    }

    public static String getMeltTimeAsString(MaterialData data) {
        int seconds = ticksToMelt.containsKey(data) ? ticksToMelt.get(data) / 20 : 0;
        if (seconds == 0)
            return "Immediately";
        else if (seconds < 0)
            return "Never";
        else
            return seconds + " Second" + (seconds == 1 ? "" : "s");
    }

    private static void cancelAllTasks() {
        for (Location l : toTasks.keySet())
            cancelLocation(l);
    }

    public void cleanup() {
        cancelAllTasks();
        HandlerList.unregisterAll(this);
    }

    private class BlockTaskInfo {
        private long ticksToMelt, startTick;
        private Location location, from;
        private Material logicFor;
        private Block oldBlock;

        public BlockTaskInfo(Location location, Material logicFor, Location from, Block oldBlock, long ticksToMelt) {
            this.startTick = tickCount;
            this.location = location;
            this.from = from;
            this.logicFor = logicFor;
            this.oldBlock = oldBlock;
            this.ticksToMelt = ticksToMelt;
        }

        public long getStartTick() {
            return this.startTick;
        }

        public long getTicksToMelt() {
            return this.ticksToMelt;
        }

        public Location getLocation() {
            return this.location;
        }

        public Location getFrom() {
            return this.from;
        }

        public Material getLogicFor() {
            return this.logicFor;
        }

        public Block getOldBlock() {
            return this.oldBlock;
        }
    }
}