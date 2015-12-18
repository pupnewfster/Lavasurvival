package me.eddiep.handles;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClassicBlockPlaceEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;
    private Location location;

    public ClassicBlockPlaceEvent(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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