package nl.matsgemmeke.battlegrounds.entity;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

/**
 * A game entity able to receive potion effects.
 */
public interface PotionEffectReceiver {

    /**
     * Adds a potion effect to the entity.
     *
     * @param potionEffect the potion effect
     */
    void addPotionEffect(PotionEffect potionEffect);

    /**
     * Returns the active potion effect of the specified type. When the effect is not present on the entity then an
     * empty optional will be returned.
     *
     * @param potionEffectType the potion effect type
     * @return                 an optional containing the active potion effect on this entity, or empty if not active
     */
    Optional<PotionEffect> getPotionEffect(PotionEffectType potionEffectType);

    /**
     * Removes any effects present of the given potion effect type.
     *
     * @param potionEffectType the potion effect type to remove
     */
    void removePotionEffect(PotionEffectType potionEffectType);
}
