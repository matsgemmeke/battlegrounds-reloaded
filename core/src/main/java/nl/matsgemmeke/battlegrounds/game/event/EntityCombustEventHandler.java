package nl.matsgemmeke.battlegrounds.game.event;

import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.bukkit.event.entity.EntityCombustEvent;
import org.jetbrains.annotations.NotNull;

public class EntityCombustEventHandler implements EventHandler<EntityCombustEvent> {

    @NotNull
    private DeploymentInfoProvider deploymentInfoProvider;

    public EntityCombustEventHandler(@NotNull DeploymentInfoProvider deploymentInfoProvider) {
        this.deploymentInfoProvider = deploymentInfoProvider;
    }

    public void handle(@NotNull EntityCombustEvent event) {
        for (DeploymentObject deploymentObject : deploymentInfoProvider.getAllDeploymentObjects()) {
            if (deploymentObject.matchesEntity(event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }
}
