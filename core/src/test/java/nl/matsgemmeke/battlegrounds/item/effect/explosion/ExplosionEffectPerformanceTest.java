package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.actor.Removable;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExplosionEffectPerformanceTest {

    private static final boolean BREAK_BLOCKS = false;
    private static final boolean SET_FIRE = false;
    private static final double LONG_RANGE_DAMAGE = 25.0;
    private static final double LONG_RANGE_DISTANCE = 10.0;
    private static final double MEDIUM_RANGE_DAMAGE = 75.0;
    private static final double MEDIUM_RANGE_DISTANCE = 5.0;
    private static final double SHORT_RANGE_DAMAGE = 150.0;
    private static final double SHORT_RANGE_DISTANCE = 2.5;
    private static final float POWER = 1.0F;
    private static final Location STARTING_LOCATION = new Location(null, 0, 0, 0);
    private static final RangeProfile RANGE_PROFILE = new RangeProfile(SHORT_RANGE_DAMAGE, SHORT_RANGE_DISTANCE, MEDIUM_RANGE_DAMAGE, MEDIUM_RANGE_DISTANCE, LONG_RANGE_DAMAGE, LONG_RANGE_DISTANCE);
    private static final ExplosionProperties PROPERTIES = new ExplosionProperties(RANGE_PROFILE, POWER, SET_FIRE, BREAK_BLOCKS);

    private static final UUID DAMAGE_SOURCE_ID = UUID.randomUUID();
    private static final CollisionResult COLLISION_RESULT = new CollisionResult(null, null, null);

    @Mock
    private DamageProcessor damageProcessor;
    @Mock
    private TargetFinder targetFinder;

    private ExplosionEffectPerformance performance;

    @BeforeEach
    void setUp() {
        performance = new ExplosionEffectPerformance(damageProcessor, targetFinder, PROPERTIES);
    }

    @Test
    @DisplayName("isPerforming returns false even after starting performance")
    void isPerforming_returnsFalse() {
        DamageSource damageSource = mock(DamageSource.class);
        World world = mock(World.class);

        Actor actor = mock(Actor.class, withSettings().extraInterfaces(Removable.class));
        when(actor.getWorld()).thenReturn(world);

        ItemEffectContext context = new ItemEffectContext(COLLISION_RESULT, damageSource, actor, STARTING_LOCATION);

        performance.setContext(context);
        performance.start();
        boolean performing = performance.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    @DisplayName("perform creates explosion at actor location and damage all entities inside the long range")
    void perform_createsExplosionAndDamagesEntities() {
        World world = mock(World.class);
        Location objectLocation = new Location(world, 2, 1, 1);
        Location actorLocation = new Location(world, 1, 1, 1);
        Location targetLocation = new Location(world, 8, 1, 1);

        DamageSource damageSource = mock(DamageSource.class);
        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);

        Actor actor = mock(Actor.class, withSettings().extraInterfaces(Removable.class));
        when(actor.getLocation()).thenReturn(actorLocation);
        when(actor.getWorld()).thenReturn(world);

        ItemEffectContext context = new ItemEffectContext(COLLISION_RESULT, damageSource, actor, STARTING_LOCATION);

        GameEntity deployerEntity = mock(GameEntity.class);
        when(deployerEntity.getLocation()).thenReturn(actorLocation);

        GameEntity target = mock(GameEntity.class);
        when(target.getLocation()).thenReturn(targetLocation);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getLocation()).thenReturn(objectLocation);

        when(targetFinder.findDeploymentObjects(DAMAGE_SOURCE_ID, actorLocation, LONG_RANGE_DISTANCE)).thenReturn(List.of(deploymentObject));
        when(targetFinder.findTargets(DAMAGE_SOURCE_ID, actorLocation, LONG_RANGE_DISTANCE)).thenReturn(List.of(deployerEntity, target));

        performance.setContext(context);
        performance.start();

        verify(damageProcessor).processDeploymentObjectDamage(deploymentObject, new Damage(SHORT_RANGE_DAMAGE, DamageType.EXPLOSIVE_DAMAGE));
        verify(deployerEntity).damage(new Damage(SHORT_RANGE_DAMAGE, DamageType.EXPLOSIVE_DAMAGE));
        verify(target).damage(new Damage(LONG_RANGE_DAMAGE, DamageType.EXPLOSIVE_DAMAGE));
        verify(world).createExplosion(actorLocation, POWER, SET_FIRE, BREAK_BLOCKS);
        verify((Removable) actor).remove();
    }
}
