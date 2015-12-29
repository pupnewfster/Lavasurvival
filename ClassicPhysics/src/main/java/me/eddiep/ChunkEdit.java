package me.eddiep;


import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.Block;
import org.bukkit.Material;

public class ChunkEdit {
    public World world;

    public ChunkEdit(World w) {
        this.world = w;

    }

    public void setBlock(int x, int y, int z, Material type, byte data) {
        int columnX = x >> 4;
        int columnZ = z >> 4;
        int chunkY = y >> 4;
        int blockX = x % 16;
        int blockY = y % 16;
        int blockZ = z % 16;
        if (blockX < 0)
            blockX += 16;
        if (blockX > 15) {
            blockX = blockX % 16;
            columnX++;
        }
        if (blockY < 0)
            blockY += 16;
        if (blockY > 15) {
            blockY = blockY % 16;
            chunkY++;
        }
        if (blockZ < 0)
            blockZ += 16;
        if (blockZ > 15) {
            blockZ = blockZ % 16;
            columnZ++;
        }
        Chunk c = this.world.getChunkAt(columnX, columnZ);
        ChunkSection[] sections = c.getSections();
        ChunkSection section = sections[chunkY];
        if (section == null)
            section = new ChunkSection(chunkY, true);
        NibbleArray blockLight;
        NibbleArray skyLight;
        try {
            blockLight = section.getEmittedLightArray();
        } catch (Exception e) {
            blockLight = new NibbleArray();
        }
        try {
            skyLight = section.getSkyLightArray();
        } catch (Exception e) {
            skyLight = new NibbleArray();
        }
        IBlockData d = Block.getByCombinedId(type.getId() + (data << 12));
        BlockPosition pos = new BlockPosition(x, y, z);
        TileEntity tileEntity = this.world.getTileEntity(pos);
        if (tileEntity != null)
            c.a(new BlockPosition(x, y, z), d);
        else
            section.setType(blockX, blockY, blockZ, d);
        blockLight.a(blockX, blockY, blockZ, getLight(type));
        skyLight.a(blockX, blockY, blockZ, getSkyLight(x, y, z));
        section.a(blockLight);
        section.b(skyLight);
        sections[chunkY] = section;
        c.a(sections);
    }

    public void setBlock(int x, int y, int z, Material type) {
        setBlock(x, y, z, type, (byte) 0);
    }

    private int getSkyLight(int x, int y, int z) {
        org.bukkit.block.Block highest = y > 0 ? this.world.getWorld().getHighestBlockAt(x, z).getRelative(0, -1, 0) : this.world.getWorld().getBlockAt(x, 0, z);
        org.bukkit.block.Block oneUp = y < 256 ? this.world.getWorld().getBlockAt(x, y + 1, z) : highest;
        int sky = oneUp.getLocation().getBlockY() < highest.getLocation().getBlockY() ? highest.getLightFromSky() : oneUp.getLightFromSky();
        //TODO: make it respect transparency
        return sky;
    }

    private int getLight(Material type) {
        switch (type) {
            case BEACON: case ENDER_PORTAL: case FIRE: case GLOWSTONE: case JACK_O_LANTERN: case LAVA: case STATIONARY_LAVA:
            case REDSTONE_LAMP_ON: case SEA_LANTERN:// case WATER: case STATIONARY_WATER:
                return 15;
            case TORCH:// case END_ROD:
                return 14;
            case BURNING_FURNACE:
                return 13;
            case PORTAL:
                return 11;
            case GLOWING_REDSTONE_ORE:
                return 9;
            case ENDER_CHEST: case REDSTONE_TORCH_ON:
                return 7;
            case BREWING_STAND: case BROWN_MUSHROOM: case DRAGON_EGG: case ENDER_PORTAL_FRAME:
                return 1;
            default:
                return 0;
        }
    }
}