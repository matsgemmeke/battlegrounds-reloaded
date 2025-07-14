package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ItemEffectContext {

    @NotNull
    private final Deployer deployer;
    @NotNull
    private final Entity entity;
    private final Location initiationLocation;
    @NotNull
    private ItemEffectSource source;

    public ItemEffectContext(@NotNull Deployer deployer, @NotNull Entity entity, @NotNull ItemEffectSource source) {
        this(deployer, entity, null, source);
    }

    public ItemEffectContext(@NotNull Deployer deployer, @NotNull Entity entity, Location initiationLocation, @NotNull ItemEffectSource source) {
        this.deployer = deployer;
        this.entity = entity;
        this.source = source;
        this.initiationLocation = initiationLocation;
    }

    @NotNull
    public Deployer getDeployer() {
        return deployer;
    }

    /**
     * Gets the entity that initiated the effect.
     *
     * @return the initiator entity
     */
    @NotNull
    public Entity getEntity() {
        return entity;
    }

    /**
     * Gets the location where the effect was initiated.
     *
     * @return the effect's initiation location
     */
    @NotNull
    public Location getInitiationLocation() {
        return initiationLocation;
    }

    /**
     * Gets the source from where the effect is produced.
     *
     * @return the effect source
     */
    @NotNull
    public ItemEffectSource getSource() {
        return source;
    }

    /**
     * Sets the source from where the effect is produced.
     *
     * @param source the effect source
     */
    public void setSource(@NotNull ItemEffectSource source) {
        this.source = source;
    }
}
