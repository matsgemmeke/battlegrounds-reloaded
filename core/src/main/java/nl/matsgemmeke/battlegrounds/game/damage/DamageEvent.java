package nl.matsgemmeke.battlegrounds.game.damage;

import nl.matsgemmeke.battlegrounds.game.GameKey;
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
    private GameKey damagerGameKey;
    @Nullable
    private GameKey entityGameKey;

    public DamageEvent(
            @NotNull Entity damager,
            @Nullable GameKey damagerGameKey,
            @NotNull Entity entity,
            @Nullable GameKey entityGameKey,
            @NotNull DamageType type,
            double damage
    ) {
        this.damager = damager;
        this.entity = entity;
        this.type = type;
        this.damagerGameKey = damagerGameKey;
        this.entityGameKey = entityGameKey;
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
    public GameKey getDamagerGameKey() {
        return damagerGameKey;
    }

    @NotNull
    public Entity getEntity() {
        return entity;
    }

    @Nullable
    public GameKey getEntityGameKey() {
        return entityGameKey;
    }
}
