package nl.matsgemmeke.battlegrounds.item.trigger.impl;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.entity.GameEntityFinder;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.result.DamageTargetTriggerResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
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
    private Actor actor;
    @Mock
    private GameEntityFinder gameEntityFinder;
    @InjectMocks
    private EntityImpactTrigger trigger;

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenTriggerActorDoesNotExist() {
        TriggerContext triggerContext = new TriggerContext(SOURCE_UNIQUE_ID, actor);

        when(actor.exists()).thenReturn(false);

        TriggerResult result = trigger.check(triggerContext);

        assertThat(result.activates()).isFalse();
    }

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenActorVelocityIsZero() {
        Vector velocity = new Vector();
        TriggerContext triggerContext = new TriggerContext(SOURCE_UNIQUE_ID, actor);

        when(actor.exists()).thenReturn(true);
        when(actor.getVelocity()).thenReturn(velocity);

        TriggerResult result = trigger.check(triggerContext);

        assertThat(result.activates()).isFalse();
    }

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenCastRayTraceResultIsNull() {
        Location actorLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, 0, 0);
        TriggerContext triggerContext = new TriggerContext(SOURCE_UNIQUE_ID, actor);

        World world = mock(World.class);
        when(world.rayTraceEntities(eq(actorLocation), eq(velocity), eq(1.0), eq(0.0), any(HitEntityFilter.class))).thenReturn(null);

        when(actor.exists()).thenReturn(true);
        when(actor.getLocation()).thenReturn(actorLocation);
        when(actor.getVelocity()).thenReturn(velocity);
        when(actor.getWorld()).thenReturn(world);

        TriggerResult result = trigger.check(triggerContext);

        assertThat(result.activates()).isFalse();
    }

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenHitEntityIsNull() {
        Location actorLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, 0, 0);
        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), (Entity) null);
        TriggerContext triggerContext = new TriggerContext(SOURCE_UNIQUE_ID, actor);

        World world = mock(World.class);
        when(world.rayTraceEntities(eq(actorLocation), eq(velocity), eq(1.0), eq(0.0), any(HitEntityFilter.class))).thenReturn(rayTraceResult);

        when(actor.exists()).thenReturn(true);
        when(actor.getLocation()).thenReturn(actorLocation);
        when(actor.getVelocity()).thenReturn(velocity);
        when(actor.getWorld()).thenReturn(world);

        TriggerResult result = trigger.check(triggerContext);

        assertThat(result.activates()).isFalse();
    }

    @Test
    void checkReturnsTriggerResultThatDoesNotActivateWhenHitEntityIsNoRegisteredEntity() {
        Location actorLocation = new Location(null, 1, 1, 1);
        Vector velocity = new Vector(1, 0, 0);
        TriggerContext triggerContext = new TriggerContext(SOURCE_UNIQUE_ID, actor);

        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(ENTITY_UNIQUE_ID);

        RayTraceResult rayTraceResult = new RayTraceResult(new Vector(), entity);

        World world = mock(World.class);
        when(world.rayTraceEntities(eq(actorLocation), eq(velocity), eq(1.0), eq(0.0), any(HitEntityFilter.class))).thenReturn(rayTraceResult);

        when(actor.exists()).thenReturn(true);
        when(actor.getLocation()).thenReturn(actorLocation);
        when(actor.getVelocity()).thenReturn(velocity);
        when(actor.getWorld()).thenReturn(world);
        when(gameEntityFinder.findGameEntityByUniqueId(ENTITY_UNIQUE_ID)).thenReturn(Optional.empty());

        TriggerResult result = trigger.check(triggerContext);

        assertThat(result.activates()).isFalse();
    }

    @Test
    void checkReturnsTriggerResultWithHitTargetAndHitLocationWhenHitEntityIsRegistered() {
        GameEntity gameEntity = mock(GameEntity.class);
        Location actorLocation = new Location(null, 1, 1, 1);
        Vector hitPosition = new Vector(1.5, 0.5, 0.5);
        Vector velocity = new Vector(1, 1, 1);
        TriggerContext triggerContext = new TriggerContext(SOURCE_UNIQUE_ID, actor);

        Entity entity = mock(Entity.class);
        when(entity.getUniqueId()).thenReturn(ENTITY_UNIQUE_ID);

        RayTraceResult rayTraceResult = new RayTraceResult(hitPosition, entity);

        World world = mock(World.class);
        when(world.rayTraceEntities(eq(actorLocation), eq(velocity), anyDouble(), eq(0.0), any(HitEntityFilter.class))).thenReturn(rayTraceResult);

        when(actor.exists()).thenReturn(true);
        when(actor.getLocation()).thenReturn(actorLocation);
        when(actor.getVelocity()).thenReturn(velocity);
        when(actor.getWorld()).thenReturn(world);
        when(gameEntityFinder.findGameEntityByUniqueId(ENTITY_UNIQUE_ID)).thenReturn(Optional.of(gameEntity));

        TriggerResult result = trigger.check(triggerContext);

        assertThat(result).isInstanceOfSatisfying(DamageTargetTriggerResult.class, damageTargetTriggerResult -> {
            assertThat(damageTargetTriggerResult.getHitTarget()).isEqualTo(gameEntity);
            assertThat(damageTargetTriggerResult.getHitLocation()).isEqualTo(new Location(world, 1.5, 0.5, 0.5));
        });
    }
}
