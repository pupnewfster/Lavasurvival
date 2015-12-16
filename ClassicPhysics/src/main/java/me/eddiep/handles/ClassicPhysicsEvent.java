package me.eddiep.handles;

import me.eddiep.ClassicPhysics;
import me.eddiep.handles.logic.LogicContainer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClassicPhysicsEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel, isClassicEvent;
    private LogicContainer container;
    private Material newBlock;
    private Location location, from;
    private Block oldBlock;

    public ClassicPhysicsEvent(Block oldBlock, Material newBlock, boolean isClassicEvent, Location location, LogicContainer logicContainer, Location from) {
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
        this.isClassicEvent = isClassicEvent;
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

    public boolean isClassicEvent() {
        return this.isClassicEvent;
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