package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.source.Removable;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
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
    private static final Location INITIATION_LOCATION = new Location(null, 0, 0, 0);
    private static final RangeProfile RANGE_PROFILE = new RangeProfile(SHORT_RANGE_DAMAGE, SHORT_RANGE_DISTANCE, MEDIUM_RANGE_DAMAGE, MEDIUM_RANGE_DISTANCE, LONG_RANGE_DAMAGE, LONG_RANGE_DISTANCE);
    private static final ExplosionProperties PROPERTIES = new ExplosionProperties(RANGE_PROFILE, POWER, SET_FIRE, BREAK_BLOCKS);

    private static final UUID DAMAGE_SOURCE_ID = UUID.randomUUID();

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
    void isPerformingReturnsFalseEvenAfterStartingPerformance() {
        DamageSource damageSource = mock(DamageSource.class);
        TriggerTarget triggerTarget = mock(TriggerTarget.class);
        World world = mock(World.class);

        ItemEffectSource effectSource = mock(ItemEffectSource.class, withSettings().extraInterfaces(Removable.class));
        when(effectSource.getWorld()).thenReturn(world);

        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, triggerTarget, INITIATION_LOCATION);

        performance.perform(context);
        boolean performing = performance.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    void performCreatesExplosionAtSourceLocationAndDamagesAllEntitiesInsideTheLongRangeDistance() {
        World world = mock(World.class);
        Location objectLocation = new Location(world, 2, 1, 1);
        Location sourceLocation = new Location(world, 1, 1, 1);
        Location targetLocation = new Location(world, 8, 1, 1);
        TriggerTarget triggerTarget = mock(TriggerTarget.class);

        DamageSource damageSource = mock(DamageSource.class);
        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);

        ItemEffectSource effectSource = mock(ItemEffectSource.class, withSettings().extraInterfaces(Removable.class));
        when(effectSource.getLocation()).thenReturn(sourceLocation);
        when(effectSource.getWorld()).thenReturn(world);

        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, triggerTarget, INITIATION_LOCATION);

        GameEntity deployerEntity = mock(GameEntity.class);
        when(deployerEntity.getLocation()).thenReturn(sourceLocation);

        GameEntity target = mock(GameEntity.class);
        when(target.getLocation()).thenReturn(targetLocation);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getLocation()).thenReturn(objectLocation);

        when(targetFinder.findDeploymentObjects(DAMAGE_SOURCE_ID, sourceLocation, LONG_RANGE_DISTANCE)).thenReturn(List.of(deploymentObject));
        when(targetFinder.findTargets(DAMAGE_SOURCE_ID, sourceLocation, LONG_RANGE_DISTANCE)).thenReturn(List.of(deployerEntity, target));

        performance.perform(context);

        verify(damageProcessor).processDeploymentObjectDamage(deploymentObject, new Damage(SHORT_RANGE_DAMAGE, DamageType.EXPLOSIVE_DAMAGE));
        verify(deployerEntity).damage(new Damage(SHORT_RANGE_DAMAGE, DamageType.EXPLOSIVE_DAMAGE));
        verify(target).damage(new Damage(LONG_RANGE_DAMAGE, DamageType.EXPLOSIVE_DAMAGE));
        verify(world).createExplosion(sourceLocation, POWER, SET_FIRE, BREAK_BLOCKS);
        verify((Removable) effectSource).remove();
    }

    @Test
    void cancelCancelsAllTriggerRuns() {
        TriggerRun triggerRun = mock(TriggerRun.class);

        performance.addTriggerRun(triggerRun);
        performance.cancel();

        verify(triggerRun).cancel();
    }
}
