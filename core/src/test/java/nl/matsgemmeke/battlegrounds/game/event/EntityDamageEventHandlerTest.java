package nl.matsgemmeke.battlegrounds.game.event;

import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentObjectRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.DamageContext;
import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntityDamageEventHandlerTest {

    private static final double DAMAGE_AMOUNT = 10.0;
    private static final UUID DEPLOYMENT_OBJECT_UNIQUE_ID = UUID.randomUUID();

    @Mock
    private DamageProcessor damageProcessor;
    @Mock
    private DeploymentObjectRegistry deploymentObjectRegistry;
    @InjectMocks
    private EntityDamageEventHandler eventHandler;

    @Test
    @DisplayName("handle does not process damage when entity does not match with any of the deployment objects")
    void handle_entityDoesNotMatch() {
        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(UUID.randomUUID());

        DamageTarget deploymentObject = mock(DamageTarget.class);
        when(deploymentObject.getUniqueId()).thenReturn(DEPLOYMENT_OBJECT_UNIQUE_ID);

        EntityDamageEvent event = new EntityDamageEvent(entity, DamageCause.CUSTOM, 0);

        when(deploymentObjectRegistry.getDamageableDeploymentObjects()).thenReturn(Set.of(deploymentObject));

        eventHandler.handle(event);

        verifyNoInteractions(damageProcessor);
    }

    @Test
    @DisplayName("handle processes damage when entity matches with a deployment object")
    void handle_entityMatches() {
        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(DEPLOYMENT_OBJECT_UNIQUE_ID);

        DamageTarget deploymentObject = mock(DamageTarget.class);
        when(deploymentObject.getUniqueId()).thenReturn(DEPLOYMENT_OBJECT_UNIQUE_ID);

        EntityDamageEvent event = new EntityDamageEvent(entity, DamageCause.CUSTOM, DAMAGE_AMOUNT);

        when(deploymentObjectRegistry.getDamageableDeploymentObjects()).thenReturn(Set.of(deploymentObject));

        eventHandler.handle(event);

        ArgumentCaptor<DamageContext> damageContextCaptor = ArgumentCaptor.forClass(DamageContext.class);
        verify(damageProcessor).processDamage(damageContextCaptor.capture());

        assertThat(damageContextCaptor.getValue()).satisfies(damageContext -> {
            assertThat(damageContext.source()).isNull();
            assertThat(damageContext.target()).isEqualTo(deploymentObject);
            assertThat(damageContext.damage()).satisfies(damage -> {
               assertThat(damage.type()).isEqualTo(DamageType.ENVIRONMENTAL_DAMAGE);
               assertThat(damage.amount()).isEqualTo(DAMAGE_AMOUNT);
            });
        });
    }
}
