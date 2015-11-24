package me.eddiep.handles;

import me.eddiep.ClassicPhysics;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClassicPhysicsEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Material newBlock;
    private Block oldBlock;
    private boolean cancel;
    private boolean isClassicEvent;
    private Location location;

    public ClassicPhysicsEvent(Block oldBlock, Material newBlock, boolean isClassicEvent, Location location) {
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
        this.isClassicEvent = isClassicEvent;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isChanging() {
        return newBlock != oldBlock.getType();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Material getNewBlock() {
        return newBlock;
    }

    public void setNewBlock(Material block) {
        this.newBlock = block;
    }

    public Block getOldBlock() {
        return oldBlock;
    }

    public ClassicPhysicsHandler getPhysicsHandler() {
        return ClassicPhysics.INSTANCE.getPhysicsHandler();
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public boolean isClassicEvent() {
        return isClassicEvent;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
}