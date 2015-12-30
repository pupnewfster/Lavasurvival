package me.eddiep;


import net.minecraft.server.v1_8_R3.*;
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
        if (y >= 256)
            return 15;
        else if (y < 0)
            return 0;
        org.bukkit.block.Block highest = this.world.getWorld().getHighestBlockAt(x, z);
        while (highest.getY() > 0 && highest.getType().isTransparent())
            highest = highest.getRelative(0, -1, 0);
        return y <= highest.getY() ? 0 : getDayLight();
    }

    private int getDayLight() {//TODO: Get light more accurately based on time of day and weather
        long time = this.world.getTime() % 24000;
        //sunlight 15
        //sunlight during rain/snow 12
        //sunlight during thunderstorm 10
        //moonlight 4
        if (time > 12000)
            return 4;
        else//if (time > 0)
            return this.world.getWorld().isThundering() ? 10 : 15;
    }

    private int getLight(Material type) {
        switch (type) {
            case BEACON: case ENDER_PORTAL: case FIRE: case GLOWSTONE: case JACK_O_LANTERN: case LAVA: case STATIONARY_LAVA: case REDSTONE_LAMP_ON: case SEA_LANTERN:
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