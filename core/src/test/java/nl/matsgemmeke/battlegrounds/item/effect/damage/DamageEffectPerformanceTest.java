package nl.matsgemmeke.battlegrounds.item.effect.damage;

import nl.matsgemmeke.battlegrounds.entity.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageType;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponent;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.DamageContext;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.actor.Removable;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private static final HitboxDamageProfile HITBOX_DAMAGE_PROFILE = new HitboxDamageProfile(2.0, 1.0, 0.5);
    private static final DamageProperties PROPERTIES = new DamageProperties(DAMAGE_TYPE, RANGE_PROFILE, HITBOX_DAMAGE_PROFILE);

    private static final String ITEM_NAME = "Test Item";

    @Mock(extraInterfaces = Removable.class)
    private Actor actor;
    @Mock
    private DamageProcessor damageProcessor;
    @Mock
    private DamageSource damageSource;

    private DamageEffectPerformance effectPerformance;

    @BeforeEach
    void setUp() {
        effectPerformance = new DamageEffectPerformance(damageProcessor, PROPERTIES);
    }

    @Test
    @DisplayName("isPerforming always returns false")
    void isPerforming() {
        boolean performing = effectPerformance.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    @DisplayName("perform causes no damage when hit damage target is null")
    void perform_damageTargetIsNull() {
        CollisionResult collisionResult = new CollisionResult(null, null, null);
        ItemEffectContext context = new ItemEffectContext(ITEM_NAME, collisionResult, damageSource, null, actor);

        effectPerformance.setContext(context);
        effectPerformance.start();

        verifyNoInteractions(damageProcessor);
    }

    @Test
    @DisplayName("perform causes no damage when hit location is null")
    void perform_hitLocationIsNull() {
        DamageTarget hitTarget = mock(DamageTarget.class);
        CollisionResult collisionResult = new CollisionResult(null, hitTarget, null);
        ItemEffectContext context = new ItemEffectContext(ITEM_NAME, collisionResult, damageSource, null, actor);

        effectPerformance.setContext(context);
        effectPerformance.start();

        verifyNoInteractions(damageProcessor);
    }

    @ParameterizedTest
    @CsvSource(value = { "HEAD,20.0", "TORSO,10.0", "LIMBS,5.0" })
    @DisplayName("perform causes damage to hit damage target by hit hitbox component type")
    void perform_damageByHitboxComponentType(HitboxComponentType hitboxComponentType, double expectedDamage) {
        World world = mock(World.class);
        Location startingLocation = new Location(world, 1, 0, 0);
        Location hitLocation = new Location(world, 2, 0, 0);
        Location targetLocation = new Location(world, 2, 0, 0);
        HitboxComponent hitboxComponent = new HitboxComponent(hitboxComponentType, 0, 0, 0, 0, 0, 0);

        Hitbox hitbox = mock(Hitbox.class);
        when(hitbox.getIntersectedHitboxComponent(hitLocation)).thenReturn(Optional.of(hitboxComponent));

        DamageTarget target = mock(DamageTarget.class);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getHitbox()).thenReturn(hitbox);

        CollisionResult collisionResult = new CollisionResult(null, target, hitLocation);
        ItemEffectContext context = new ItemEffectContext(ITEM_NAME, collisionResult, damageSource, startingLocation, actor);

        effectPerformance.setContext(context);
        effectPerformance.start();

        ArgumentCaptor<DamageContext> damageContextCaptor = ArgumentCaptor.forClass(DamageContext.class);
        verify(damageProcessor).processDamage(damageContextCaptor.capture());

        assertThat(damageContextCaptor.getAllValues()).satisfiesExactly(damageContext -> {
            assertThat(damageContext.source()).isEqualTo(damageSource);
            assertThat(damageContext.target()).isEqualTo(target);
            assertThat(damageContext.itemName()).isEqualTo(ITEM_NAME);
            assertThat(damageContext.damage()).satisfies(damage -> {
                assertThat(damage.amount()).isEqualTo(expectedDamage);
                assertThat(damage.type()).isEqualTo(DamageType.BULLET_DAMAGE);
            });
            assertThat(damageContext.distance()).isOne();
        });

        verify((Removable) actor).remove();
    }

    @Test
    @DisplayName("perform causes damage for limb hitbox component as fallback when no hitbox component")
    void perform_fallbackDamage() {
        World world = mock(World.class);
        Location startingLocation = new Location(world, 1, 0, 0);
        Location hitLocation = new Location(world, 2, 0, 0);
        Location targetLocation = new Location(world, 2, 0, 0);

        Hitbox hitbox = mock(Hitbox.class);
        when(hitbox.getIntersectedHitboxComponent(hitLocation)).thenReturn(Optional.empty());

        DamageTarget target = mock(DamageTarget.class);
        when(target.getLocation()).thenReturn(targetLocation);
        when(target.getHitbox()).thenReturn(hitbox);

        CollisionResult collisionResult = new CollisionResult(null, target, hitLocation);
        ItemEffectContext context = new ItemEffectContext(ITEM_NAME, collisionResult, damageSource, startingLocation, actor);

        effectPerformance.setContext(context);
        effectPerformance.start();

        ArgumentCaptor<DamageContext> damageContextCaptor = ArgumentCaptor.forClass(DamageContext.class);
        verify(damageProcessor).processDamage(damageContextCaptor.capture());

        assertThat(damageContextCaptor.getAllValues()).satisfiesExactly(damageContext -> {
            assertThat(damageContext.source()).isEqualTo(damageSource);
            assertThat(damageContext.target()).isEqualTo(target);
            assertThat(damageContext.itemName()).isEqualTo(ITEM_NAME);
            assertThat(damageContext.damage()).satisfies(damage -> {
                assertThat(damage.amount()).isEqualTo(5.0);
                assertThat(damage.type()).isEqualTo(DamageType.BULLET_DAMAGE);
                assertThat(damage.hitboxComponentType()).isEqualTo(HitboxComponentType.LIMBS);
            });
            assertThat(damageContext.distance()).isOne();
        });

        verify((Removable) actor).remove();
    }
}
