package nl.matsgemmeke.battlegrounds.game.training.event;

import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.component.info.deploy.DeploymentInfoProvider;
import org.bukkit.event.entity.EntityCombustEvent;
import org.jetbrains.annotations.NotNull;

public class EntityCombustEventHandler implements EventHandler<EntityCombustEvent> {

    @NotNull
    private DeploymentInfoProvider deploymentInfoProvider;

    public EntityCombustEventHandler(@NotNull DeploymentInfoProvider deploymentInfoProvider) {
        this.deploymentInfoProvider = deploymentInfoProvider;
    }

    public void handle(@NotNull EntityCombustEvent event) {
        System.out.println(deploymentInfoProvider);
    }
}
