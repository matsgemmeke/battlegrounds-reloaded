package nl.matsgemmeke.battlegrounds.game.component.deploy;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

class DeploymentObjectRegistryTest {

    private DeploymentObjectRegistry deploymentObjectRegistry;

    @BeforeEach
    void setUp() {
        deploymentObjectRegistry = new DeploymentObjectRegistry();
    }

    @Test
    @DisplayName("getAllDeploymentObjects returns all deployment objects registered to the registry")
    void getAllDeploymentObjects_success() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);

        deploymentObjectRegistry.register(deploymentObject);
        Set<DeploymentObject> deploymentObjects = deploymentObjectRegistry.getAllDeploymentObjects();

        assertThat(deploymentObjects).containsExactly(deploymentObject);
    }

    @Test
    @DisplayName("getDamageableDeploymentObjects returns all deployment objects registered to the registry")
    void getDamageableDeploymentObjects_success() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class, withSettings().extraInterfaces(DamageTarget.class));

        deploymentObjectRegistry.register(deploymentObject);
        Set<DamageTarget> damageableDeploymentObjects = deploymentObjectRegistry.getDamageableDeploymentObjects();

        assertThat(damageableDeploymentObjects).containsExactly((DamageTarget) deploymentObject);
    }
}
