package nl.matsgemmeke.battlegrounds.item.deploy;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface Deployment {

    @NotNull
    DeploymentObject perform(@NotNull Deployer deployer, @NotNull Entity deployerEntity);
}
