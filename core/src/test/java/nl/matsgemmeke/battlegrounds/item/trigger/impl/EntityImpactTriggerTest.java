package nl.matsgemmeke.battlegrounds.item.trigger.impl;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.entity.GameEntityFinder;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.result.DamageTargetTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntityImpactTriggerTest {

    private static final UUID SOURCE_UNIQUE_ID = UUID.randomUUID();
    private static final UUID ENTITY_UNIQUE_ID = UUID.randomUUID();

    @Mock
    private GameEntityFinder gameEntityFinder;
    @Mock
    private TriggerTarget target;
    @InjectMocks
    private EntityImpactTrigger trigger;

    @Test
    void checkReturnsEmptyOptionalWhenTriggerTargetDoesNotExist() {
        TriggerContext triggerContext = new TriggerContext(SOURCE_UNIQUE_ID, target);

        when(target.exists()).thenReturn(false);

        TriggerResult result = trigger.check(triggerContext);

        assertThat(result.activates()).isFalse();
    }

    @Test
    void checkReturnsEmptyOptionalWhenTargetVelocityIsZero() {
        Vector velocity = new Vector();
        TriggerContext triggerContext = new TriggerContext(SOURCE_UNIQUE_ID, target);

        when(target.exists()).thenReturn(true);
        when(target.getVelocity()).thenReturn(velocity);

        TriggerResult result = trigger.check(triggerContext);

        assertThat(result.activates()).isFalse();
    }

    @Test
    void checkReturnsEmptyOptionalWhenCastRayTraceResultIsNull() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, 0, 0);
        TriggerContext triggerContext = new TriggerContext(SOURCE_UNIQUE_ID, target);

        World world = mock(World.class);
        when(world.rayTraceEntities(eq(targetLocation), eq(velocity), eq(1.0), eq(0.0), any(HitEntityFilter.class))).thenReturn(null);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        TriggerResult result = trigger.check(triggerContext);

        assertThat(result.activates()).isFalse();
    }

    @Test
    void checkReturnsEmptyOptionalWhenHitEntityIsNull() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, 0, 0);
        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), (Entity) null);
        TriggerContext triggerContext = new TriggerContext(SOURCE_UNIQUE_ID, target);

        World world = mock(World.class);
        when(world.rayTraceEntities(eq(targetLocation), eq(velocity), eq(1.0), eq(0.0), any(HitEntityFilter.class))).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);

        TriggerResult result = trigger.check(triggerContext);

        assertThat(result.activates()).isFalse();
    }

    @Test
    void checkReturnsEmptyOptionalWhenHitEntityIsNoRegisteredEntity() {
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, 0, 0);
        TriggerContext triggerContext = new TriggerContext(SOURCE_UNIQUE_ID, target);

        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(ENTITY_UNIQUE_ID);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), entity);

        World world = mock(World.class);
        when(world.rayTraceEntities(eq(targetLocation), eq(velocity), eq(1.0), eq(0.0), any(HitEntityFilter.class))).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);
        when(gameEntityFinder.findGameEntityByUniqueId(ENTITY_UNIQUE_ID)).thenReturn(Optional.empty());

        TriggerResult result = trigger.check(triggerContext);

        assertThat(result.activates()).isFalse();
    }

    @Test
    void checkReturnsOptionalWithCheckResultWhenHitEntityIsRegistered() {
        GameEntity gameEntity = mock(GameEntity.class);
        Location targetLocation = new Location(null, 1, 1, 1);
        Vector hitPosition = new Vector(1.5, 0.5, 0.5);
        Vector velocity = new Vector(1, 1, 1);
        TriggerContext triggerContext = new TriggerContext(SOURCE_UNIQUE_ID, target);

        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(ENTITY_UNIQUE_ID);

        RayTraceResult rayTraceResult = new RayTraceResult(hitPosition, entity);

        World world = mock(World.class);
        when(world.rayTraceEntities(eq(targetLocation), eq(velocity), anyDouble(), eq(0.0), any(HitEntityFilter.class))).thenReturn(rayTraceResult);

        when(target.exists()).thenReturn(true);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getVelocity()).thenReturn(velocity);
        when(target.getWorld()).thenReturn(world);
        when(gameEntityFinder.findGameEntityByUniqueId(ENTITY_UNIQUE_ID)).thenReturn(Optional.of(gameEntity));

        TriggerResult result = trigger.check(triggerContext);

        assertThat(result).isInstanceOfSatisfying(DamageTargetTriggerResult.class, damageTargetTriggerResult -> {
            assertThat(damageTargetTriggerResult.getHitTarget()).isEqualTo(gameEntity);
            assertThat(damageTargetTriggerResult.getHitLocation()).isEqualTo(new Location(world, 1.5, 0.5, 0.5));
        });
    }
}
