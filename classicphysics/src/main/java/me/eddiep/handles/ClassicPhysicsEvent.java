package me.eddiep.handles;

import me.eddiep.ClassicPhysics;
import me.eddiep.handles.logic.LogicContainer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@SuppressWarnings("unused")
public class ClassicPhysicsEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;
    private final LogicContainer container;
    private Material newBlock;
    private final Location location;
    private final Location from;
    private final Block oldBlock;

    public ClassicPhysicsEvent(Block oldBlock, Material newBlock, Location location, LogicContainer logicContainer, Location from) {
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
        this.location = location;
        this.container = logicContainer;
        this.from = from;
    }

    public LogicContainer getLogicContainer() {
        return this.container;
    }

    public Location getLocation() {
        return this.location;
    }

    public Location getFrom() {
        return this.from;
    }

    public boolean isChanging() {
        return this.newBlock != this.oldBlock.getType();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Material getNewBlock() {
        return this.newBlock;
    }

    public void setNewBlock(Material block) {
        this.newBlock = block;
    }

    public Block getOldBlock() {
        return this.oldBlock;
    }

    public ClassicPhysicsHandler getPhysicsHandler() {
        return ClassicPhysics.INSTANCE.getPhysicsHandler();
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
}