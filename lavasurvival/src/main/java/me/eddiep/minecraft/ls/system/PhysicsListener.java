package me.eddiep.minecraft.ls.system;

import me.eddiep.handles.ClassicPhysicsEvent;
import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import me.eddiep.minecraft.ls.game.LavaMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PhysicsListener implements Listener {
    private static final HashMap<MaterialData, Integer> lavaTicksToMelt = new HashMap<>();
    private static final HashMap<MaterialData, Integer> waterTicksToMelt = new HashMap<>();
    private static final ConcurrentHashMap<Location, ConcurrentLinkedQueue<BlockTaskInfo>> toTasks = new ConcurrentHashMap<>();

    public PhysicsListener() {
        setup();
    }

    private static void setup() {
        if (lavaTicksToMelt.size() > 0 && waterTicksToMelt.size() > 0)
            return;

        //Default blocks
        lavaTicksToMelt.put(new MaterialData(Material.TORCH), 20);
        lavaTicksToMelt.put(new MaterialData(Material.WOOD, (byte) 0), 30 * 20);//Oak plank
        lavaTicksToMelt.put(new MaterialData(Material.DIRT), 90 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.GRASS), 90 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.SAND, (byte) 0), 90 * 20);//Sand
        lavaTicksToMelt.put(new MaterialData(Material.COBBLESTONE), 120 * 20);

        //Basic blocks
        lavaTicksToMelt.put(new MaterialData(Material.GRAVEL), 120 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.STONE), 120 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.LOG, (byte) 0), 30 * 20);//Oak log
        lavaTicksToMelt.put(new MaterialData(Material.LOG, (byte) 1), 30 * 20);//Spruce log
        lavaTicksToMelt.put(new MaterialData(Material.LOG, (byte) 2), 30 * 20);//Birch log
        lavaTicksToMelt.put(new MaterialData(Material.LOG, (byte) 3), 30 * 20);//Jungle log
        lavaTicksToMelt.put(new MaterialData(Material.LOG_2, (byte) 0), 30 * 20);//Acacia log
        lavaTicksToMelt.put(new MaterialData(Material.LOG_2, (byte) 1), 30 * 20);//Dark oak log
        lavaTicksToMelt.put(new MaterialData(Material.SANDSTONE), 120 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.RED_SANDSTONE), 120 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.HARD_CLAY), 120 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 0), 120 * 20);//White clay
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 1), 120 * 20);//Orange clay
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 2), 120 * 20);//Magenta clay
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 3), 120 * 20);//Light blue clay
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 4), 120 * 20);//Yellow clay
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 5), 120 * 20);//Lime clay
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 6), 120 * 20);//Pink clay
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 7), 120 * 20);//Gray clay
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 8), 120 * 20);//Light gray clay
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 9), 120 * 20);//Cyan clay
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 10), 120 * 20);//Purple clay
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 11), 120 * 20);//Blue clay
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 12), 120 * 20);//Brown clay
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 13), 120 * 20);//Green clay
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 14), 120 * 20);//Red clay
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_CLAY, (byte) 15), 120 * 20);//Black clay

        //Advanced blocks
        lavaTicksToMelt.put(new MaterialData(Material.STEP, (byte) 0), 120 * 20);//Stone slab
        lavaTicksToMelt.put(new MaterialData(Material.STEP, (byte) 1), 120 * 20);//Sandstone slab
        lavaTicksToMelt.put(new MaterialData(Material.STEP, (byte) 3), 120 * 20);//Cobble slab
        lavaTicksToMelt.put(new MaterialData(Material.STEP, (byte) 4), 120 * 20);//Brick slab
        lavaTicksToMelt.put(new MaterialData(Material.STEP, (byte) 5), 120 * 20);//Stone brick slab
        lavaTicksToMelt.put(new MaterialData(Material.STEP, (byte) 7), 120 * 20);//Quartz slab
        lavaTicksToMelt.put(new MaterialData(Material.STEP, (byte) 8), 120 * 20);//Upper Stone slab
        lavaTicksToMelt.put(new MaterialData(Material.STEP, (byte) 9), 120 * 20);//Upper Sandstone slab
        lavaTicksToMelt.put(new MaterialData(Material.STEP, (byte) 11), 120 * 20);//Upper Cobble slab
        lavaTicksToMelt.put(new MaterialData(Material.STEP, (byte) 12), 120 * 20);//Upper Brick slab
        lavaTicksToMelt.put(new MaterialData(Material.STEP, (byte) 13), 120 * 20);//Upper Stone brick slab
        lavaTicksToMelt.put(new MaterialData(Material.STEP, (byte) 15), 120 * 20);//Upper Quartz slab
        lavaTicksToMelt.put(new MaterialData(Material.STONE_SLAB2, (byte) 0), 120 * 20);//Red sandstone slab
        lavaTicksToMelt.put(new MaterialData(Material.STONE_SLAB2, (byte) 8), 120 * 20);//Upper red sandstone slab
        lavaTicksToMelt.put(new MaterialData(Material.DOUBLE_STEP, (byte) 0), 120 * 20);//Double Stone slab
        lavaTicksToMelt.put(new MaterialData(Material.DOUBLE_STEP, (byte) 1), 120 * 20);//Double Sandstone slab
        lavaTicksToMelt.put(new MaterialData(Material.DOUBLE_STEP, (byte) 3), 120 * 20);//Double Cobble slab
        lavaTicksToMelt.put(new MaterialData(Material.DOUBLE_STEP, (byte) 4), 120 * 20);//Double Brick slab
        lavaTicksToMelt.put(new MaterialData(Material.DOUBLE_STEP, (byte) 5), 120 * 20);//Double Stone brick slab
        lavaTicksToMelt.put(new MaterialData(Material.DOUBLE_STEP, (byte) 7), 120 * 20);//Double Quartz slab
        lavaTicksToMelt.put(new MaterialData(Material.DOUBLE_STEP, (byte) 8), 120 * 20);//Smooth Double Stone slab
        lavaTicksToMelt.put(new MaterialData(Material.DOUBLE_STEP, (byte) 9), 120 * 20);//Smooth Double Sandstone slab
        lavaTicksToMelt.put(new MaterialData(Material.DOUBLE_STEP, (byte) 15), 120 * 20);//Smooth Double Quartz slab
        lavaTicksToMelt.put(new MaterialData(Material.DOUBLE_STONE_SLAB2, (byte) 0), 120 * 20);//Double red sandstone slab
        lavaTicksToMelt.put(new MaterialData(Material.DOUBLE_STONE_SLAB2, (byte) 8), 120 * 20);//Smooth double red sandstone slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 0), 30 * 20);//Oak slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 1), 30 * 20);//Spruce slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 2), 30 * 20);//Birch slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 3), 30 * 20);//Jungle slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 4), 30 * 20);//Acacia slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 5), 30 * 20);//Dark oak slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 8), 30 * 20);//Upper Oak slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 9), 30 * 20);//Upper Spruce slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 10), 30 * 20);//Upper Birch slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 11), 30 * 20);//Upper Jungle slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 12), 30 * 20);//Upper Acacia slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_STEP, (byte) 13), 30 * 20);//Upper Dark oak slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 0), 30 * 20);//Double Oak slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 1), 30 * 20);//Double Spruce slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 2), 30 * 20);//Double Birch slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 3), 30 * 20);//Double Jungle slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 4), 30 * 20);//Double Acacia slab
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 5), 30 * 20);//Double Dark oak slab
        lavaTicksToMelt.put(new MaterialData(Material.MOSSY_COBBLESTONE), 150 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.SMOOTH_BRICK, (byte) 2), 120 * 20);//Cracked stone brick
        lavaTicksToMelt.put(new MaterialData(Material.GLASS), 110 * 20);

        //Survivor blocks
        lavaTicksToMelt.put(new MaterialData(Material.PACKED_ICE), 30 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.BRICK), 120 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.SMOOTH_BRICK, (byte) 0), 180 * 20);//Stone brick
        lavaTicksToMelt.put(new MaterialData(Material.THIN_GLASS), 150 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.IRON_FENCE), 150 * 20);//Iron bars
        lavaTicksToMelt.put(new MaterialData(Material.IRON_BLOCK), 180 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.LADDER), 30 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.ICE), 10 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.QUARTZ_BLOCK, (byte) 0), 150 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.QUARTZ_BLOCK, (byte) 1), 150 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.QUARTZ_BLOCK, (byte) 2), 150 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.WOOL, (byte) 0), 20 * 20);//White wool
        lavaTicksToMelt.put(new MaterialData(Material.WOOL, (byte) 1), 20 * 20);//Orange wool
        lavaTicksToMelt.put(new MaterialData(Material.WOOL, (byte) 2), 20 * 20);//Magenta wool
        lavaTicksToMelt.put(new MaterialData(Material.WOOL, (byte) 3), 20 * 20);//Light blue wool
        lavaTicksToMelt.put(new MaterialData(Material.WOOL, (byte) 4), 20 * 20);//Yellow wool
        lavaTicksToMelt.put(new MaterialData(Material.WOOL, (byte) 5), 20 * 20);//Lime wool
        lavaTicksToMelt.put(new MaterialData(Material.WOOL, (byte) 6), 20 * 20);//Pink wool
        lavaTicksToMelt.put(new MaterialData(Material.WOOL, (byte) 7), 20 * 20);//Gray wool
        lavaTicksToMelt.put(new MaterialData(Material.WOOL, (byte) 8), 20 * 20);//Light gray wool
        lavaTicksToMelt.put(new MaterialData(Material.WOOL, (byte) 9), 20 * 20);//Cyan wool
        lavaTicksToMelt.put(new MaterialData(Material.WOOL, (byte) 10), 20 * 20);//Purple wool
        lavaTicksToMelt.put(new MaterialData(Material.WOOL, (byte) 11), 20 * 20);//Blue wool
        lavaTicksToMelt.put(new MaterialData(Material.WOOL, (byte) 12), 20 * 20);//Brown wool
        lavaTicksToMelt.put(new MaterialData(Material.WOOL, (byte) 13), 20 * 20);//Green wool
        lavaTicksToMelt.put(new MaterialData(Material.WOOL, (byte) 14), 20 * 20);//Red wool
        lavaTicksToMelt.put(new MaterialData(Material.WOOL, (byte) 15), 20 * 20);//Black wool

        //Trusted blocks
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_DOOR), 30 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.WOODEN_DOOR), 30 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.FENCE), 30 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.WOOD_STAIRS), 30 * 20);//Oak stairs
        lavaTicksToMelt.put(new MaterialData(Material.COBBLESTONE_STAIRS), 120 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.BRICK_STAIRS), 120 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.SMOOTH_STAIRS), 180 * 20);//Stone brick stairs
        lavaTicksToMelt.put(new MaterialData(Material.SANDSTONE_STAIRS), 120 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.RED_SANDSTONE_STAIRS), 120 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.SPRUCE_WOOD_STAIRS), 30 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.BIRCH_WOOD_STAIRS), 30 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.JUNGLE_WOOD_STAIRS), 30 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.QUARTZ_STAIRS), 150 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.ACACIA_STAIRS), 30 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.DARK_OAK_STAIRS), 30 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.COBBLE_WALL, (byte) 0), 120 * 20);//Cobblestone wall
        lavaTicksToMelt.put(new MaterialData(Material.COBBLE_WALL, (byte) 1), 120 * 20);//Mossy cobblestone wall
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 0), 110 * 20);//White glass
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 1), 110 * 20);//Orange glass
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 2), 110 * 20);//Magenta glass
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 3), 110 * 20);//Light blue glass
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 4), 110 * 20);//Yellow glass
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 5), 110 * 20);//Lime glass
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 6), 110 * 20);//Pink glass
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 7), 110 * 20);//Gray glass
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 8), 110 * 20);//Light gray glass
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 9), 110 * 20);//Cyan glass
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 10), 110 * 20);//Purple glass
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 11), 110 * 20);//Blue glass
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 12), 110 * 20);//Brown glass
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 13), 110 * 20);//Green glass
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 14), 110 * 20);//Red glass
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS, (byte) 15), 110 * 20);//Black glass
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 0), 110 * 20);//White glass pane
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 1), 110 * 20);//Orange glass pane
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 2), 110 * 20);//Magenta glass pane
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 3), 110 * 20);//Light blue glass pane
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 4), 110 * 20);//Yellow glass pane
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 5), 110 * 20);//Lime glass pane
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 6), 110 * 20);//Pink glass pane
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), 110 * 20);//Gray glass pane
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 8), 110 * 20);//Light gray glass pane
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 9), 110 * 20);//Cyan glass pane
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 10), 110 * 20);//Purple glass pane
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 11), 110 * 20);//Blue glass pane
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 12), 110 * 20);//Brown glass pane
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 13), 110 * 20);//Green glass pane
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 14), 110 * 20);//Red glass pane
        lavaTicksToMelt.put(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 15), 110 * 20);//Black glass pane
        lavaTicksToMelt.put(new MaterialData(Material.PRISMARINE, (byte) 0), 210 * 20);//Prismarine
        lavaTicksToMelt.put(new MaterialData(Material.PRISMARINE, (byte) 1), 210 * 20);//Prismarine Bricks
        lavaTicksToMelt.put(new MaterialData(Material.PRISMARINE, (byte) 2), 210 * 20);//Dark Prismarine


        //Elder blocks
        lavaTicksToMelt.put(new MaterialData(Material.GLOWSTONE), 225 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.STEP, (byte) 6), 240 * 20);//Nether brick slab
        lavaTicksToMelt.put(new MaterialData(Material.STEP, (byte) 14), 240 * 20);//Upper Nether brick slab
        lavaTicksToMelt.put(new MaterialData(Material.DOUBLE_STEP, (byte) 6), 240 * 20);//Double Nether brick slab
        lavaTicksToMelt.put(new MaterialData(Material.NETHER_FENCE), 240 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.NETHERRACK), 240 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.NETHER_BRICK), 240 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.NETHER_BRICK_STAIRS), 240 * 20);
        lavaTicksToMelt.put(new MaterialData(Material.ENDER_STONE), 270 * 20);


        //Donnor blocks Instant melt)
        lavaTicksToMelt.put(new MaterialData(Material.WOOD, (byte) 1), 0);//Spruce planks
        lavaTicksToMelt.put(new MaterialData(Material.WOOD, (byte) 2), 0);//Birch planks
        lavaTicksToMelt.put(new MaterialData(Material.WOOD, (byte) 3), 0);//Jungle planks
        lavaTicksToMelt.put(new MaterialData(Material.WOOD, (byte) 4), 0);//Acacia planks
        lavaTicksToMelt.put(new MaterialData(Material.WOOD, (byte) 5), 0);//Dark oak planks
        lavaTicksToMelt.put(new MaterialData(Material.SAND, (byte) 1), 0);//Red sand
        lavaTicksToMelt.put(new MaterialData(Material.SPRUCE_FENCE), 0);
        lavaTicksToMelt.put(new MaterialData(Material.BIRCH_FENCE), 0);
        lavaTicksToMelt.put(new MaterialData(Material.JUNGLE_FENCE), 0);
        lavaTicksToMelt.put(new MaterialData(Material.ACACIA_FENCE), 0);
        lavaTicksToMelt.put(new MaterialData(Material.DARK_OAK_FENCE), 0);
        lavaTicksToMelt.put(new MaterialData(Material.FENCE_GATE), 0);
        lavaTicksToMelt.put(new MaterialData(Material.SPRUCE_FENCE_GATE), 0);
        lavaTicksToMelt.put(new MaterialData(Material.BIRCH_FENCE_GATE), 0);
        lavaTicksToMelt.put(new MaterialData(Material.JUNGLE_FENCE_GATE), 0);
        lavaTicksToMelt.put(new MaterialData(Material.ACACIA_FENCE_GATE), 0);
        lavaTicksToMelt.put(new MaterialData(Material.DARK_OAK_FENCE_GATE), 0);
        lavaTicksToMelt.put(new MaterialData(Material.SPRUCE_DOOR), 0);
        lavaTicksToMelt.put(new MaterialData(Material.SPRUCE_DOOR_ITEM), 0);
        lavaTicksToMelt.put(new MaterialData(Material.BIRCH_DOOR), 0);
        lavaTicksToMelt.put(new MaterialData(Material.BIRCH_DOOR_ITEM), 0);
        lavaTicksToMelt.put(new MaterialData(Material.JUNGLE_DOOR), 0);
        lavaTicksToMelt.put(new MaterialData(Material.JUNGLE_DOOR_ITEM), 0);
        lavaTicksToMelt.put(new MaterialData(Material.ACACIA_DOOR), 0);
        lavaTicksToMelt.put(new MaterialData(Material.ACACIA_DOOR_ITEM), 0);
        lavaTicksToMelt.put(new MaterialData(Material.DARK_OAK_DOOR), 0);
        lavaTicksToMelt.put(new MaterialData(Material.DARK_OAK_DOOR_ITEM), 0);
        lavaTicksToMelt.put(new MaterialData(Material.SEA_LANTERN), 0);
        lavaTicksToMelt.put(new MaterialData(Material.CARPET, (byte) 0), 0);//White carpet
        lavaTicksToMelt.put(new MaterialData(Material.CARPET, (byte) 1), 0);//Orange carpet
        lavaTicksToMelt.put(new MaterialData(Material.CARPET, (byte) 2), 0);//Magenta carpet
        lavaTicksToMelt.put(new MaterialData(Material.CARPET, (byte) 3), 0);//Light blue carpet
        lavaTicksToMelt.put(new MaterialData(Material.CARPET, (byte) 4), 0);//Yellow carpet
        lavaTicksToMelt.put(new MaterialData(Material.CARPET, (byte) 5), 0);//Lime carpet
        lavaTicksToMelt.put(new MaterialData(Material.CARPET, (byte) 6), 0);//Pink carpet
        lavaTicksToMelt.put(new MaterialData(Material.CARPET, (byte) 7), 0);//Gray carpet
        lavaTicksToMelt.put(new MaterialData(Material.CARPET, (byte) 8), 0);//Light gray carpet
        lavaTicksToMelt.put(new MaterialData(Material.CARPET, (byte) 9), 0);//Cyan carpet
        lavaTicksToMelt.put(new MaterialData(Material.CARPET, (byte) 10), 0);//Purple carpet
        lavaTicksToMelt.put(new MaterialData(Material.CARPET, (byte) 11), 0);//Blue carpet
        lavaTicksToMelt.put(new MaterialData(Material.CARPET, (byte) 12), 0);//Brown carpet
        lavaTicksToMelt.put(new MaterialData(Material.CARPET, (byte) 13), 0);//Green carpet
        lavaTicksToMelt.put(new MaterialData(Material.CARPET, (byte) 14), 0);//Red carpet
        lavaTicksToMelt.put(new MaterialData(Material.CARPET, (byte) 15), 0);//Black carpet
        lavaTicksToMelt.put(new MaterialData(Material.FLOWER_POT_ITEM), 0);
        lavaTicksToMelt.put(new MaterialData(Material.FLOWER_POT), 0);
        lavaTicksToMelt.put(new MaterialData(Material.BOOKSHELF), 0);
        lavaTicksToMelt.put(new MaterialData(Material.YELLOW_FLOWER), 0);
        lavaTicksToMelt.put(new MaterialData(Material.RED_ROSE, (byte) 0), 0);//Poppy
        lavaTicksToMelt.put(new MaterialData(Material.RED_ROSE, (byte) 1), 0);//Blue Orchid
        lavaTicksToMelt.put(new MaterialData(Material.RED_ROSE, (byte) 2), 0);//Allium
        lavaTicksToMelt.put(new MaterialData(Material.RED_ROSE, (byte) 3), 0);//Azure Bluet
        lavaTicksToMelt.put(new MaterialData(Material.RED_ROSE, (byte) 4), 0);//Red Tulip
        lavaTicksToMelt.put(new MaterialData(Material.RED_ROSE, (byte) 5), 0);//Orange Tulip
        lavaTicksToMelt.put(new MaterialData(Material.RED_ROSE, (byte) 6), 0);//White Tulip
        lavaTicksToMelt.put(new MaterialData(Material.RED_ROSE, (byte) 7), 0);//Pink Tulip
        lavaTicksToMelt.put(new MaterialData(Material.RED_ROSE, (byte) 8), 0);//Oxeye Daisy


        //Unburnable
        lavaTicksToMelt.put(new MaterialData(Material.BEDROCK), -1);
        lavaTicksToMelt.put(new MaterialData(Material.OBSIDIAN), -1);
        lavaTicksToMelt.put(new MaterialData(Material.BARRIER), -1);


        for (Material m : Material.values())
            if (m.isSolid()) {
                if (!lavaTicksToMelt.containsKey(new MaterialData(m)))
                    lavaTicksToMelt.put(new MaterialData(m), 30 * 20);
                if (!waterTicksToMelt.containsKey(new MaterialData(m)))
                    waterTicksToMelt.put(new MaterialData(m), lavaTicksToMelt.get(new MaterialData(m)));
            }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClassicPhysics(ClassicPhysicsEvent event) {
        synchronized (toTasks) {
            if (!event.isClassicEvent() || Gamemode.getCurrentGame().hasEnded() || event.getLocation() == null || event.getLocation().getWorld() == null ||
                    !event.getLocation().getChunk().isLoaded() || event.getLocation().getBlock() == null)
                return;

            HashMap<MaterialData, Integer> ticksToMelt;
            Material type = event.getLogicContainer().logicFor();
            if (type == Material.LAVA || type == Material.STATIONARY_LAVA)
                ticksToMelt = lavaTicksToMelt;
            else
                ticksToMelt = waterTicksToMelt;

            LavaMap map = Gamemode.getCurrentMap();
            if (map.isInSafeZone(event.getLocation())) {
                event.setCancelled(true); //Do not allow physics inside the safezone!
                return;
            }

            Block blockChecking = event.getOldBlock();
            if (blockChecking.getType().equals(Material.AIR) || blockChecking.hasMetadata("classic_block") || blockChecking.isLiquid())
                return;

            MaterialData dat = new MaterialData(blockChecking.getType(), blockChecking.getData());
            if (!ticksToMelt.containsKey(dat))
                dat = new MaterialData(blockChecking.getType());

            if (ticksToMelt.containsKey(dat)) {
                event.setCancelled(true);
                long meltTicks = ticksToMelt.get(dat);
                if (meltTicks < 0) //It's unburnable
                    return;
                if (!blockChecking.hasMetadata("player_placed"))
                    meltTicks *= 0.5;
                ConcurrentLinkedQueue<BlockTaskInfo> temp = new ConcurrentLinkedQueue<>();
                Location location = event.getLocation();
                if (toTasks.containsKey(location) && toTasks.get(location) != null && toTasks.get(location).size() > 0)
                    temp = toTasks.get(location);
                temp.add(new BlockTaskInfo(event.getLogicContainer().logicFor(), event.getFrom(), blockChecking, meltTicks));
                toTasks.put(event.getLocation(), temp);
            }
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
                                    Lavasurvival.INSTANCE.getPhysicsHandler().placeClassicBlockAt(loc, b.getLogicFor(), b.getFrom());
                                    cancelLocation(loc);
                                }
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

    public static String getLavaMeltTimeAsString(MaterialData data) {
        int seconds = lavaTicksToMelt.containsKey(data) ? lavaTicksToMelt.get(data) : 0;
        if (seconds < 0)
            return "Never";
        seconds = seconds / 20;
        if (seconds == 0)
            return "Immediately";
        else
            return seconds + " Second" + (seconds == 1 ? "" : "s");
    }

    public static String getWaterMeltTimeAsString(MaterialData data) {
        int seconds = waterTicksToMelt.containsKey(data) ? waterTicksToMelt.get(data) : 0;
        if (seconds < 0)
            return "Never";
        seconds = seconds / 20;
        if (seconds == 0)
            return "Immediately";
        else
            return seconds + " Second" + (seconds == 1 ? "" : "s");
    }

    private static void cancelAllTasks() {
        for (Location l : toTasks.keySet())
            cancelLocation(l);
    }

    public void cleanup() {
        cancelAllTasks();
        //PHYSICS_TICK.cancel();
        //HandlerList.unregisterAll(this);
    }

    public void prepare() {
        tickCount = 0;
        PHYSICS_TICK.runTaskTimerAsynchronously(Lavasurvival.INSTANCE, 0, 1);
    }

    private class BlockTaskInfo {
        private long ticksToMelt, startTick;
        private Location from;
        private Material logicFor;
        private Block oldBlock;

        public BlockTaskInfo(Material logicFor, Location from, Block oldBlock, long ticksToMelt) {
            this.startTick = tickCount;
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