package me.eddiep.minecraft.ls.game.status;

import org.bukkit.entity.Player;

import java.util.WeakHashMap;

public class PlayerStatusManager {
    private static final WeakHashMap<Player, PlayerStatus> status = new WeakHashMap<>();

    public static void tick() {
        for (Player p : status.keySet()) {
            PlayerStatus s = status.get(p);
            s.tick();
        }
    }

    public static void cleanup() {
        status.clear();
    }

    public static PlayerStatus getStatus(Player owner) {
        if (!status.containsKey(owner)) {
            PlayerStatus s = new PlayerStatus(owner);
            status.put(owner, s);
        }

        return status.get(owner);
    }

    public static PlayerStatus makeInvincible(Player owner, int seconds) {
        PlayerStatus status = getStatus(owner);
        status.makeInvincible(seconds * 1000);
        return status;
    }

    public static boolean isInvincible(Player entity) {
        return status.containsKey(entity) && status.get(entity).isInvincible();
    }
}
