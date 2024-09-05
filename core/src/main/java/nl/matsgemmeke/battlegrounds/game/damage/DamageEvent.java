package nl.matsgemmeke.battlegrounds.game.damage;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class DamageEvent {

    @NotNull
    private DamageCause cause;
    private double damage;
    @NotNull
    private GameEntity damager;
    @NotNull
    private GameEntity entity;

    public DamageEvent(@NotNull GameEntity damager, @NotNull GameEntity entity, @NotNull DamageCause cause, double damage) {
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
    public GameEntity getDamager() {
        return damager;
    }

    @NotNull
    public GameEntity getEntity() {
        return entity;
    }
}
