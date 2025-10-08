package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectNew;

public interface DeploymentHandlerFactory {

    DeploymentHandler create(DeploymentProperties deploymentProperties, ItemEffectNew itemEffect);
}
