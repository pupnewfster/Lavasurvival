package com.crossge.necessities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Teleports {
    private static HashMap<UUID, ArrayList<String>> teleportRequests = new HashMap<UUID, ArrayList<String>>();

    public void removeRequests(UUID uuid) {
        teleportRequests.remove(uuid);
        if (teleportRequests != null)
            for (UUID u : teleportRequests.keySet())
                if (hasRequestFrom(u, uuid))
                    removeRequestFrom(u, uuid);
    }

    public void addRequest(UUID to, String from) {
        if (teleportRequests.containsKey(to)) {
            if (teleportRequests.get(to).contains(from.split(" ")[0] + " toMe"))
                teleportRequests.get(to).remove(from.split(" ")[0] + " toMe");
            else if (teleportRequests.get(to).contains(from.split(" ")[0] + " toThem"))
                teleportRequests.get(to).remove(from.split(" ")[0] + " toThem");
        } else
            teleportRequests.put(to, new ArrayList<String>());
        teleportRequests.get(to).add(from);
    }

    public UUID lastRequest(UUID uuid) {
        if (!teleportRequests.containsKey(uuid))
            return null;
        return UUID.fromString(teleportRequests.get(uuid).get(teleportRequests.get(uuid).size() - 1).split(" ")[0]);
    }

    public String getRequestType(UUID uuid, UUID from) {
        if (!teleportRequests.containsKey(uuid))
            return null;
        if (teleportRequests.get(uuid).contains(from.toString() + " toMe"))
            return "toMe";
        else if (teleportRequests.get(uuid).contains(from.toString() + " toThem"))
            return "toThem";
        return null;
    }

    public boolean hasRequestFrom(UUID uuid, UUID from) {
        return teleportRequests.containsKey(uuid) && (teleportRequests.get(uuid).contains(from.toString() + " toMe") || teleportRequests.get(uuid).contains(from.toString() + " toThem"));
    }

    public void removeRequestFrom(UUID uuid, UUID from) {
        if (teleportRequests.containsKey(uuid)) {
            teleportRequests.get(uuid).remove(from.toString() + " toMe");
            teleportRequests.get(uuid).remove(from.toString() + " toThem");
            if (teleportRequests.get(uuid).isEmpty())
                teleportRequests.remove(uuid);
        }
    }
}