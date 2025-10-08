package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;

public interface DeploymentHandlerFactory {

    DeploymentHandler create(DeploymentProperties deploymentProperties, ItemEffect itemEffect);
}
