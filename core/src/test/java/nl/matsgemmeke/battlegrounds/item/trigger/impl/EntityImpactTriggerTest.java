package nl.matsgemmeke.battlegrounds.item.trigger.impl;

import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntityImpactTriggerTest {

    private static final UUID SOURCE_ID = UUID.fromString("f0fe1496-cdbe-44f1-ad94-fb5c986e3f00");;

    @Mock
    private TriggerTarget target;

    private final EntityImpactTrigger trigger = new EntityImpactTrigger();

    @Test
    void activatesReturnFalseWhenTriggerTargetDoesNotExist() {
        TriggerContext context = new TriggerContext(SOURCE_ID, target);

        when(target.exists()).thenReturn(false);

        boolean activates = trigger.activates(context);

        assertThat(activates).isFalse();
    }

    @Test
    void activatesReturnsFalseWhenTargetVelocityIsZero() {
        Vector velocity = new Vector();
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        when(target.exists()).thenReturn(true);
        when(target.getVelocity()).thenReturn(velocity);

        boolean activates = trigger.activates(triggerContext);

        assertThat(activates).isFalse();
    }

    @Test
    void activatesReturnsFalseWhenCastRayTraceResultIsNull() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, 0, 0);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        World world = mock(World.class);
        when(world.rayTraceEntities(targetLocation, velocity, 1.0, 1.0, null)).thenReturn(null);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        boolean activates = trigger.activates(triggerContext);

        assertThat(activates).isFalse();
    }

    @Test
    void activatesReturnsFalseWhenHitEntityIsNull() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, 0, 0);
        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), (Entity) null);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        World world = mock(World.class);
        when(world.rayTraceEntities(targetLocation, velocity, 1.0, 1.0, null)).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        boolean activates = trigger.activates(triggerContext);

        assertThat(activates).isFalse();
    }

    @ParameterizedTest
    @CsvSource({ "f0fe1496-cdbe-44f1-ad94-fb5c986e3f00,false", "00000000-0000-0000-0000-000000000001,true" })
    void activatesReturnsWhetherHitEntityUniqueIdDoesNotMatchSourceId(UUID entityUniqueId, boolean expectedResult) {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, 0, 0);
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, target);

        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(entityUniqueId);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), entity);

        World world = mock(World.class);
        when(world.rayTraceEntities(targetLocation, velocity, 1.0, 1.0, null)).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        boolean activates = trigger.activates(triggerContext);

        assertThat(activates).isEqualTo(expectedResult);
    }
}
