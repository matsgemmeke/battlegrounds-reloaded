package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ItemEffectContext {

    @NotNull
    private Deployer deployer;
    @NotNull
    private Entity entity;
    @NotNull
    private ItemEffectSource source;

    public ItemEffectContext(@NotNull Deployer deployer, @NotNull Entity entity, @NotNull ItemEffectSource source) {
        this.deployer = deployer;
        this.entity = entity;
        this.source = source;
    }

    @NotNull
    public Deployer getDeployer() {
        return deployer;
    }

    public void setDeployer(@NotNull Deployer deployer) {
        this.deployer = deployer;
    }

    @NotNull
    public Entity getEntity() {
        return entity;
    }

    public void setEntity(@NotNull Entity entity) {
        this.entity = entity;
    }

    @NotNull
    public ItemEffectSource getSource() {
        return source;
    }

    public void setSource(@NotNull ItemEffectSource source) {
        this.source = source;
    }
}
