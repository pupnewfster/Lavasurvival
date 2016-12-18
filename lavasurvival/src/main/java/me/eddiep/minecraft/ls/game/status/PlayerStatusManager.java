package me.eddiep.minecraft.ls.game.status;

import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

public class PlayerStatusManager {
    private static final ConcurrentHashMap<Player, PlayerStatus> status = new ConcurrentHashMap<>();

    public static void tick() {
        status.keySet().forEach(p -> status.get(p).tick());
    }

    public static void cleanup() {
        status.clear();
    }

    private static PlayerStatus getStatus(Player owner) {
        if (!status.containsKey(owner))
            status.put(owner, new PlayerStatus(owner));
        return status.get(owner);
    }

    static void removePlayer(Player owner) {
        if (owner != null)
            status.remove(owner);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static PlayerStatus makeInvincible(Player owner, int seconds) {
        PlayerStatus status = getStatus(owner);
        status.makeInvincible(seconds * 1000);
        return status;
    }

    public static boolean isInvincible(Player entity) {
        return status.containsKey(entity) && status.get(entity).isInvincible();
    }
}