package nl.matsgemmeke.battlegrounds.game.component.deploy;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.item.deploy.object.DeploymentObject;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DeploymentObjectRegistry {

    private final Set<DeploymentObject> deploymentObjects;
    private final Set<DeploymentObject> damageableDeploymentObjects;

    public DeploymentObjectRegistry() {
        this.deploymentObjects = new HashSet<>();
        this.damageableDeploymentObjects = new HashSet<>();
    }

    public Set<DeploymentObject> getAllDeploymentObjects() {
        return Set.copyOf(deploymentObjects);
    }

    public Set<DamageTarget> getDamageableDeploymentObjects() {
        return damageableDeploymentObjects.stream()
                .map(DamageTarget.class::cast)
                .collect(Collectors.toSet());
    }

    public void register(DeploymentObject deploymentObject) {
        deploymentObjects.add(deploymentObject);

        if (deploymentObject instanceof DamageTarget) {
            damageableDeploymentObjects.add(deploymentObject);
        }
    }

    public void unregister(DeploymentObject deploymentObject) {
        if (!deploymentObjects.contains(deploymentObject)) {
            return;
        }

        deploymentObjects.remove(deploymentObject);
        damageableDeploymentObjects.remove(deploymentObject);
    }
}
