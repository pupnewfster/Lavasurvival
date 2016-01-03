package me.eddiep.minecraft.ls.game.status;

import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

public class PlayerStatus {
    private WeakReference<Player> owner;
    private boolean isInvincible;
    private long invincibleStart, invincibleDuration;

    public PlayerStatus(Player owner) {
        this.owner = new WeakReference<Player>(owner);
    }

    public void tick() {
        Player p = owner.get();
        if (p == null)
            return;

        if (isInvincible) {
            long dur = System.currentTimeMillis() - invincibleStart;
            if (dur < invincibleDuration) {
                float percent = ((float)dur / (float)invincibleDuration);
                p.setExp(percent);
            } else {
                isInvincible = false;
                invincibleStart = 0;
                invincibleDuration = 0;
            }
        }
    }

    public void makeInvincible(long duration) {
        invincibleDuration = duration;
        invincibleStart = System.currentTimeMillis();
        isInvincible = true;
    }

    public boolean isInvincible() {
        return isInvincible;
    }
}
