package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import org.bukkit.Location;

public class ItemEffectContext {

    private final DamageSource damageSource;
    private final Location initiationLocation;
    private ItemEffectSource effectSource;

    public ItemEffectContext(DamageSource damageSource, ItemEffectSource effectSource, Location initiationLocation) {
        this.damageSource = damageSource;
        this.effectSource = effectSource;
        this.initiationLocation = initiationLocation;
    }

    /**
     * Gets the damage source responsible for the damage that the effect causes.
     *
     * @return the damage source
     */
    public DamageSource getDamageSource() {
        return damageSource;
    }

    /**
     * Gets the source from where the effect is produced.
     *
     * @return the effect source
     */
    public ItemEffectSource getEffectSource() {
        return effectSource;
    }

    /**
     * Sets the source from where the effect is produced.
     *
     * @param effectSource the effect source
     */
    public void setEffectSource(ItemEffectSource effectSource) {
        this.effectSource = effectSource;
    }

    /**
     * Gets the location where the effect was initiated.
     *
     * @return the effect's initiation location
     */
    public Location getInitiationLocation() {
        return initiationLocation;
    }
}
