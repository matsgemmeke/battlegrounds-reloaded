package nl.matsgemmeke.battlegrounds.item.deploynew;

import nl.matsgemmeke.battlegrounds.item.deploynew.state.DeploymentState;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;

public interface DeploymentFactory {

    Deployment create(DeploymentState state, ItemEffect itemEffect);
}
