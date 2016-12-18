package me.eddiep.minecraft.ls.game.status;

import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;

class PlayerStatus {
    private final WeakReference<Player> owner;
    private boolean isInvincible;
    private long invincibleStart, invincibleDuration;

    PlayerStatus(Player owner) {
        this.owner = new WeakReference<>(owner);
    }

    void tick() {
        Player p = this.owner.get();
        if (p == null)
            return;
        if (this.isInvincible) {
            long dur = System.currentTimeMillis() - this.invincibleStart;
            if (dur < this.invincibleDuration) {
                float percent = ((float) dur / (float) this.invincibleDuration);
                p.setExp(percent);
            } else {
                this.isInvincible = false;
                this.invincibleStart = 0;
                this.invincibleDuration = 0;
                p.setExp(0);
                PlayerStatusManager.removePlayer(p);
            }
        }
    }

    void makeInvincible(long duration) {
        this.invincibleDuration = duration;
        this.invincibleStart = System.currentTimeMillis();
        this.isInvincible = true;
    }

    boolean isInvincible() {
        return this.isInvincible;
    }
}