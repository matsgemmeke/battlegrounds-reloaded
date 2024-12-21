package nl.matsgemmeke.battlegrounds.game.component.deploy;

import nl.matsgemmeke.battlegrounds.game.storage.DeploymentObjectStorage;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class DefaultDeploymentObjectRegistryTest {

    private DeploymentObjectStorage deploymentObjectStorage;

    @BeforeEach
    public void setUp() {
        deploymentObjectStorage = new DeploymentObjectStorage();
    }

    @Test
    public void registerDeploymentObjectAddsGivenDeploymentObjectToStorage() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);

        DefaultDeploymentObjectRegistry deploymentObjectRegistry = new DefaultDeploymentObjectRegistry(deploymentObjectStorage);
        deploymentObjectRegistry.registerDeploymentObject(deploymentObject);

        assertTrue(deploymentObjectStorage.getDeploymentObjects().contains(deploymentObject));
    }
}
