package nl.matsgemmeke.battlegrounds.item.deploy;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface Deployment {

    @NotNull
    DeploymentResult perform(@NotNull Deployer deployer, @NotNull Entity deployerEntity);
}
