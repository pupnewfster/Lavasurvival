package me.eddiep;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClassicBlockPlaceEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;
    private final Location location;

    public ClassicBlockPlaceEvent(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}