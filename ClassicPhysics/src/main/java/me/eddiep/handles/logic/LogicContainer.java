package me.eddiep.handles.logic;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public interface LogicContainer {

    /**
     * Request a block to be queued for the next logic check
     * @param block The block to queue
     */
    void queueBlock(Block block);

    /**
     * Run a logic tick on all blocks in this {@link LogicContainer}
     */
    void tick();

    /**
     * How often a tick should occur, in minecraft ticks.
     * @return How often the {@link LogicContainer#tick()} method should be called, in minecraft ticks
     */
    int updateRate();

    /**
     * Check if a {@link Material} of a given type can be queued in this {@link LogicContainer}
     * @param material The {@link Material} to check
     * @return True if this {@link Material} can be queued, otherwise false
     */
    boolean doesHandle(Material material);

    void blockUpdate(Location location);
}
