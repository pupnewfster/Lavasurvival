package me.eddiep.handles;

import org.bukkit.Material;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockPhysicsEvent;

public class ClassicPhysicsEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private BlockPhysicsEvent originalEvent;
    private Material newBlock;
    private boolean cancel;

    public ClassicPhysicsEvent(BlockPhysicsEvent event, Material newBlock) {
        this.originalEvent = event;
        this.newBlock = newBlock;
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

    public BlockPhysicsEvent getOriginalEvent() {
        return originalEvent;
    }

    public HandlerList getHandlers() {
        return handlers;
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
