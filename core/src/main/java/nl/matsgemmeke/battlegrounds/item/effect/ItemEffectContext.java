package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ItemEffectContext {

    @NotNull
    private Deployer deployer;
    @NotNull
    private ItemEffectSource source;
    @NotNull
    private LivingEntity entity;

    public ItemEffectContext(@NotNull Deployer deployer, @NotNull LivingEntity entity, @NotNull ItemEffectSource source) {
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
    public LivingEntity getEntity() {
        return entity;
    }

    public void setEntity(@NotNull LivingEntity entity) {
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
