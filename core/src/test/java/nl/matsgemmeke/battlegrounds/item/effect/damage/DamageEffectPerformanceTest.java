package nl.matsgemmeke.battlegrounds.item.effect.damage;

import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponent;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.DamageContext;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DamageEffectPerformanceTest {

    private static final DamageType DAMAGE_TYPE = DamageType.BULLET_DAMAGE;
    private static final RangeProfile RANGE_PROFILE = new RangeProfile(10.0, 1.0, 5.0, 2.0, 2.5, 3.0);
    private static final HitboxMultiplierProfile HITBOX_MULTIPLIER_PROFILE = new HitboxMultiplierProfile(2.0, 1.0, 0.5);
    private static final DamageProperties PROPERTIES = new DamageProperties(DAMAGE_TYPE, RANGE_PROFILE, HITBOX_MULTIPLIER_PROFILE);

    @Mock
    private DamageProcessor damageProcessor;
    @Mock
    private DamageSource damageSource;
    @Mock
    private ItemEffectSource effectSource;
    @Mock
    private TriggerTarget triggerTarget;

    private DamageEffectPerformance effectPerformance;

    @BeforeEach
    void setUp() {
        effectPerformance = new DamageEffectPerformance(damageProcessor, PROPERTIES);
    }

    @Test
    void isPerformingAlwaysReturnsFalse() {
        boolean performing = effectPerformance.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    void performCausesNoDamageWhenHitDamageTargetIsNull() {
        CollisionResult collisionResult = new CollisionResult(null, null, null);
        ItemEffectContext context = new ItemEffectContext(collisionResult, damageSource, effectSource, triggerTarget, null);

        effectPerformance.perform(context);

        verifyNoInteractions(damageProcessor);
    }

    @Test
    void performCausesNoDamageWhenHitLocationIsNull() {
        DamageTarget hitTarget = mock(DamageTarget.class);
        CollisionResult collisionResult = new CollisionResult(null, hitTarget, null);
        ItemEffectContext context = new ItemEffectContext(collisionResult, damageSource, effectSource, triggerTarget, null);

        effectPerformance.perform(context);

        verifyNoInteractions(damageProcessor);
    }

    @ParameterizedTest
    @CsvSource(value = { "HEAD,20.0", "TORSO,10.0", "LIMBS,5.0" })
    void performCausesDamageToHitDamageTargetByHitHitboxComponentType(HitboxComponentType hitboxComponentType, double expectedDamage) {
        World world = mock(World.class);
        Location initiationLocation = new Location(world, 1, 0, 0);
        Location hitLocation = new Location(world, 2, 0, 0);
        HitboxComponent hitboxComponent = new HitboxComponent(hitboxComponentType, 0, 0, 0, 0, 0, 0);

        Hitbox hitbox = mock(Hitbox.class);
        when(hitbox.getIntersectedHitboxComponent(hitLocation)).thenReturn(Optional.of(hitboxComponent));

        DamageTarget target = mock(DamageTarget.class);
        when(target.getHitbox()).thenReturn(hitbox);

        CollisionResult collisionResult = new CollisionResult(null, target, hitLocation);
        ItemEffectContext context = new ItemEffectContext(collisionResult, damageSource, effectSource, triggerTarget, initiationLocation);

        effectPerformance.perform(context);

        ArgumentCaptor<DamageContext> damageContextCaptor = ArgumentCaptor.forClass(DamageContext.class);
        verify(damageProcessor).processDamage(damageContextCaptor.capture());

        assertThat(damageContextCaptor.getAllValues()).satisfiesExactly(damageContext -> {
                    assertThat(damageContext.source()).isEqualTo(damageSource);
                    assertThat(damageContext.target()).isEqualTo(target);
                    assertThat(damageContext.damage()).satisfies(damage -> {
                        assertThat(damage.amount()).isEqualTo(expectedDamage);
                        assertThat(damage.type()).isEqualTo(DamageType.BULLET_DAMAGE);
                    });
        });
    }

    @Test
    void performCausesDefaultDamageToHitDamageTargetWhenNoHitboxComponentWasHit() {
        World world = mock(World.class);
        Location initiationLocation = new Location(world, 1, 0, 0);
        Location hitLocation = new Location(world, 2, 0, 0);

        Hitbox hitbox = mock(Hitbox.class);
        when(hitbox.getIntersectedHitboxComponent(hitLocation)).thenReturn(Optional.empty());

        DamageTarget target = mock(DamageTarget.class);
        when(target.getHitbox()).thenReturn(hitbox);

        CollisionResult collisionResult = new CollisionResult(null, target, hitLocation);
        ItemEffectContext context = new ItemEffectContext(collisionResult, damageSource, effectSource, triggerTarget, initiationLocation);

        effectPerformance.perform(context);

        ArgumentCaptor<DamageContext> damageContextCaptor = ArgumentCaptor.forClass(DamageContext.class);
        verify(damageProcessor).processDamage(damageContextCaptor.capture());

        assertThat(damageContextCaptor.getAllValues()).satisfiesExactly(damageContext -> {
            assertThat(damageContext.source()).isEqualTo(damageSource);
            assertThat(damageContext.target()).isEqualTo(target);
            assertThat(damageContext.damage()).satisfies(damage -> {
                assertThat(damage.amount()).isEqualTo(10.0);
                assertThat(damage.type()).isEqualTo(DamageType.BULLET_DAMAGE);
            });
        });
    }

    @Test
    void cancelCancelsAllTriggerRuns() {
        TriggerRun triggerRun = mock(TriggerRun.class);

        effectPerformance.addTriggerRun(triggerRun);
        effectPerformance.cancel();

        verify(triggerRun).cancel();
    }
}
