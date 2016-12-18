package me.eddiep.minecraft.ls.system;

import me.eddiep.handles.ClassicPhysicsEvent;
import me.eddiep.minecraft.ls.Lavasurvival;
import me.eddiep.minecraft.ls.game.Gamemode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PhysicsListener implements Listener {
    private static final HashMap<MaterialData, Integer> lavaTicksToMelt = new HashMap<>();
    private static final HashMap<MaterialData, Integer> waterTicksToMelt = new HashMap<>();
    private static final ConcurrentHashMap<Location, ConcurrentLinkedQueue<BlockTaskInfo>> toTasks = new ConcurrentHashMap<>();
    private static final Random RANDOM = new Random();

    public PhysicsListener() {
        setup();
    }

    @SuppressWarnings("deprecation")
    private static void setup() {
        if (lavaTicksToMelt.size() > 0 && waterTicksToMelt.size() > 0)
            return;

        //Default blocks
        addMeltTime(new MaterialData(Material.TORCH), 20);
        addMeltTime(new MaterialData(Material.WOOD, (byte) 0), 30 * 20, 120 * 20);//Oak plank
        addMeltTime(new MaterialData(Material.DIRT), 90 * 20, 30 * 20);
        addMeltTime(new MaterialData(Material.GRASS), 90 * 20, 30 * 20);
        addMeltTime(new MaterialData(Material.SAND, (byte) 0), 90 * 20, 30 * 20);//Sand
        addMeltTime(new MaterialData(Material.COBBLESTONE), 120 * 20);

        //Basic blocks
        addMeltTime(new MaterialData(Material.GRAVEL), 120 * 20, 30 * 20);
        addMeltTime(new MaterialData(Material.STONE), 120 * 20);
        addMeltTime(new MaterialData(Material.LOG, (byte) 0), 30 * 20, 120 * 20);//Oak log
        addMeltTime(new MaterialData(Material.LOG, (byte) 1), 30 * 20, 120 * 20);//Spruce log
        addMeltTime(new MaterialData(Material.LOG, (byte) 2), 30 * 20, 120 * 20);//Birch log
        addMeltTime(new MaterialData(Material.LOG, (byte) 3), 30 * 20, 120 * 20);//Jungle log
        addMeltTime(new MaterialData(Material.LOG_2, (byte) 0), 30 * 20, 120 * 20);//Acacia log
        addMeltTime(new MaterialData(Material.LOG_2, (byte) 1), 30 * 20, 120 * 20);//Dark oak log
        addMeltTime(new MaterialData(Material.SANDSTONE), 120 * 20, 90 * 20);
        addMeltTime(new MaterialData(Material.RED_SANDSTONE), 120 * 20, 90 * 20);
        addMeltTime(new MaterialData(Material.HARD_CLAY), 120 * 20, 90 * 20);
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 0), 120 * 20, 90 * 20);//White clay
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 1), 120 * 20, 90 * 20);//Orange clay
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 2), 120 * 20, 90 * 20);//Magenta clay
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 3), 120 * 20, 90 * 20);//Light blue clay
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 4), 120 * 20, 90 * 20);//Yellow clay
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 5), 120 * 20, 90 * 20);//Lime clay
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 6), 120 * 20, 90 * 20);//Pink clay
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 7), 120 * 20, 90 * 20);//Gray clay
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 8), 120 * 20, 90 * 20);//Light gray clay
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 9), 120 * 20, 90 * 20);//Cyan clay
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 10), 120 * 20, 90 * 20);//Purple clay
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 11), 120 * 20, 90 * 20);//Blue clay
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 12), 120 * 20, 90 * 20);//Brown clay
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 13), 120 * 20, 90 * 20);//Green clay
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 14), 120 * 20, 90 * 20);//Red clay
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 15), 120 * 20, 90 * 20);//Black clay

        //Advanced blocks
        addMeltTime(new MaterialData(Material.STEP, (byte) 0), 120 * 20);//Stone slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 1), 120 * 20, 90 * 20);//Sandstone slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 2), 120 * 20, 90 * 20);//Sandstone slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 3), 120 * 20);//Cobble slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 4), 120 * 20);//Brick slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 5), 120 * 20);//Stone brick slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 7), 120 * 20);//Quartz slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 8), 120 * 20);//Upper Stone slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 9), 120 * 20);//Upper Sandstone slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 11), 120 * 20);//Upper Cobble slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 12), 120 * 20);//Upper Brick slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 13), 120 * 20);//Upper Stone brick slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 15), 120 * 20);//Upper Quartz slab
        addMeltTime(new MaterialData(Material.STONE_SLAB2, (byte) 0), 120 * 20);//Red sandstone slab
        addMeltTime(new MaterialData(Material.STONE_SLAB2, (byte) 8), 120 * 20);//Upper red sandstone slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 0), 120 * 20);//Double Stone slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 1), 120 * 20);//Double Sandstone slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 3), 120 * 20);//Double Cobble slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 4), 120 * 20);//Double Brick slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 5), 120 * 20);//Double Stone brick slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 7), 120 * 20);//Double Quartz slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 8), 120 * 20);//Smooth Double Stone slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 9), 120 * 20);//Smooth Double Sandstone slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 15), 120 * 20);//Smooth Double Quartz slab
        addMeltTime(new MaterialData(Material.DOUBLE_STONE_SLAB2, (byte) 0), 120 * 20);//Double red sandstone slab
        addMeltTime(new MaterialData(Material.DOUBLE_STONE_SLAB2, (byte) 8), 120 * 20);//Smooth double red sandstone slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 0), 30 * 20, 120 * 20);//Oak slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 1), 30 * 20, 120 * 20);//Spruce slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 2), 30 * 20, 120 * 20);//Birch slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 3), 30 * 20, 120 * 20);//Jungle slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 4), 30 * 20, 120 * 20);//Acacia slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 5), 30 * 20, 120 * 20);//Dark oak slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 8), 30 * 20, 120 * 20);//Upper Oak slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 9), 30 * 20, 120 * 20);//Upper Spruce slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 10), 30 * 20, 120 * 20);//Upper Birch slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 11), 30 * 20, 120 * 20);//Upper Jungle slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 12), 30 * 20, 120 * 20);//Upper Acacia slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 13), 30 * 20, 120 * 20);//Upper Dark oak slab
        addMeltTime(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 0), 30 * 20, 120 * 20);//Double Oak slab
        addMeltTime(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 1), 30 * 20, 120 * 20);//Double Spruce slab
        addMeltTime(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 2), 30 * 20, 120 * 20);//Double Birch slab
        addMeltTime(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 3), 30 * 20, 120 * 20);//Double Jungle slab
        addMeltTime(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 4), 30 * 20, 120 * 20);//Double Acacia slab
        addMeltTime(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 5), 30 * 20, 120 * 20);//Double Dark oak slab
        addMeltTime(new MaterialData(Material.MOSSY_COBBLESTONE), 150 * 20, 90 * 20);
        addMeltTime(new MaterialData(Material.SMOOTH_BRICK, (byte) 2), 120 * 20);//Cracked stone brick
        addMeltTime(new MaterialData(Material.GLASS), 90 * 20, 120 * 20);

        //Survivor blocks
        addMeltTime(new MaterialData(Material.PACKED_ICE), 30 * 20, 180 * 20);
        addMeltTime(new MaterialData(Material.BRICK), 180 * 20);
        addMeltTime(new MaterialData(Material.SMOOTH_BRICK, (byte) 0), 180 * 20);//Stone brick
        addMeltTime(new MaterialData(Material.THIN_GLASS), 90 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.IRON_FENCE), 150 * 20);//Iron bars
        addMeltTime(new MaterialData(Material.IRON_BLOCK), 180 * 20);
        addMeltTime(new MaterialData(Material.LADDER), 10 * 20);
        addMeltTime(new MaterialData(Material.TRAP_DOOR), 10 * 20);
        addMeltTime(new MaterialData(Material.ICE), 10 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.QUARTZ_BLOCK, (byte) 0), 150 * 20);
        addMeltTime(new MaterialData(Material.QUARTZ_BLOCK, (byte) 1), 150 * 20);
        addMeltTime(new MaterialData(Material.QUARTZ_BLOCK, (byte) 2), 150 * 20);
        addMeltTime(new MaterialData(Material.WOOL, (byte) 0), 20 * 20);//White wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 1), 20 * 20);//Orange wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 2), 20 * 20);//Magenta wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 3), 20 * 20);//Light blue wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 4), 20 * 20);//Yellow wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 5), 20 * 20);//Lime wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 6), 20 * 20);//Pink wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 7), 20 * 20);//Gray wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 8), 20 * 20);//Light gray wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 9), 20 * 20);//Cyan wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 10), 20 * 20);//Purple wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 11), 20 * 20);//Blue wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 12), 20 * 20);//Brown wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 13), 20 * 20);//Green wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 14), 20 * 20);//Red wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 15), 20 * 20);//Black wool
        addMeltTime(new MaterialData(Material.NETHER_WART_BLOCK), 150 * 20);

        //Trusted blocks
        addMeltTime(new MaterialData(Material.WOOD_DOOR), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.WOODEN_DOOR), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.FENCE), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.WOOD_STAIRS), 30 * 20, 120 * 20);//Oak stairs
        addMeltTime(new MaterialData(Material.COBBLESTONE_STAIRS), 120 * 20);
        addMeltTime(new MaterialData(Material.BRICK_STAIRS), 180 * 20);
        addMeltTime(new MaterialData(Material.SMOOTH_STAIRS), 180 * 20);//Stone brick stairs
        addMeltTime(new MaterialData(Material.SANDSTONE_STAIRS), 120 * 20, 90 * 20);
        addMeltTime(new MaterialData(Material.RED_SANDSTONE_STAIRS), 120 * 20, 90 * 20);
        addMeltTime(new MaterialData(Material.SPRUCE_WOOD_STAIRS), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.BIRCH_WOOD_STAIRS), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.JUNGLE_WOOD_STAIRS), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.QUARTZ_STAIRS), 150 * 20);
        addMeltTime(new MaterialData(Material.ACACIA_STAIRS), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.DARK_OAK_STAIRS), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.COBBLE_WALL, (byte) 0), 120 * 20);//Cobblestone wall
        addMeltTime(new MaterialData(Material.COBBLE_WALL, (byte) 1), 150 * 20, 90 * 20);//Mossy cobblestone wall
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 0), 90 * 20, 120 * 20);//White glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 1), 90 * 20, 120 * 20);//Orange glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 2), 90 * 20, 120 * 20);//Magenta glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 3), 90 * 20, 120 * 20);//Light blue glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 4), 90 * 20, 120 * 20);//Yellow glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 5), 90 * 20, 120 * 20);//Lime glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 6), 90 * 20, 120 * 20);//Pink glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 7), 90 * 20, 120 * 20);//Gray glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 8), 90 * 20, 120 * 20);//Light gray glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 9), 90 * 20, 120 * 20);//Cyan glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 10), 90 * 20, 120 * 20);//Purple glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 11), 90 * 20, 120 * 20);//Blue glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 12), 90 * 20, 120 * 20);//Brown glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 13), 90 * 20, 120 * 20);//Green glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 14), 90 * 20, 120 * 20);//Red glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 15), 90 * 20, 120 * 20);//Black glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 0), 90 * 20, 120 * 20);//White glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 1), 90 * 20, 120 * 20);//Orange glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 2), 90 * 20, 120 * 20);//Magenta glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 3), 90 * 20, 120 * 20);//Light blue glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 4), 90 * 20, 120 * 20);//Yellow glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 5), 90 * 20, 120 * 20);//Lime glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 6), 90 * 20, 120 * 20);//Pink glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), 90 * 20, 120 * 20);//Gray glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 8), 90 * 20, 120 * 20);//Light gray glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 9), 90 * 20, 120 * 20);//Cyan glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 10), 90 * 20, 120 * 20);//Purple glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 11), 90 * 20, 120 * 20);//Blue glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 12), 90 * 20, 120 * 20);//Brown glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 13), 90 * 20, 120 * 20);//Green glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 14), 90 * 20, 120 * 20);//Red glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 15), 90 * 20, 120 * 20);//Black glass pane
        addMeltTime(new MaterialData(Material.PRISMARINE, (byte) 0), 120 * 20, 210 * 20);//Prismarine
        addMeltTime(new MaterialData(Material.PRISMARINE, (byte) 1), 120 * 20, 210 * 20);//Prismarine Bricks
        addMeltTime(new MaterialData(Material.PRISMARINE, (byte) 2), 120 * 20, 210 * 20);//Dark Prismarine

        //Elder blocks
        addMeltTime(new MaterialData(Material.GLOWSTONE), 225 * 20);
        addMeltTime(new MaterialData(Material.NETHERRACK), 270 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.MAGMA), 270 * 20, 120 * 20);//Magma block
        addMeltTime(new MaterialData(Material.NETHER_BRICK), 240 * 20);
        addMeltTime(new MaterialData(Material.RED_NETHER_BRICK), 240 * 20);
        addMeltTime(new MaterialData(Material.NETHER_FENCE), 240 * 20);
        addMeltTime(new MaterialData(Material.STEP, (byte) 6), 240 * 20);//Nether brick slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 14), 240 * 20);//Upper Nether brick slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 6), 240 * 20);//Double Nether brick slab
        addMeltTime(new MaterialData(Material.NETHER_BRICK_STAIRS), 240 * 20);
        addMeltTime(new MaterialData(Material.PURPUR_BLOCK), 240 * 20);
        addMeltTime(new MaterialData(Material.PURPUR_PILLAR), 240 * 20);
        addMeltTime(new MaterialData(Material.PURPUR_STAIRS), 240 * 20);
        addMeltTime(new MaterialData(Material.PURPUR_SLAB), 240 * 20);
        addMeltTime(new MaterialData(Material.PURPUR_DOUBLE_SLAB), 240 * 20);
        addMeltTime(new MaterialData(Material.ENDER_STONE), 270 * 20);
        addMeltTime(new MaterialData(Material.END_BRICKS), 270 * 20);
        addMeltTime(new MaterialData(Material.END_ROD), 180 * 20);

        //Donor blocks Instant melt)
        addMeltTime(new MaterialData(Material.WOOD, (byte) 1), 30 * 20, 120 * 20);//Spruce planks
        addMeltTime(new MaterialData(Material.WOOD, (byte) 2), 30 * 20, 120 * 20);//Birch planks
        addMeltTime(new MaterialData(Material.WOOD, (byte) 3), 30 * 20, 120 * 20);//Jungle planks
        addMeltTime(new MaterialData(Material.WOOD, (byte) 4), 30 * 20, 120 * 20);//Acacia planks
        addMeltTime(new MaterialData(Material.WOOD, (byte) 5), 30 * 20, 120 * 20);//Dark oak planks
        addMeltTime(new MaterialData(Material.SAND, (byte) 1), 90 * 20, 30 * 20);//Red sand
        addMeltTime(new MaterialData(Material.SPRUCE_FENCE), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.BIRCH_FENCE), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.JUNGLE_FENCE), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.ACACIA_FENCE), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.DARK_OAK_FENCE), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.FENCE_GATE), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.SPRUCE_FENCE_GATE), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.BIRCH_FENCE_GATE), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.JUNGLE_FENCE_GATE), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.ACACIA_FENCE_GATE), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.DARK_OAK_FENCE_GATE), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.SPRUCE_DOOR), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.SPRUCE_DOOR_ITEM), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.BIRCH_DOOR), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.BIRCH_DOOR_ITEM), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.JUNGLE_DOOR), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.JUNGLE_DOOR_ITEM), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.ACACIA_DOOR), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.ACACIA_DOOR_ITEM), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.DARK_OAK_DOOR), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.DARK_OAK_DOOR_ITEM), 30 * 20, 120 * 20);
        addMeltTime(new MaterialData(Material.SEA_LANTERN), 0);
        addMeltTime(new MaterialData(Material.CARPET, (byte) 0), 20 * 20);//White carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 1), 20 * 20);//Orange carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 2), 20 * 20);//Magenta carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 3), 20 * 20);//Light blue carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 4), 20 * 20);//Yellow carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 5), 20 * 20);//Lime carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 6), 20 * 20);//Pink carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 7), 20 * 20);//Gray carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 8), 20 * 20);//Light gray carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 9), 20 * 20);//Cyan carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 10), 20 * 20);//Purple carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 11), 20 * 20);//Blue carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 12), 20 * 20);//Brown carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 13), 20 * 20);//Green carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 14), 20 * 20);//Red carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 15), 20 * 20);//Black carpet
        addMeltTime(new MaterialData(Material.FLOWER_POT_ITEM), 0);
        addMeltTime(new MaterialData(Material.FLOWER_POT), 0);
        addMeltTime(new MaterialData(Material.BOOKSHELF), 0);
        addMeltTime(new MaterialData(Material.YELLOW_FLOWER), 0);
        addMeltTime(new MaterialData(Material.RED_ROSE, (byte) 0), 0);//Poppy
        addMeltTime(new MaterialData(Material.RED_ROSE, (byte) 1), 0);//Blue Orchid
        addMeltTime(new MaterialData(Material.RED_ROSE, (byte) 2), 0);//Allium
        addMeltTime(new MaterialData(Material.RED_ROSE, (byte) 3), 0);//Azure Bluet
        addMeltTime(new MaterialData(Material.RED_ROSE, (byte) 4), 0);//Red Tulip
        addMeltTime(new MaterialData(Material.RED_ROSE, (byte) 5), 0);//Orange Tulip
        addMeltTime(new MaterialData(Material.RED_ROSE, (byte) 6), 0);//White Tulip
        addMeltTime(new MaterialData(Material.RED_ROSE, (byte) 7), 0);//Pink Tulip
        addMeltTime(new MaterialData(Material.RED_ROSE, (byte) 8), 0);//Oxeye Daisy
        addMeltTime(new MaterialData(Material.BONE_BLOCK), 0);
        addMeltTime(new MaterialData(Material.GRASS_PATH), 0);

        //Unmeltable
        addMeltTime(new MaterialData(Material.BEDROCK), -1);
        addMeltTime(new MaterialData(Material.OBSIDIAN), -1);
        addMeltTime(new MaterialData(Material.BARRIER), -1);


        for (Material m : Material.values())
            if (m.isSolid())
                addMeltTime(new MaterialData(m), 30 * 20);//Checks in itself if either already contain it
    }

    private static void addMeltTime(MaterialData data, int time) {
        addMeltTime(data, time, time);
    }

    private static void addMeltTime(MaterialData data, int lava, int water) {
        if (!lavaTicksToMelt.containsKey(data))
            lavaTicksToMelt.put(data, lava);
        if (!waterTicksToMelt.containsKey(data))
            waterTicksToMelt.put(data, water);
    }

    @SuppressWarnings({"deprecation", "unused"})
    @EventHandler(priority = EventPriority.MONITOR)
    public void onClassicPhysics(ClassicPhysicsEvent event) {
        synchronized (toTasks) {
            if (Gamemode.getCurrentGame() == null || Gamemode.getCurrentGame().hasEnded() || event.getLocation() == null || event.getLocation().getWorld() == null ||
                    !event.getLocation().getChunk().isLoaded() || event.getLocation().getBlock() == null || Gamemode.getCurrentMap().isInSafeZone(event.getLocation())) {
                event.setCancelled(true);
                return;
            }

            Block blockChecking = event.getOldBlock();
            if (blockChecking.getType().equals(Material.AIR) || blockChecking.hasMetadata("classic_block") || blockChecking.isLiquid())
                return;

            HashMap<MaterialData, Integer> ticksToMelt;
            Material type = event.getLogicContainer().logicFor();
            if (type == Material.LAVA || type == Material.STATIONARY_LAVA)
                ticksToMelt = lavaTicksToMelt;
            else
                ticksToMelt = waterTicksToMelt;

            MaterialData dat = new MaterialData(blockChecking.getType(), blockChecking.getData());
            if (!ticksToMelt.containsKey(dat))
                dat = new MaterialData(blockChecking.getType());

            if (ticksToMelt.containsKey(dat)) {
                long meltTicks = ticksToMelt.get(dat);
                if (meltTicks < 0) //It's unburnable
                    return;

                double percent = Gamemode.getCurrentMap().getMeltRange() / 100.0;
                int range = (int) (meltTicks * percent + 0.5); //Round normally
                long bonus = RANDOM.nextInt(range + 1);

                if (RANDOM.nextBoolean())
                    meltTicks += bonus;
                else
                    meltTicks -= bonus;

                if (!blockChecking.hasMetadata("player_placed"))
                    meltTicks *= Gamemode.getCurrentMap().getMeltMultiplier();
                if (meltTicks <= 0)
                    return;
                event.setCancelled(true);
                ConcurrentLinkedQueue<BlockTaskInfo> temp;
                Location location = event.getLocation();
                if (toTasks.containsKey(location) && toTasks.get(location) != null && toTasks.get(location).size() > 0) {
                    temp = toTasks.get(location);
                    long lticks = temp.isEmpty() ? 0 : ((BlockTaskInfo) temp.toArray()[0]).getTicksToMelt();
                    if (lticks != 0)
                        meltTicks = lticks;
                } else
                    temp = new ConcurrentLinkedQueue<>();
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
                        if (b != null && tickCount - b.getStartTick() >= b.getTicksToMelt()) {
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
    };

    static void cancelLocation(Location loc) {
        if (toTasks.containsKey(loc))
            toTasks.remove(loc);
    }

    public static String getLavaMeltTimeAsString(MaterialData data) {
        return getMeltTimeAsString(lavaTicksToMelt.containsKey(data) ? lavaTicksToMelt.get(data) : 0);
    }

    public static String getWaterMeltTimeAsString(MaterialData data) {
        return getMeltTimeAsString(waterTicksToMelt.containsKey(data) ? waterTicksToMelt.get(data) : 0);
    }

    private static String getMeltTimeAsString(int seconds) {
        if (seconds < 0)
            return "Never";
        seconds = seconds / 20;
        if (seconds == 0)
            return "Immediately";
        return seconds + " Second" + (seconds == 1 ? "" : "s");
    }

    public static String getLavaMeltRangeTimeAsString(MaterialData data) {
        return getMeltRangeAsString(lavaTicksToMelt.containsKey(data) ? lavaTicksToMelt.get(data) : 0);
    }

    private static String getMeltRangeAsString(int seconds) {
        if (seconds < 0)
            return "Never";
        seconds = seconds / 20;
        if (seconds == 0)
            return "Immediately";
        double percent = Gamemode.getCurrentMap().getMeltRange() / 100.0;
        int range = (int) (seconds * percent);
        int min = seconds - range, max = seconds + range;
        return (min == max ? max : min + " to " + max) + " Second" + (max == 1 ? "" : "s");
    }

    public static String getWaterMeltRangeTimeAsString(MaterialData data) {
        return getMeltRangeAsString(waterTicksToMelt.containsKey(data) ? waterTicksToMelt.get(data) : 0);
    }

    private static void cancelAllTasks() {
        toTasks.keySet().forEach(PhysicsListener::cancelLocation);
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
        private final long ticksToMelt;
        private final long startTick;
        private final Location from;
        private final Material logicFor;
        private final Block oldBlock;

        BlockTaskInfo(Material logicFor, Location from, Block oldBlock, long ticksToMelt) {
            this.startTick = tickCount;
            this.from = from;
            this.logicFor = logicFor;
            this.oldBlock = oldBlock;
            this.ticksToMelt = ticksToMelt;
        }

        long getStartTick() {
            return this.startTick;
        }

        long getTicksToMelt() {
            return this.ticksToMelt;
        }

        public Location getFrom() {
            return this.from;
        }

        Material getLogicFor() {
            return this.logicFor;
        }

        Block getOldBlock() {
            return this.oldBlock;
        }
    }
}