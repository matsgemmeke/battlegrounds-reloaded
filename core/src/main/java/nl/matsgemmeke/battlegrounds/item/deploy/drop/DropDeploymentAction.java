package nl.matsgemmeke.battlegrounds.item.deploy.drop;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentAction;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;
import org.bukkit.entity.Entity;

import java.util.Optional;

public class DropDeploymentAction implements DeploymentAction {

    @Override
    public Optional<DeploymentResult> perform(Deployer deployer, Entity deployerEntity, DestructionListener destructionListener) {
        return Optional.empty();
    }
}
