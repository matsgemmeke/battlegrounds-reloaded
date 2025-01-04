package nl.matsgemmeke.battlegrounds.game.training.event;

import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityCombustEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EntityCombustEventHandlerTest {

    private DeploymentInfoProvider deploymentInfoProvider;

    @BeforeEach
    public void setUp() {
        deploymentInfoProvider = mock(DeploymentInfoProvider.class);
    }

    @Test
    public void handleCancelsEventIfAnyDeploymentObjectExistsThatMatchesTheEntity() {
        Entity entity = mock(Entity.class);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.matchesEntity(entity)).thenReturn(true);

        EntityCombustEvent event = new EntityCombustEvent(entity, 0);

        when(deploymentInfoProvider.getAllDeploymentObjects()).thenReturn(List.of(deploymentObject));

        EntityCombustEventHandler eventHandler = new EntityCombustEventHandler(deploymentInfoProvider);
        eventHandler.handle(event);

        assertTrue(event.isCancelled());
    }

    @Test
    public void handleDoesNotCancelEventIfNoneOfTheDeploymentObjectsMatchesTheEntity() {
        Entity entity = mock(Entity.class);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.matchesEntity(entity)).thenReturn(false);

        EntityCombustEvent event = new EntityCombustEvent(entity, 0);

        when(deploymentInfoProvider.getAllDeploymentObjects()).thenReturn(List.of(deploymentObject));

        EntityCombustEventHandler eventHandler = new EntityCombustEventHandler(deploymentInfoProvider);
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }
}
