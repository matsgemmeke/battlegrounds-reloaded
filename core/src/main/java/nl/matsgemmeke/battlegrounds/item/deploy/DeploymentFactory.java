package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.item.deploy.state.DeploymentState;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;

public interface DeploymentFactory {

    Deployment create(DeploymentState state, ItemEffect itemEffect);
}
