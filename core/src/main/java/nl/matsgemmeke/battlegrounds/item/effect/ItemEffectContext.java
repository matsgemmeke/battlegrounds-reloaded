package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class ItemEffectContext {

    private final Entity entity;
    private final Location initiationLocation;
    private ItemEffectSource source;

    public ItemEffectContext(Entity entity, ItemEffectSource source, Location initiationLocation) {
        this.entity = entity;
        this.source = source;
        this.initiationLocation = initiationLocation;
    }

    /**
     * Gets the entity that initiated the effect.
     *
     * @return the initiator entity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Gets the location where the effect was initiated.
     *
     * @return the effect's initiation location
     */
    public Location getInitiationLocation() {
        return initiationLocation;
    }

    /**
     * Gets the source from where the effect is produced.
     *
     * @return the effect source
     */
    public ItemEffectSource getSource() {
        return source;
    }

    /**
     * Sets the source from where the effect is produced.
     *
     * @param source the effect source
     */
    public void setSource(ItemEffectSource source) {
        this.source = source;
    }
}
