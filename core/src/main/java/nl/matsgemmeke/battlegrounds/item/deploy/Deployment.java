package nl.matsgemmeke.battlegrounds.item.deploy;

import org.bukkit.entity.Entity;

import java.util.Optional;

public interface Deployment {

    Optional<DeploymentContext> createContext(Deployer deployer, Entity deployerEntity);
}
