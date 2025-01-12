package nl.matsgemmeke.battlegrounds.game.event;

import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class EntityDamageEventHandlerTest {

    private DamageProcessor damageProcessor;
    private DeploymentInfoProvider deploymentInfoProvider;

    @BeforeEach
    public void setUp() {
        damageProcessor = mock(DamageProcessor.class);
        deploymentInfoProvider = mock(DeploymentInfoProvider.class);
    }

    @Test
    public void handleDoesNotProcessDamageIfEntityMatchesNoneOfTheDeploymentObjects() {
        Entity entity = mock(Entity.class);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.matchesEntity(entity)).thenReturn(false);

        EntityDamageEvent event = new EntityDamageEvent(entity, DamageCause.CUSTOM, 0);

        when(deploymentInfoProvider.getAllDeploymentObjects()).thenReturn(List.of(deploymentObject));

        EntityDamageEventHandler eventHandler = new EntityDamageEventHandler(damageProcessor, deploymentInfoProvider);
        eventHandler.handle(event);

        verifyNoInteractions(damageProcessor);
    }

    @Test
    public void handleProcessesDamageIfEntityMatchesDeploymentObject() {
        double damageAmount = 10.0;
        Entity entity = mock(Entity.class);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.matchesEntity(entity)).thenReturn(true);

        EntityDamageEvent event = new EntityDamageEvent(entity, DamageCause.CUSTOM, damageAmount);

        when(deploymentInfoProvider.getAllDeploymentObjects()).thenReturn(List.of(deploymentObject));

        EntityDamageEventHandler eventHandler = new EntityDamageEventHandler(damageProcessor, deploymentInfoProvider);
        eventHandler.handle(event);

        verify(damageProcessor).processDeploymentObjectDamage(deploymentObject, new Damage(damageAmount, DamageType.ENVIRONMENTAL_DAMAGE));
    }
}
