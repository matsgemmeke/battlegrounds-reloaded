package nl.matsgemmeke.battlegrounds.game.component.deploy;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;

import java.util.HashSet;
import java.util.Set;

public class DeploymentObjectRegistry {

    private final Set<DeploymentObject> deploymentObjects;
    private final Set<DamageTarget> damageTargets;

    public DeploymentObjectRegistry() {
        this.deploymentObjects = new HashSet<>();
        this.damageTargets = new HashSet<>();
    }

    public Set<DeploymentObject> getAllDeploymentObjects() {
        return Set.copyOf(deploymentObjects);
    }

    public Set<DamageTarget> getDamageableDeploymentObjects() {
        return Set.copyOf(damageTargets);
    }

    public void register(DeploymentObject deploymentObject) {
        deploymentObjects.add(deploymentObject);
        damageTargets.add(deploymentObject);
    }

    public void unregister(DeploymentObject deploymentObject) {
        if (!deploymentObjects.contains(deploymentObject)) {
            return;
        }

        deploymentObjects.remove(deploymentObject);
        damageTargets.remove(deploymentObject);
    }
}
