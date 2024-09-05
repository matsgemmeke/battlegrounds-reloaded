package nl.matsgemmeke.battlegrounds.game.damage;

import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;

public enum DamageCause {

    /**
     * Explosion damage caused by a normal world explosion (TNT, creeper, custom explosion etc.)
     */
    DEFAULT_EXPLOSION,
    /**
     * Damage caused by an entity physically attacking another
     */
    ENTITY_ATTACK,
    /**
     * Damage caused by direct impact from a gun projectile
     */
    GUN_PROJECTILE,
    /**
     * Explosion damage caused by an explosion originating from an item inside a game.
     */
    ITEM_EXPLOSION;

    @Nullable
    public static DamageCause map(@NotNull EntityDamageEvent.DamageCause cause) {
        switch (cause) {
            case ENTITY_ATTACK -> {
                return ENTITY_ATTACK;
            }
            case ENTITY_EXPLOSION -> {
                return DEFAULT_EXPLOSION;
            }
            default -> {
                return null;
            }
        }
    }
}
