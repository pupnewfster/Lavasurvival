package me.eddiep.handles.logic;


import org.bukkit.Location;
import org.bukkit.Material;

public interface LogicContainer {
    /**
     * Request a block to be queued for the next logic check
     *
     * @param location The location of the block to queue
     */
    void queueBlock(Location location);

    /**
     * Run a logic tick on all blocks in this {@link LogicContainer}
     */
    void tick();

    /**
     * How often a tick should occur, in minecraft ticks.
     *
     * @return How often the {@link LogicContainer#tick()} method should be called, in minecraft ticks
     */
    @SuppressWarnings("SameReturnValue")
    int updateRate();

    /**
     * Check if a {@link Material} of a given type can be queued in this {@link LogicContainer}
     *
     * @param material The {@link Material} to check
     * @return True if this {@link Material} can be queued, otherwise false
     */
    boolean doesHandle(Material material);

    /**
     * Returns what this LogicContainer does physics for
     *
     * @return The Material this LogicContainer does physics for
     */
    Material logicFor();

    /**
     * Unload all block updates
     */
    void unload();
}