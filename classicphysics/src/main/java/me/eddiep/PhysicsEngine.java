package me.eddiep;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.FaweQueue;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftFallingBlock;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class PhysicsEngine implements Listener {
    private static final int SHIFT = 30000000;//30 million
    private static final HashMap<MaterialData, Short> ticksToMelt = new HashMap<>();
    private static final Random RANDOM = new Random();
    private static final short tickModifier = 2;
    private static double percent;
    private static double multiplier;
    //Values that shift the specific direction
    private final long X = 67108864;
    private final long Y = 4503599627370496L;
    private final long Z = 1;
    private final Color warn = Color.fromRGB(244, 66, 244);
    /**
     * key is YXZ, Value is the melt time of the material of the block.
     * -1 is unmeltable, and -2 is a block of "active" liquid
     */
    private HashMap<Long, Short> meltMap = new HashMap<>();//yxz, melt time of material of block,
    private ArrayList<Long> activeBlocks = new ArrayList<>();//TODO Should this stay an array list?
    private HashMap<Long, Short> blockedMap = new HashMap<>();//yxz, number blocked
    private boolean running, stopped;
    private World w;
    private FaweQueue queue;
    private HashMap<Long, ArrayList<MeltLocationInfo>> meltTimers;//Should value be queue
    private long tickCount;
    private HashMap<Long, ArrayList<Long>> spongeTimers = new HashMap<>();
    private HashMap<Long, SpongeInfo> sponges = new HashMap<>();
    private BukkitRunnable ticker = new BukkitRunnable() {
        @Override
        public void run() {
            if (stopped)
                return;
            tick();
            spongeTick();
        }
    };
    private String world;

    @SuppressWarnings("deprecation")
    public PhysicsEngine() {
        if (!ticksToMelt.isEmpty())
            return;
        //Default blocks
        addMeltTime(new MaterialData(Material.TORCH), tickModifier);
        addMeltTime(new MaterialData(Material.WOOD, (byte) 0), 30 * tickModifier);//Oak plank
        addMeltTime(new MaterialData(Material.DIRT), 90 * tickModifier);
        addMeltTime(new MaterialData(Material.GRASS), 90 * tickModifier);
        addMeltTime(new MaterialData(Material.SAND, (byte) 0), 90 * tickModifier);//Sand
        addMeltTime(new MaterialData(Material.COBBLESTONE), 120 * tickModifier);

        //Basic blocks
        addMeltTime(new MaterialData(Material.GRAVEL), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.STONE), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.LOG, (byte) 0), 30 * tickModifier);//Oak log
        addMeltTime(new MaterialData(Material.LOG, (byte) 1), 30 * tickModifier);//Spruce log
        addMeltTime(new MaterialData(Material.LOG, (byte) 2), 30 * tickModifier);//Birch log
        addMeltTime(new MaterialData(Material.LOG, (byte) 3), 30 * tickModifier);//Jungle log
        addMeltTime(new MaterialData(Material.LOG_2, (byte) 0), 30 * tickModifier);//Acacia log
        addMeltTime(new MaterialData(Material.LOG_2, (byte) 1), 30 * tickModifier);//Dark oak log
        addMeltTime(new MaterialData(Material.SANDSTONE), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.RED_SANDSTONE), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.HARD_CLAY), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 0), 120 * tickModifier);//White terracotta
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 1), 120 * tickModifier);//Orange terracotta
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 2), 120 * tickModifier);//Magenta terracotta
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 3), 120 * tickModifier);//Light blue terracotta
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 4), 120 * tickModifier);//Yellow terracotta
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 5), 120 * tickModifier);//Lime terracotta
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 6), 120 * tickModifier);//Pink terracotta
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 7), 120 * tickModifier);//Gray terracotta
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 8), 120 * tickModifier);//Light gray terracotta
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 9), 120 * tickModifier);//Cyan terracotta
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 10), 120 * tickModifier);//Purple terracotta
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 11), 120 * tickModifier);//Blue terracotta
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 12), 120 * tickModifier);//Brown terracotta
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 13), 120 * tickModifier);//Green terracotta
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 14), 120 * tickModifier);//Red terracotta
        addMeltTime(new MaterialData(Material.STAINED_CLAY, (byte) 15), 120 * tickModifier);//Black terracotta
        addMeltTime(new MaterialData(Material.CONCRETE_POWDER, (byte) 0), 90 * tickModifier);//White concrete powder
        addMeltTime(new MaterialData(Material.CONCRETE_POWDER, (byte) 1), 90 * tickModifier);//Orange concrete powder
        addMeltTime(new MaterialData(Material.CONCRETE_POWDER, (byte) 2), 90 * tickModifier);//Magenta concrete powder
        addMeltTime(new MaterialData(Material.CONCRETE_POWDER, (byte) 3), 90 * tickModifier);//Light blue concrete powder
        addMeltTime(new MaterialData(Material.CONCRETE_POWDER, (byte) 4), 90 * tickModifier);//Yellow concrete powder
        addMeltTime(new MaterialData(Material.CONCRETE_POWDER, (byte) 5), 90 * tickModifier);//Lime concrete powder
        addMeltTime(new MaterialData(Material.CONCRETE_POWDER, (byte) 6), 90 * tickModifier);//Pink concrete powder
        addMeltTime(new MaterialData(Material.CONCRETE_POWDER, (byte) 7), 90 * tickModifier);//Gray concrete powder
        addMeltTime(new MaterialData(Material.CONCRETE_POWDER, (byte) 8), 90 * tickModifier);//Light gray concrete powder
        addMeltTime(new MaterialData(Material.CONCRETE_POWDER, (byte) 9), 90 * tickModifier);//Cyan concrete powder
        addMeltTime(new MaterialData(Material.CONCRETE_POWDER, (byte) 10), 90 * tickModifier);//Purple concrete powder
        addMeltTime(new MaterialData(Material.CONCRETE_POWDER, (byte) 11), 90 * tickModifier);//Blue concrete powder
        addMeltTime(new MaterialData(Material.CONCRETE_POWDER, (byte) 12), 90 * tickModifier);//Brown concrete powder
        addMeltTime(new MaterialData(Material.CONCRETE_POWDER, (byte) 13), 90 * tickModifier);//Green concrete powder
        addMeltTime(new MaterialData(Material.CONCRETE_POWDER, (byte) 14), 90 * tickModifier);//Red concrete powder
        addMeltTime(new MaterialData(Material.CONCRETE_POWDER, (byte) 15), 90 * tickModifier);//Black concrete powder

        //Advanced blocks
        addMeltTime(new MaterialData(Material.STEP, (byte) 0), 120 * tickModifier);//Stone slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 1), 120 * tickModifier);//Sandstone slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 2), 120 * tickModifier);//Sandstone slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 3), 120 * tickModifier);//Cobble slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 4), 120 * tickModifier);//Brick slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 5), 120 * tickModifier);//Stone brick slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 7), 120 * tickModifier);//Quartz slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 8), 120 * tickModifier);//Upper Stone slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 9), 120 * tickModifier);//Upper Sandstone slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 11), 120 * tickModifier);//Upper Cobble slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 12), 120 * tickModifier);//Upper Brick slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 13), 120 * tickModifier);//Upper Stone brick slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 15), 120 * tickModifier);//Upper Quartz slab
        addMeltTime(new MaterialData(Material.STONE_SLAB2, (byte) 0), 120 * tickModifier);//Red sandstone slab
        addMeltTime(new MaterialData(Material.STONE_SLAB2, (byte) 8), 120 * tickModifier);//Upper red sandstone slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 0), 120 * tickModifier);//Double Stone slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 1), 120 * tickModifier);//Double Sandstone slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 3), 120 * tickModifier);//Double Cobble slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 4), 120 * tickModifier);//Double Brick slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 5), 120 * tickModifier);//Double Stone brick slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 7), 120 * tickModifier);//Double Quartz slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 8), 120 * tickModifier);//Smooth Double Stone slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 9), 120 * tickModifier);//Smooth Double Sandstone slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 15), 120 * tickModifier);//Smooth Double Quartz slab
        addMeltTime(new MaterialData(Material.DOUBLE_STONE_SLAB2, (byte) 0), 120 * tickModifier);//Double red sandstone slab
        addMeltTime(new MaterialData(Material.DOUBLE_STONE_SLAB2, (byte) 8), 120 * tickModifier);//Smooth double red sandstone slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 0), 30 * tickModifier);//Oak slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 1), 30 * tickModifier);//Spruce slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 2), 30 * tickModifier);//Birch slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 3), 30 * tickModifier);//Jungle slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 4), 30 * tickModifier);//Acacia slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 5), 30 * tickModifier);//Dark oak slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 8), 30 * tickModifier);//Upper Oak slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 9), 30 * tickModifier);//Upper Spruce slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 10), 30 * tickModifier);//Upper Birch slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 11), 30 * tickModifier);//Upper Jungle slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 12), 30 * tickModifier);//Upper Acacia slab
        addMeltTime(new MaterialData(Material.WOOD_STEP, (byte) 13), 30 * tickModifier);//Upper Dark oak slab
        addMeltTime(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 0), 30 * tickModifier);//Double Oak slab
        addMeltTime(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 1), 30 * tickModifier);//Double Spruce slab
        addMeltTime(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 2), 30 * tickModifier);//Double Birch slab
        addMeltTime(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 3), 30 * tickModifier);//Double Jungle slab
        addMeltTime(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 4), 30 * tickModifier);//Double Acacia slab
        addMeltTime(new MaterialData(Material.WOOD_DOUBLE_STEP, (byte) 5), 30 * tickModifier);//Double Dark oak slab
        addMeltTime(new MaterialData(Material.MOSSY_COBBLESTONE), 150 * tickModifier);
        addMeltTime(new MaterialData(Material.SMOOTH_BRICK, (byte) 2), 120 * tickModifier);//Cracked stone brick
        addMeltTime(new MaterialData(Material.GLASS), 90 * tickModifier);
        addMeltTime(new MaterialData(Material.CONCRETE, (byte) 0), 120 * tickModifier);//White concrete
        addMeltTime(new MaterialData(Material.CONCRETE, (byte) 1), 120 * tickModifier);//Orange concrete
        addMeltTime(new MaterialData(Material.CONCRETE, (byte) 2), 120 * tickModifier);//Magenta concrete
        addMeltTime(new MaterialData(Material.CONCRETE, (byte) 3), 120 * tickModifier);//Light blue concrete
        addMeltTime(new MaterialData(Material.CONCRETE, (byte) 4), 120 * tickModifier);//Yellow concrete
        addMeltTime(new MaterialData(Material.CONCRETE, (byte) 5), 120 * tickModifier);//Lime concrete
        addMeltTime(new MaterialData(Material.CONCRETE, (byte) 6), 120 * tickModifier);//Pink concrete
        addMeltTime(new MaterialData(Material.CONCRETE, (byte) 7), 120 * tickModifier);//Gray concrete
        addMeltTime(new MaterialData(Material.CONCRETE, (byte) 8), 120 * tickModifier);//Light gray concrete
        addMeltTime(new MaterialData(Material.CONCRETE, (byte) 9), 120 * tickModifier);//Cyan concrete
        addMeltTime(new MaterialData(Material.CONCRETE, (byte) 10), 120 * tickModifier);//Purple concrete
        addMeltTime(new MaterialData(Material.CONCRETE, (byte) 11), 120 * tickModifier);//Blue concrete
        addMeltTime(new MaterialData(Material.CONCRETE, (byte) 12), 120 * tickModifier);//Brown concrete
        addMeltTime(new MaterialData(Material.CONCRETE, (byte) 13), 120 * tickModifier);//Green concrete
        addMeltTime(new MaterialData(Material.CONCRETE, (byte) 14), 120 * tickModifier);//Red concrete
        addMeltTime(new MaterialData(Material.CONCRETE, (byte) 15), 120 * tickModifier);//Black concrete

        //Survivor blocks
        addMeltTime(new MaterialData(Material.PACKED_ICE), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.BRICK), 180 * tickModifier);
        addMeltTime(new MaterialData(Material.SMOOTH_BRICK, (byte) 0), 180 * tickModifier);//Stone brick
        addMeltTime(new MaterialData(Material.THIN_GLASS), 90 * tickModifier);
        addMeltTime(new MaterialData(Material.IRON_FENCE), 150 * tickModifier);//Iron bars
        addMeltTime(new MaterialData(Material.IRON_BLOCK), 180 * tickModifier);
        addMeltTime(new MaterialData(Material.LADDER), 10 * tickModifier);
        addMeltTime(new MaterialData(Material.TRAP_DOOR), 10 * tickModifier);
        addMeltTime(new MaterialData(Material.ICE), 10 * tickModifier);
        addMeltTime(new MaterialData(Material.QUARTZ_BLOCK, (byte) 0), 150 * tickModifier);
        addMeltTime(new MaterialData(Material.QUARTZ_BLOCK, (byte) 1), 150 * tickModifier);
        addMeltTime(new MaterialData(Material.QUARTZ_BLOCK, (byte) 2), 150 * tickModifier);
        addMeltTime(new MaterialData(Material.NETHER_WART_BLOCK), 150 * tickModifier);
        addMeltTime(new MaterialData(Material.WOOL, (byte) 0), 20 * tickModifier);//White wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 1), 20 * tickModifier);//Orange wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 2), 20 * tickModifier);//Magenta wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 3), 20 * tickModifier);//Light blue wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 4), 20 * tickModifier);//Yellow wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 5), 20 * tickModifier);//Lime wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 6), 20 * tickModifier);//Pink wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 7), 20 * tickModifier);//Gray wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 8), 20 * tickModifier);//Light gray wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 9), 20 * tickModifier);//Cyan wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 10), 20 * tickModifier);//Purple wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 11), 20 * tickModifier);//Blue wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 12), 20 * tickModifier);//Brown wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 13), 20 * tickModifier);//Green wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 14), 20 * tickModifier);//Red wool
        addMeltTime(new MaterialData(Material.WOOL, (byte) 15), 20 * tickModifier);//Black wool
        addMeltTime(new MaterialData(Material.WHITE_GLAZED_TERRACOTTA), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.ORANGE_GLAZED_TERRACOTTA), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.MAGENTA_GLAZED_TERRACOTTA), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.LIGHT_BLUE_GLAZED_TERRACOTTA), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.YELLOW_GLAZED_TERRACOTTA), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.LIME_GLAZED_TERRACOTTA), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.PINK_GLAZED_TERRACOTTA), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.GRAY_GLAZED_TERRACOTTA), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.SILVER_GLAZED_TERRACOTTA), 120 * tickModifier);//Light gray glazed terracotta
        addMeltTime(new MaterialData(Material.CYAN_GLAZED_TERRACOTTA), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.PURPLE_GLAZED_TERRACOTTA), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.BLUE_GLAZED_TERRACOTTA), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.BROWN_GLAZED_TERRACOTTA), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.GREEN_GLAZED_TERRACOTTA), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.RED_GLAZED_TERRACOTTA), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.BLACK_GLAZED_TERRACOTTA), 120 * tickModifier);

        //Trusted blocks
        addMeltTime(new MaterialData(Material.WOOD_DOOR), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.WOODEN_DOOR), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.FENCE), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.WOOD_STAIRS), 30 * tickModifier);//Oak stairs
        addMeltTime(new MaterialData(Material.COBBLESTONE_STAIRS), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.BRICK_STAIRS), 180 * tickModifier);
        addMeltTime(new MaterialData(Material.SMOOTH_STAIRS), 180 * tickModifier);//Stone brick stairs
        addMeltTime(new MaterialData(Material.SANDSTONE_STAIRS), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.RED_SANDSTONE_STAIRS), 120 * tickModifier);
        addMeltTime(new MaterialData(Material.SPRUCE_WOOD_STAIRS), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.BIRCH_WOOD_STAIRS), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.JUNGLE_WOOD_STAIRS), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.QUARTZ_STAIRS), 150 * tickModifier);
        addMeltTime(new MaterialData(Material.ACACIA_STAIRS), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.DARK_OAK_STAIRS), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.COBBLE_WALL, (byte) 0), 120 * tickModifier);//Cobblestone wall
        addMeltTime(new MaterialData(Material.COBBLE_WALL, (byte) 1), 150 * tickModifier);//Mossy cobblestone wall
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 0), 90 * tickModifier);//White glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 1), 90 * tickModifier);//Orange glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 2), 90 * tickModifier);//Magenta glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 3), 90 * tickModifier);//Light blue glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 4), 90 * tickModifier);//Yellow glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 5), 90 * tickModifier);//Lime glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 6), 90 * tickModifier);//Pink glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 7), 90 * tickModifier);//Gray glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 8), 90 * tickModifier);//Light gray glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 9), 90 * tickModifier);//Cyan glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 10), 90 * tickModifier);//Purple glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 11), 90 * tickModifier);//Blue glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 12), 90 * tickModifier);//Brown glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 13), 90 * tickModifier);//Green glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 14), 90 * tickModifier);//Red glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS, (byte) 15), 90 * tickModifier);//Black glass
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 0), 90 * tickModifier);//White glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 1), 90 * tickModifier);//Orange glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 2), 90 * tickModifier);//Magenta glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 3), 90 * tickModifier);//Light blue glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 4), 90 * tickModifier);//Yellow glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 5), 90 * tickModifier);//Lime glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 6), 90 * tickModifier);//Pink glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), 90 * tickModifier);//Gray glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 8), 90 * tickModifier);//Light gray glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 9), 90 * tickModifier);//Cyan glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 10), 90 * tickModifier);//Purple glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 11), 90 * tickModifier);//Blue glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 12), 90 * tickModifier);//Brown glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 13), 90 * tickModifier);//Green glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 14), 90 * tickModifier);//Red glass pane
        addMeltTime(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 15), 90 * tickModifier);//Black glass pane
        addMeltTime(new MaterialData(Material.PRISMARINE, (byte) 0), 120 * tickModifier);//Prismarine
        addMeltTime(new MaterialData(Material.PRISMARINE, (byte) 1), 120 * tickModifier);//Prismarine Bricks
        addMeltTime(new MaterialData(Material.PRISMARINE, (byte) 2), 120 * tickModifier);//Dark Prismarine

        //Elder blocks
        addMeltTime(new MaterialData(Material.GLOWSTONE), 225 * tickModifier);
        addMeltTime(new MaterialData(Material.NETHERRACK), 270 * tickModifier);
        addMeltTime(new MaterialData(Material.MAGMA), 270 * tickModifier);//Magma block
        addMeltTime(new MaterialData(Material.NETHER_BRICK), 240 * tickModifier);
        addMeltTime(new MaterialData(Material.RED_NETHER_BRICK), 240 * tickModifier);
        addMeltTime(new MaterialData(Material.NETHER_FENCE), 240 * tickModifier);
        addMeltTime(new MaterialData(Material.STEP, (byte) 6), 240 * tickModifier);//Nether brick slab
        addMeltTime(new MaterialData(Material.STEP, (byte) 14), 240 * tickModifier);//Upper Nether brick slab
        addMeltTime(new MaterialData(Material.DOUBLE_STEP, (byte) 6), 240 * tickModifier);//Double Nether brick slab
        addMeltTime(new MaterialData(Material.NETHER_BRICK_STAIRS), 240 * tickModifier);
        addMeltTime(new MaterialData(Material.PURPUR_BLOCK), 240 * tickModifier);
        addMeltTime(new MaterialData(Material.PURPUR_PILLAR), 240 * tickModifier);
        addMeltTime(new MaterialData(Material.PURPUR_STAIRS), 240 * tickModifier);
        addMeltTime(new MaterialData(Material.PURPUR_SLAB), 240 * tickModifier);
        addMeltTime(new MaterialData(Material.PURPUR_DOUBLE_SLAB), 240 * tickModifier);
        addMeltTime(new MaterialData(Material.ENDER_STONE), 270 * tickModifier);
        addMeltTime(new MaterialData(Material.END_BRICKS), 270 * tickModifier);
        addMeltTime(new MaterialData(Material.END_ROD), 180 * tickModifier);

        //Donor blocks Instant melt)
        addMeltTime(new MaterialData(Material.WOOD, (byte) 1), 30 * tickModifier);//Spruce planks
        addMeltTime(new MaterialData(Material.WOOD, (byte) 2), 30 * tickModifier);//Birch planks
        addMeltTime(new MaterialData(Material.WOOD, (byte) 3), 30 * tickModifier);//Jungle planks
        addMeltTime(new MaterialData(Material.WOOD, (byte) 4), 30 * tickModifier);//Acacia planks
        addMeltTime(new MaterialData(Material.WOOD, (byte) 5), 30 * tickModifier);//Dark oak planks
        addMeltTime(new MaterialData(Material.SAND, (byte) 1), 90 * tickModifier);//Red sand
        addMeltTime(new MaterialData(Material.SPRUCE_FENCE), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.BIRCH_FENCE), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.JUNGLE_FENCE), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.ACACIA_FENCE), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.DARK_OAK_FENCE), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.FENCE_GATE), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.SPRUCE_FENCE_GATE), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.BIRCH_FENCE_GATE), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.JUNGLE_FENCE_GATE), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.ACACIA_FENCE_GATE), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.DARK_OAK_FENCE_GATE), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.SPRUCE_DOOR), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.SPRUCE_DOOR_ITEM), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.BIRCH_DOOR), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.BIRCH_DOOR_ITEM), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.JUNGLE_DOOR), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.JUNGLE_DOOR_ITEM), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.ACACIA_DOOR), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.ACACIA_DOOR_ITEM), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.DARK_OAK_DOOR), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.DARK_OAK_DOOR_ITEM), 30 * tickModifier);
        addMeltTime(new MaterialData(Material.SEA_LANTERN), 0);
        addMeltTime(new MaterialData(Material.CARPET, (byte) 0), 20 * tickModifier);//White carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 1), 20 * tickModifier);//Orange carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 2), 20 * tickModifier);//Magenta carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 3), 20 * tickModifier);//Light blue carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 4), 20 * tickModifier);//Yellow carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 5), 20 * tickModifier);//Lime carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 6), 20 * tickModifier);//Pink carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 7), 20 * tickModifier);//Gray carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 8), 20 * tickModifier);//Light gray carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 9), 20 * tickModifier);//Cyan carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 10), 20 * tickModifier);//Purple carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 11), 20 * tickModifier);//Blue carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 12), 20 * tickModifier);//Brown carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 13), 20 * tickModifier);//Green carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 14), 20 * tickModifier);//Red carpet
        addMeltTime(new MaterialData(Material.CARPET, (byte) 15), 20 * tickModifier);//Black carpet
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
                addMeltTime(new MaterialData(m), 30 * tickModifier);//Checks in itself if either already contain it
    }

    public static String getLavaMeltTimeAsString(MaterialData data) {
        int seconds = ticksToMelt.getOrDefault(data, (short) 0);
        if (seconds < 0)
            return "Never";
        seconds = seconds / tickModifier;
        if (seconds == 0)
            return "Immediately";
        return seconds + " Second" + (seconds == 1 ? "" : "s");
    }

    public static String getLavaMeltRangeTimeAsString(MaterialData data) {
        int seconds = ticksToMelt.getOrDefault(data, (short) 0);
        if (seconds < 0)
            return "Never";
        seconds = seconds / tickModifier;
        if (seconds == 0)
            return "Immediately";
        int range = (int) (seconds * percent);
        int min = seconds - range, max = seconds + range;
        return (min == max ? max : min + " to " + max) + " Second" + (max == 1 ? "" : "s");
    }

    private static void addMeltTime(MaterialData data, int time) {
        if (!ticksToMelt.containsKey(data))
            ticksToMelt.put(data, (short) time);
    }

    public static short getMeltTime(MaterialData type) {
        //Can use Air or null to remove it
        if (!ticksToMelt.containsKey(type))
            type = new MaterialData(type.getItemType());
        return ticksToMelt.getOrDefault(type, (short) 0);
    }

    public static long convert(int x, int y, int z) {
        //TODO check if x, or z is greater than 30 million and if y > 256
        return (long) y << 52 | (long) (x + SHIFT) << 26 | (z + SHIFT);
    }

    @SuppressWarnings("unchecked")
    private void loadMeltMap(String worldName) {
        //TODO: Dynamic physics start with few chunks that lava spawns in... then load neighboring chunks dynamically
        //Start load should be near instant 3072 blocks max for 3 spawns
        //TODO how to check playerPlaced vs natural blocks (keep track of player placed?)
        File fileMeltMap = new File("plugins/ClassicPhysics", worldName + ".txt");
        if (fileMeltMap.exists()) {
            HashMap<Short, ArrayList<Long>> compressedMeltMap = new HashMap<>();
            try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(fileMeltMap))) {
                compressedMeltMap = (HashMap<Short, ArrayList<Long>>) is.readObject();
            } catch (IOException | ClassNotFoundException ignored) {
            }
            for (Map.Entry<Short, ArrayList<Long>> cMelt : compressedMeltMap.entrySet()) {
                for (Long l : cMelt.getValue()) {
                    short s = cMelt.getKey();
                    short bonus = (short) RANDOM.nextInt((short) (s * percent + 0.5) + 1);
                    if (RANDOM.nextBoolean())
                        s += bonus;
                    else
                        s -= bonus;
                    if (s == 0)//If it somehow ended up as 0 just don't add it
                        continue;
                    if (s > 1)
                        s *= multiplier;
                    meltMap.put(l, s);
                }
            }
        }
    }

    public boolean isClassicBlock(Location location) {
        return isClassicBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    private boolean isClassicBlock(int x, int y, int z) {
        return isClassicBlock(convert(x, y, z));
    }

    private boolean isClassicBlock(long l) {
        Short lo = meltMap.get(l);
        return lo != null && lo < -1;//All numbers less than -1 are classic blocks of some sort
    }

    void addMeltTimer(int x, int y, int z, MaterialData type) {
        //TODO remove from active if it is not air. Shouldn't it just remove from active all together?
        long l = convert(x, y, z);
        short time = getMeltTime(type);
        long bonus = RANDOM.nextInt((short) (time * percent + 0.5) + 1);
        if (RANDOM.nextBoolean())
            time += bonus;
        else
            time -= bonus;

        if (time == 0)
            meltMap.remove(l);
        else
            meltMap.put(l, time);
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

    public void setRangePercent(double rangePercent) {
        percent = rangePercent;
    }

    public void setMeltMultiplier(double meltMultiplier) {
        multiplier = meltMultiplier;
    }

    public void start(String worldName) {
        meltMap = new HashMap<>();
        activeBlocks = new ArrayList<>();
        meltTimers = new HashMap<>();
        blockedMap = new HashMap<>();
        spongeTimers = new HashMap<>();
        sponges = new HashMap<>();
        w = Bukkit.getWorld(worldName);
        tickCount = 0;
        loadMeltMap(world = worldName);
        queue = FaweAPI.createQueue(worldName, true);
        //Bukkit.broadcastMessage("[DEBUG]: " + worldName + " loaded.");//TODO add a debug thing for people to listen to
        if (stopped)
            stopped = false;
        else
            ticker.runTaskTimerAsynchronously(ClassicPhysics.INSTANCE, 0, 20 / tickModifier);
    }

    public void end() {
        stopped = true;
        running = false;
        if (queue != null)
            queue.clear();
    }

    private void tick() {
        tickCount++;

        if (!meltTimers.isEmpty()) {
            ArrayList<MeltLocationInfo> melts = meltTimers.get(tickCount);
            if (melts != null)
                for (MeltLocationInfo melt : melts)
                    if (isClassicBlock(melt.from)) {
                        Short m = meltMap.get(melt.loc);
                        if (m == null || (m == melt.ticksToMelt && m >= 0)) //Don't try placing if it already is there
                            placeAt(melt.loc);
                    }
            meltTimers.remove(tickCount);
        }

        if (running)
            return; //TODO if not running do we want to get a head start in case we were behind?
        running = true;
        List<Long> lastActive = activeBlocks;
        activeBlocks = new ArrayList<>();
        if (lastActive != null)
            for (long l : lastActive) {//TODO try taking only some at a time
                attemptFlow(l, l + X);
                attemptFlow(l, l - X);
                attemptFlow(l, l + Z);
                attemptFlow(l, l - Z);
                attemptFlow(l, l - Y);
            }
        queue.flush();
        queue.clear();//Clears the queue which makes it less laggy in the future
        running = false;
    }

    private void attemptFlow(long from, long to) {
        if (getY(to) < 0)//Disable physics below world
            return;
        Short time = meltMap.get(to);
        if (time == null || time == 0) //Instant melt
            placeAt(to);
            //else if (time < 0)//Not in map probably should just stop it or Ummeltable
        else if (time > 0 && !blockedMap.containsKey(to))//if not blocked start melting
            startMelt(from, to, time);
    }

    private void placeAt(int x, int y, int z) {
        placeAt(convert(x, y, z));
    }

    private void placeAt(long to) {
        if (blockedMap.containsKey(to))
            return;//Cancel if it is blocked (could happen from a melt or order issue)
        meltMap.put(to, (short) -2);
        activeBlocks.add(to);
        queue.setBlock(getX(to), getY(to), getZ(to), 11);//LAVA
    }

    private void startMelt(long from, long to, int time) {
        ArrayList<MeltLocationInfo> melts = meltTimers.computeIfAbsent(tickCount + time, k -> new ArrayList<>());
        melts.add(new MeltLocationInfo(to, from, time));
    }

    public void placeSponge(int xLoc, int yLoc, int zLoc, int range) {
        ArrayList<Long> locations = new ArrayList<>(), outerLocations = new ArrayList<>();
        range++;//add the outside rim
        long loc = convert(xLoc, yLoc, zLoc);
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    long blockedLocation = loc + x * X + y * Y + z * Z;
                    if (Math.abs(x) == range || Math.abs(y) == range || Math.abs(z) == range)
                        outerLocations.add(blockedLocation); //Make the x,y,z be ints
                    else {
                        addBlockedLocation(blockedLocation, loc != blockedLocation);
                        locations.add(blockedLocation);
                    }
                }
            }
        }
        int spongeDuration = range == 5 ? 15 * tickModifier : 25 * tickModifier;
        ArrayList<Long> spngs = spongeTimers.computeIfAbsent(tickCount + spongeDuration, k -> new ArrayList<>());
        spngs.add(loc);
        SpongeInfo info;
        sponges.put(loc, info = new SpongeInfo(locations, outerLocations));
        info.curParticle = spawnParticleEffect(loc, spongeDuration);
    }

    private void addBlockedLocation(long loc, boolean clearIfClassic) {
        blockedMap.merge(loc, (short) 1, (a, b) -> (short) (a + b));
        if (clearIfClassic && isClassicBlock(loc)) {
            setAirNoPhysics(loc);
            //TODO remove from active blocks?
        }
    }

    private void setAirNoPhysics(long l) {
        queue.setBlock(getX(l), getY(l), getZ(l), 0);//AIR
        meltMap.remove(l);
    }

    public void removeSponge(int x, int y, int z) {
        removeSponge(convert(x, y, z), false);
    }

    private void removeSponge(long loc, boolean expired) {
        SpongeInfo sponge = sponges.get(loc);
        if (sponge == null)
            return;
        sponges.remove(loc);
        sponge.curParticle.remove();//If broken early remove the particle
        //Remove all blocked locations
        for (long location : sponge.blockingLocations) {
            Short count = blockedMap.get(location);
            if (count == null)
                continue;
            if (count == 1)
                blockedMap.remove(location);
            else
                blockedMap.put(location, (short) (count - 1));
        }
        sponge.blockingLocations.clear();
        sponge.outerLocations.stream().filter(this::isClassicBlock).forEach(l -> activeBlocks.add(l));
        if (expired) //If it was removed it doesn't need to be set as air again
            setAirNoPhysics(loc);
    }

    private void spongeTick() {
        if (!spongeTimers.isEmpty()) {
            ArrayList<Long> expiring = spongeTimers.get(tickCount);
            if (expiring != null)
                for (Long sponge : expiring)
                    removeSponge(sponge, true);
            spongeTimers.remove(tickCount);
            ArrayList<Long> warning = spongeTimers.get(tickCount + 15);
            if (warning != null)
                for (Long loc : warning) {
                    SpongeInfo sponge = sponges.get(loc);
                    if (sponge == null)
                        continue;
                    sponge.curParticle.setColor(warn);
                }
            ArrayList<Long> lastWarning = spongeTimers.get(tickCount + 5);
            if (lastWarning != null)
                for (Long loc : lastWarning) {
                    SpongeInfo sponge = sponges.get(loc);
                    if (sponge == null)
                        continue;
                    sponge.curParticle.setColor(Color.RED);
                }
        }
    }

    private Location fromLong(long l) {
        return w == null ? null : new Location(w, getX(l), getY(l), getZ(l));
    }

    private AreaEffectCloud spawnParticleEffect(long loc, int ticks) {
        Location location = fromLong(loc);
        if (location == null)
            return null;
        location.add(0.5, 0.5, 0.5);
        AreaEffectCloud e = (AreaEffectCloud) w.spawnEntity(location, EntityType.AREA_EFFECT_CLOUD);
        e.setColor(Color.LIME);
        e.setRadius((float) 0.75);
        e.setDuration(ticks);
        return e;
    }

    private class MeltLocationInfo {
        private final int ticksToMelt;
        private final long loc;
        private final long from;

        MeltLocationInfo(long loc, long from, int ticksToMelt) {
            this.loc = loc;
            this.from = from;
            this.ticksToMelt = ticksToMelt;
        }
    }

    public class SpongeInfo {
        private List<Long> blockingLocations;
        private List<Long> outerLocations;
        private AreaEffectCloud curParticle;

        SpongeInfo(List<Long> blocked, List<Long> outerLocations) {
            this.blockingLocations = blocked;
            this.outerLocations = outerLocations;
        }
    }

    public void placeClassicBlock(Location location) {
        placeAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public void placeBlock(int x, int y, int z, Material type, byte data) {
        FaweQueue q = FaweAPI.createQueue(world, true);
        q.setBlock(x, y, z, type.getId(), data);
        q.flush();
        addMeltTimer(x, y, z, new MaterialData(type, data));
    }

    private List<MaterialData> fallingTypes;

    public void addFallingTypes(List<MaterialData> types) {
        this.fallingTypes = types;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block b = event.getBlock();
        addMeltTimer(b.getX(), b.getY(), b.getZ(), new MaterialData(b.getType(), b.getData()));
        if (b.getType().toString().contains("DOOR") && b.getRelative(BlockFace.UP).getType().equals(b.getType()))
            addMeltTimer(b.getX(), b.getY() + 1, b.getZ(), new MaterialData(b.getType(), b.getData()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        addMeltTimer(b.getX(), b.getY(), b.getZ(), new MaterialData(Material.AIR));
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void blockFall(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock) {
            FallingBlock f = (FallingBlock) event.getEntity();
            event.setCancelled(true);
            if (!f.isGlowing())
                event.getBlock().getState().update(true, false);
            else {
                String uid = f.getUniqueId().toString();
                f = f.getWorld().spawnFallingBlock(f.getLocation(), new MaterialData(f.getMaterial(), f.getBlockData()));
                f.setGlowing(true);
                f.setGravity(false);
                ((CraftFallingBlock) f).getHandle().ticksLived = -2147483648; //Bypass the spigot check of it being negative
                Team t = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Special");
                if (t != null) {
                    t.removeEntry(uid);
                    t.addEntry(f.getUniqueId().toString());
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void itemSpawn(ItemSpawnEvent event) {
        org.bukkit.entity.Item i = event.getEntity();
        MaterialData data = i.getItemStack().getData();
        if (data.getItemType().equals(Material.DROPPER))
            data = new MaterialData(Material.DROPPER, (byte) 1);
        if (i.getTicksLived() == 0 && this.fallingTypes.contains(data)) {
            FallingBlock f = i.getWorld().spawnFallingBlock(i.getLocation(), data);
            f.setGlowing(true);
            f.setGravity(false);
            ((CraftFallingBlock) f).getHandle().ticksLived = -2147483648; //Bypass the spigot check of it being negative
            Team t = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Special");
            if (t != null)
                t.addEntry(f.getUniqueId().toString());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPhysicsUpdate(BlockPhysicsEvent event) {
        if (!event.getBlock().getType().toString().contains("DOOR") || !event.getChangedType().toString().contains("PLATE") || (event.getBlock().getType().toString().contains("DOOR") &&
                event.getChangedType().toString().contains("PLATE") && !event.getBlock().getType().equals(event.getBlock().getRelative(BlockFace.UP).getType())))
            event.setCancelled(true);
    }

    @EventHandler
    public void onLeafDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onIceMelt(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onCactusGrow(BlockGrowEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void fireSpread(BlockIgniteEvent event) {
        event.setCancelled(true);
    }
}