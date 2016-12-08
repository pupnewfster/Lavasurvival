package com.crossge.necessities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Teleports {
    private final HashMap<UUID, ArrayList<String>> teleportRequests = new HashMap<>();

    void removeRequests(UUID uuid) {
        this.teleportRequests.remove(uuid);
        this.teleportRequests.keySet().stream().filter(u -> hasRequestFrom(u, uuid)).forEach(u -> removeRequestFrom(u, uuid));
    }

    public void addRequest(UUID to, String from) {
        if (this.teleportRequests.containsKey(to)) {
            if (this.teleportRequests.get(to).contains(from.split(" ")[0] + " toMe"))
                this.teleportRequests.get(to).remove(from.split(" ")[0] + " toMe");
            else if (this.teleportRequests.get(to).contains(from.split(" ")[0] + " toThem"))
                this.teleportRequests.get(to).remove(from.split(" ")[0] + " toThem");
        } else
            this.teleportRequests.put(to, new ArrayList<>());
        this.teleportRequests.get(to).add(from);
    }

    public UUID lastRequest(UUID uuid) {
        return !this.teleportRequests.containsKey(uuid) ? null : UUID.fromString(this.teleportRequests.get(uuid).get(this.teleportRequests.get(uuid).size() - 1).split(" ")[0]);
    }

    public String getRequestType(UUID uuid, UUID from) {
        if (!this.teleportRequests.containsKey(uuid))
            return null;
        if (this.teleportRequests.get(uuid).contains(from.toString() + " toMe"))
            return "toMe";
        else if (this.teleportRequests.get(uuid).contains(from.toString() + " toThem"))
            return "toThem";
        return null;
    }

    public boolean hasRequestFrom(UUID uuid, UUID from) {
        return this.teleportRequests.containsKey(uuid) && (this.teleportRequests.get(uuid).contains(from.toString() + " toMe") || this.teleportRequests.get(uuid).contains(from.toString() + " toThem"));
    }

    public void removeRequestFrom(UUID uuid, UUID from) {
        if (this.teleportRequests.containsKey(uuid)) {
            this.teleportRequests.get(uuid).remove(from.toString() + " toMe");
            this.teleportRequests.get(uuid).remove(from.toString() + " toThem");
            if (this.teleportRequests.get(uuid).isEmpty())
                this.teleportRequests.remove(uuid);
        }
    }
}