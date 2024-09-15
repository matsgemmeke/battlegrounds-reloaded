package nl.matsgemmeke.battlegrounds.game.damage;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DamageEvent {

    @NotNull
    private DamageCause cause;
    private double damage;
    @NotNull
    private Entity damager;
    @NotNull
    private Entity entity;
    @Nullable
    private GameContext damagerContext;
    @Nullable
    private GameContext entityContext;

    public DamageEvent(
            @NotNull Entity damager,
            @Nullable GameContext damagerContext,
            @NotNull Entity entity,
            @Nullable GameContext entityContext,
            @NotNull DamageCause cause,
            double damage
    ) {
        this.damager = damager;
        this.entity = entity;
        this.cause = cause;
        this.damagerContext = damagerContext;
        this.entityContext = entityContext;
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

    @Nullable
    public GameContext getDamagerContext() {
        return damagerContext;
    }

    @NotNull
    public Entity getEntity() {
        return entity;
    }

    @Nullable
    public GameContext getEntityContext() {
        return entityContext;
    }
}
