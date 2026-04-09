package nl.matsgemmeke.battlegrounds.item.deploy.action;

import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentAction;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploy.DestructionListener;

import java.util.Optional;

public class DropDeploymentAction implements DeploymentAction {

    @Override
    public Optional<DeploymentResult> perform(Deployer deployer, DestructionListener destructionListener) {
        return Optional.empty();
    }
}
