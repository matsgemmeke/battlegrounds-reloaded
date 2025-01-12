package nl.matsgemmeke.battlegrounds.game.damage;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DamageEvent {

    @NotNull
    private DamageType type;
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
            @NotNull DamageType type,
            double damage
    ) {
        this.damager = damager;
        this.entity = entity;
        this.type = type;
        this.damagerContext = damagerContext;
        this.entityContext = entityContext;
        this.damage = damage;
    }

    public double getDamage() {
        return damage;
    }

    @NotNull
    public DamageType getDamageType() {
        return type;
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
