package nl.matsgemmeke.battlegrounds.game.damage;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DamageEvent {

    @NotNull
    private DamageCause cause;
    private double damage;
    @NotNull
    private Entity damager;
    @NotNull
    private Entity entity;

    public DamageEvent(@NotNull Entity damager, @NotNull Entity entity, @NotNull DamageCause cause, double damage) {
        this.damager = damager;
        this.entity = entity;
        this.cause = cause;
        this.damage = damage;
    }

    @NotNull
    public DamageCause getCause() {
        return cause;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @NotNull
    public Entity getDamager() {
        return damager;
    }

    @NotNull
    public Entity getEntity() {
        return entity;
    }
}
