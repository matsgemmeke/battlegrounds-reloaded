package nl.matsgemmeke.battlegrounds.item.effect.damage;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponent;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.TargetQuery;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.DamageContext;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerTarget;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DamageEffectPerformanceTest {

    private static final RangeProfile RANGE_PROFILE = new RangeProfile(10.0, 1.0, 5.0, 2.0, 2.5, 3.0);
    private static final DamageType DAMAGE_TYPE = DamageType.BULLET_DAMAGE;
    private static final HitboxMultiplierProfile HITBOX_MULTIPLIER_PROFILE = new HitboxMultiplierProfile(2.0, 1.0, 0.5);
    private static final DamageProperties PROPERTIES = new DamageProperties(HITBOX_MULTIPLIER_PROFILE, RANGE_PROFILE, DAMAGE_TYPE);

    @Mock
    private DamageProcessor damageProcessor;
    @Mock
    private DamageSource damageSource;
    @Mock
    private ItemEffectSource effectSource;
    @Mock
    private TargetFinder targetFinder;
    @Mock
    private TriggerTarget triggerTarget;

    private DamageEffectPerformance damageEffectPerformance;

    @BeforeEach
    void setUp() {
        damageEffectPerformance = new DamageEffectPerformance(damageProcessor, targetFinder, PROPERTIES);
    }

    @Test
    void isPerformingAlwaysReturnsFalse() {
        boolean performing = damageEffectPerformance.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    void performCausesZeroDamageToNearbyEntitiesWhenHitboxWasNotHit() {
        World world = mock(World.class);
        Location initiationLocation = new Location(world, 1, 0, 0);
        Location effectSourceLocation = new Location(world, 2, 0, 0);
        Location targetLocation = new Location(world, 2, 0, 0);
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, triggerTarget, initiationLocation);

        Hitbox hitbox = mock(Hitbox.class);
        when(hitbox.getIntersectedHitboxComponent(effectSourceLocation)).thenReturn(Optional.empty());

        DamageTarget target = mock(DamageTarget.class);
        when(target.getHitbox()).thenReturn(hitbox);
        when(target.getLocation()).thenReturn(targetLocation);

        when(effectSource.getLocation()).thenReturn(effectSourceLocation);
        when(targetFinder.findTargets(any(TargetQuery.class))).thenReturn(List.of(target));

        damageEffectPerformance.perform(context);

        ArgumentCaptor<DamageContext> damageContextCaptor = ArgumentCaptor.forClass(DamageContext.class);
        verify(damageProcessor).processDamage(damageContextCaptor.capture());

        assertThat(damageContextCaptor.getValue()).satisfies(damageContext -> {
            assertThat(damageContext.source()).isNull();
            assertThat(damageContext.target()).isEqualTo(target);
            assertThat(damageContext.damage()).satisfies(damage -> {
                assertThat(damage.amount()).isZero();
                assertThat(damage.type()).isEqualTo(DamageType.BULLET_DAMAGE);
            });
        });
    }

    @ParameterizedTest
    @CsvSource({ "HEAD,20.0", "TORSO,10.0", "LIMBS,5.0" })
    void performCausesDamageToNearbyEntitiesAndObjects(HitboxComponentType hitboxComponentType, double expectedDamage) {
        World world = mock(World.class);
        Location initiationLocation = new Location(world, 1, 0, 0);
        Location effectSourceLocation = new Location(world, 2, 0, 0);
        Location targetLocation = new Location(world, 2, 0, 0);
        HitboxComponent hitboxComponent = new HitboxComponent(hitboxComponentType, 0, 0, 0, 0, 0, 0);
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, triggerTarget, initiationLocation);

        Hitbox hitbox = mock(Hitbox.class);
        when(hitbox.getIntersectedHitboxComponent(effectSourceLocation)).thenReturn(Optional.of(hitboxComponent));

        DamageTarget target = mock(DamageTarget.class);
        when(target.getHitbox()).thenReturn(hitbox);
        when(target.getLocation()).thenReturn(targetLocation);

        when(effectSource.getLocation()).thenReturn(effectSourceLocation);
        when(targetFinder.findTargets(any(TargetQuery.class))).thenReturn(List.of(target));

        damageEffectPerformance.perform(context);

        ArgumentCaptor<DamageContext> damageContextCaptor = ArgumentCaptor.forClass(DamageContext.class);
        verify(damageProcessor).processDamage(damageContextCaptor.capture());

        assertThat(damageContextCaptor.getAllValues()).satisfiesExactly(
                damageContext -> {
                    assertThat(damageContext.source()).isNull();
                    assertThat(damageContext.target()).isEqualTo(target);
                    assertThat(damageContext.damage()).satisfies(damage -> {
                        assertThat(damage.amount()).isEqualTo(expectedDamage);
                        assertThat(damage.type()).isEqualTo(DamageType.BULLET_DAMAGE);
                    });
                }
        );
    }

    @Test
    void cancelCancelsAllTriggerRuns() {
        TriggerRun triggerRun = mock(TriggerRun.class);

        damageEffectPerformance.addTriggerRun(triggerRun);
        damageEffectPerformance.cancel();

        verify(triggerRun).cancel();
    }
}
