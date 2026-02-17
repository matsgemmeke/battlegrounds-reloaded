package nl.matsgemmeke.battlegrounds.item.deploy;

import org.bukkit.entity.Entity;

import java.util.Optional;

public interface Deployment {

    Optional<DeploymentResult> perform(Deployer deployer, Entity deployerEntity, DestructionListener destructionListener);
}
