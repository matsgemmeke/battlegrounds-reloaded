package nl.matsgemmeke.battlegrounds.item.deploy;

import java.util.Optional;

public interface DeploymentAction {

    Optional<DeploymentResult> perform(Deployer deployer, DestructionListener destructionListener);
}
