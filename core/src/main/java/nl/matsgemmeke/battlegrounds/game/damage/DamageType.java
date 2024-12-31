package nl.matsgemmeke.battlegrounds.game.damage;

import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum DamageType {

    /**
     * Damage caused by an entity physically attacking another
     */
    ATTACK_DAMAGE,
    /**
     * Damage caused by direct impact from a bullet projectile.
     */
    BULLET_DAMAGE,
    /**
     * Explosion damage caused by a normal world explosion (TNT, creeper, custom explosion etc.)
     */
    EXPLOSIVE_DAMAGE,
    /**
     * Explosion damage caused by an explosion originating from an item inside a game.
     */
    EXPLOSIVE_ITEM_DAMAGE;

    @Nullable
    public static DamageType map(@NotNull EntityDamageEvent.DamageCause cause) {
        switch (cause) {
            case ENTITY_ATTACK -> {
                return ATTACK_DAMAGE;
            }
            case ENTITY_EXPLOSION -> {
                return EXPLOSIVE_DAMAGE;
            }
            default -> {
                return null;
            }
        }
    }
}
