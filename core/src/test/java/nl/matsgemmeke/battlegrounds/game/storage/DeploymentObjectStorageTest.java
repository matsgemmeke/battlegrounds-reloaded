package nl.matsgemmeke.battlegrounds.game.storage;

import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class DeploymentObjectStorageTest {

    @Test
    public void addDeploymentObjectAddsDeploymentObjectToList() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);

        DeploymentObjectStorage storage = new DeploymentObjectStorage();
        storage.addDeploymentObject(deploymentObject);

        assertTrue(storage.getDeploymentObjects().contains(deploymentObject));
    }
}
