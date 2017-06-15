package me.eddiep.handles;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.FaweQueue;
import me.eddiep.ClassicPhysics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PhysicsEngine {
    private final int SHIFT = 30000000;//30 million
    //Values that shift the specific direction
    private final long X = 67108864;
    private final long Y = 4503599627370496L;
    private final long Z = 1;

    private static final HashMap<MaterialData, Integer> lavaTicksToMelt = new HashMap<>();
    private static final HashMap<MaterialData, Integer> waterTicksToMelt = new HashMap<>();

    @SuppressWarnings("deprecation")
    public PhysicsEngine() {
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

    /**
     * key is YXZ, Value is the melt time of the material of the block.
     * -1 is unmeltable, and -2 is a block of "active" liquid
     */
    private HashMap<Long, Long> meltMap = new HashMap<>();//yxz, melt time of material of block,
    private ArrayList<Long> activeBlocks = new ArrayList<>();//TODO Should this stay an array list?

    private void loadMeltMap(String worldName) {
        File fileMeltMap = new File("plugins/ClassicPhysics", worldName + ".txt");
        if (fileMeltMap.exists())
            try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(fileMeltMap))) {
                meltMap = (HashMap<Long, Long>) is.readObject();
            } catch (IOException | ClassNotFoundException ignored) {
            }
    }

    public static boolean calculateMeltMap(Location left, Location right) {
        //TODO Don't store things with melt timer of -1 or spawn to save space and load time because out of bounds stuff doesn't have physics anyways...
        //TODO add the random range based on the map so that it is always the same? to speed it up and things
        /*
        double percent = Gamemode.getCurrentMap().getMeltRange() / 100.0;
        int range = (int) (meltTicks * percent + 0.5); //Round normally
        long bonus = RANDOM.nextInt(range + 1);

        if (RANDOM.nextBoolean())
            meltTicks += bonus;
        else
            meltTicks -= bonus;
         */
        //TODO check that they are in the same world?
        World w = left.getWorld();
        String worldName = w.getName();
        int x1 = left.getBlockX(), x2 = right.getBlockX(), y1 = left.getBlockY(), y2 = right.getBlockY(), z1 = left.getBlockZ(), z2 = right.getBlockZ();
        if (x2 < x1) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }
        if (y2 < y1) {
            int temp = y1;
            y1 = y2;
            y2 = temp;
        }
        if (z2 < z1) {
            int temp = z1;
            z1 = z2;
            z2 = temp;
        }
        HashMap<Long, Long> meltMap = new HashMap<>();
        PhysicsEngine pe = new PhysicsEngine();
        for (int y = y1; y <= y2; y++) {
            for (int x = x1; x <= x2; x++) {
                for (int z = z1; z <= z2; z++) {
                    Block b = new Location(w, x, y, z).getBlock();
                    long melt = pe.getMeltTime(new MaterialData(b.getType(), b.getData()));
                    if (melt > 1)
                        melt /= 2;
                    meltMap.put(pe.convert(x, y, z), melt);
                }
            }
        }
        File fileMeltMap = new File("plugins/ClassicPhysics", worldName + ".txt");
        if (fileMeltMap.exists()) //Clear it
            fileMeltMap.delete();
        try {
            fileMeltMap.createNewFile();
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

    public boolean isClassicBlock(int x, int y, int z) {
        return isClassicBlock(convert(x, y, z));
    }

    private boolean isClassicBlock(long l) {
        Long lo = meltMap.get(l);
        return lo != null && lo == -2;
    }

    void addMeltTimer(int x, int y, int z, MaterialData type) {
        //TODO remove from active if it is not air. Shouldn't it just remove from active all together?
        long l = convert(x, y, z);
        //toTasks.remove(l);//TODO is this line correct?
        meltMap.put(l, getMeltTime(type));
        long temp = l + X;
        if (isClassicBlock(temp))
            activeBlocks.add(temp);
        temp = l - X;
        if (isClassicBlock(temp))
            activeBlocks.add(temp);
        temp = l + Z;
        if (isClassicBlock(temp))
            activeBlocks.add(temp);
        temp = l - Z;
        if (isClassicBlock(temp))
            activeBlocks.add(temp);
        temp = l + Y;
        if (isClassicBlock(temp))
            activeBlocks.add(temp);
    }

    private long getMeltTime(MaterialData type) {
        //Can use Air or null to remove it
        if (!lavaTicksToMelt.containsKey(type))
            type = new MaterialData(type.getItemType());
        return lavaTicksToMelt.getOrDefault(type, 0);
    }

    private long convert(int x, int y, int z) {
        //TODO check if x, or z is greater than 30 million and if y > 256
        return (long) y << 52 | (long) (x + SHIFT) << 26 | (z + SHIFT);
    }

    private int getX(long l) {
        return (int) ((l >> 26) & 0x3FFFFFF) - SHIFT;
    }

    private int getY(long l) {
        return (int) (l >> 52);
    }

    private int getZ(long l) {
        return (int) (l & 0x3FFFFFF) - SHIFT;
    }

    private void printNorm(long l) {
        System.out.println(getX(l) + " " + getY(l) + " " + getZ(l));
    }

    private boolean running;

    private final BukkitRunnable ticker = new BukkitRunnable() {
        @Override
        public void run() {
            tick();
        }
    };

    void start(String worldName) {
        if (running)
            return;
        meltMap = new HashMap<>();
        activeBlocks = new ArrayList<>();
        meltTimers = new HashMap<>();
        loadMeltMap(worldName);//TODO make loading async
        queue = FaweAPI.createQueue(worldName, true);
        tickCount = 0;
        Bukkit.broadcastMessage(worldName + " loaded.");
        ticker.runTaskTimerAsynchronously(ClassicPhysics.INSTANCE, 0, 1);
        //meltTicker.runTaskTimerAsynchronously(ClassicPhysics.INSTANCE, 0, 1);
    }

    public void end() {
        if (!running)
            return;
        //TODO end queue somehow? clear? or just flush or both
        running = false;
        ticker.cancel();
        //meltTicker.cancel();
        //toTasks.clear();
    }

    private FaweQueue queue;

    private void tick() {
        tickCount++;
        //TODO should melt checker be inside the running loop if so how to easily get the ones that have already been done

        if (!meltTimers.isEmpty()) {
            ArrayList<MeltLocationInfo> melts = meltTimers.get(tickCount);
            if (melts != null)
                for (MeltLocationInfo melt : melts)
                    if (isClassicBlock(melt.from) && meltMap.get(melt.loc) == melt.ticksToMelt)
                        placeAt(melt.loc);
            meltTimers.remove(tickCount);
        }

        if (running) {
            //Bukkit.broadcast(ChatColor.GOLD + "To Ops - " + ChatColor.WHITE + "Skipping current Physics Tick", "Necessities.opBroadcast");
            return;
        }
        //TODO make lava not flow as fast about 1/10th the current speed because of running the tick every tick instead of every 10
        //TODO what is best way to slow lava down? add melt time to air?
        running = true;
        List<Long> lastActive = activeBlocks;
        activeBlocks = new ArrayList<>();
        for (long l : lastActive) {//TODO try taking only some at a time
            attemptFlow(l, l + X);
            attemptFlow(l, l - X);
            attemptFlow(l, l + Z);
            attemptFlow(l, l - Z);
            attemptFlow(l, l - Y);
        }
        queue.flush();//TODO should this be a small number inside instead of default 10000
        running = false;
    }

    private void attemptFlow(long from, long to) {
        Long time = meltMap.get(to);
        if (time == null || time < 0) //Not in map probably should just stop it or Ummeltable
            return;
        else if (time == 0) //Instant melt
            placeAt(to);
        else //Start melt timer For now should probably use the old method
            startMelt(from, to, time);
    }

    void placeAt(int x, int y, int z) {
        placeAt(convert(x, y, z));
    }

    private void placeAt(long to) {//TODO call ClassicBlockPlaceEvent
        meltMap.put(to, -2L);
        activeBlocks.add(to);
        queue.setBlock(getX(to), getY(to), getZ(to), 11);//LAVA
    }

    private HashMap<Long, ArrayList<MeltLocationInfo>> meltTimers;//Should value be queue

    private void startMelt(long from, long to, long time) {
        //TODO map of endMelt->location
        //Would work a lot better because it can just retrieve the ones that should finish melting
        //Maybe endMelt-> info so location the from location (to check that it is still active)
        //TODO how would it cancel if block was broken and changed types or just replaced
        //TODO it would not update if its source got done but melted before the time finished on the to
        //TODO if it stores the melt time it was supposed to be then can compare that
        //TODO should it use higher melt time if bloc was replaced or start from where it is

        long end = tickCount + time;
        ArrayList<MeltLocationInfo> melts = meltTimers.computeIfAbsent(end, k -> new ArrayList<>());
        melts.add(new MeltLocationInfo(to, from, time));
    }

    private long tickCount;

    private class MeltLocationInfo {
        private final long ticksToMelt;
        private final long loc;
        private final long from;

        MeltLocationInfo(long loc, long from, long ticksToMelt) {
            this.loc = loc;
            this.from = from;
            this.ticksToMelt = ticksToMelt;
        }
    }

    //TODO do we want bedrock to just be a block instead of its own thing so we only keep track of where blocks are and let melter say bedrock cannot melt
    //How will be the best way of saying there is already a classic physics block there?
    //Map of location -> cp block / normal block and leave it out if it is air?
    //Should it have all non cp blocks instead so that it checks if it is valid to attempt to move and let the melt engine check then the timer
    //Or should the melt engine move into classic physics so that we do not need to store blocks that have no melt time
    //Keep a list/set/vector of the current cp blocks?
    //Does melt engine actually need to be integrated so new physics works instead of passing on information
    //Aka do we get performance increases by keeping track of the blocks and then what melt time they will have
    //Map of location -> melt time of block at location? cp block no melt time? TODO probably is best idea
    //This would cut out a lot of logic by not caring about the actual world until it has to set a new cp block
    //Setting a new cp block, the updates would then just see the way it came from is unmeltable instead of having to check and store "metadata"
    //Store cp blocks as -2 instead of -1? That way you can determine if a block should add its physics checks back when something occurs nearby
    //Can have a list of new cp blocks and just add them to the world all at once instead of getting the same section multiple times
    //Will need to do something about per map melt times... TODO as well as reimplement the sponges because this will break them
    //Per map melt time could potentially be set in some info about the virtual map it will keep track of with the melt times of the blocks
    //For sponges maybe store it as a different negative number? How would this interfere with melt time of blocks inside
    //
}