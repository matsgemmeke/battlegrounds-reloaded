package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageType;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.effect.ExplosionAttributorRegistry;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.targeting.TargetQuery;
import nl.matsgemmeke.battlegrounds.game.component.targeting.condition.ProximityTargetCondition;
import nl.matsgemmeke.battlegrounds.game.damage.*;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.actor.Removable;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExplosionEffectPerformanceTest {

    // Property variables
    private static final boolean BREAK_BLOCKS = false;
    private static final boolean SET_FIRE = false;
    private static final double LONG_RANGE_DAMAGE = 25.0;
    private static final double LONG_RANGE_DISTANCE = 10.0;
    private static final double MEDIUM_RANGE_DAMAGE = 75.0;
    private static final double MEDIUM_RANGE_DISTANCE = 5.0;
    private static final double SHORT_RANGE_DAMAGE = 150.0;
    private static final double SHORT_RANGE_DISTANCE = 2.5;
    private static final float POWER = 1.0F;
    private static final RangeProfile RANGE_PROFILE = new RangeProfile(SHORT_RANGE_DAMAGE, SHORT_RANGE_DISTANCE, MEDIUM_RANGE_DAMAGE, MEDIUM_RANGE_DISTANCE, LONG_RANGE_DAMAGE, LONG_RANGE_DISTANCE);
    private static final ExplosionProperties PROPERTIES = new ExplosionProperties(RANGE_PROFILE, POWER, SET_FIRE, BREAK_BLOCKS);

    // Context variables
    private static final Location STARTING_LOCATION = new Location(null, 0, 0, 0);
    private static final String ITEM_NAME = "Test Item";

    private static final UUID DAMAGE_SOURCE_ID = UUID.randomUUID();
    private static final UUID ARMOR_STAND_ID = UUID.randomUUID();
    private static final CollisionResult COLLISION_RESULT = new CollisionResult(null, null, null);

    @Mock
    private DamageProcessor damageProcessor;
    @Mock
    private ExplosionAttributorRegistry explosionAttributorRegistry;
    @Mock
    private TargetFinder targetFinder;
    @Mock
    private World world;

    private ExplosionEffectPerformance performance;

    @BeforeEach
    void setUp() {
        performance = new ExplosionEffectPerformance(damageProcessor, explosionAttributorRegistry, targetFinder, PROPERTIES);
    }

    @Test
    @DisplayName("isPerforming returns false even after starting performance")
    void isPerforming_returnsFalse() {
        ArmorStand armorStand = mock(ArmorStand.class);
        DamageSource damageSource = mock(DamageSource.class);
        Location actorLocation = new Location(world, 1, 1, 1);

        Actor actor = mock(Actor.class, withSettings().extraInterfaces(Removable.class));
        when(actor.getLocation()).thenReturn(actorLocation);
        when(actor.getWorld()).thenReturn(world);

        when(world.spawn(actorLocation, ArmorStand.class)).thenReturn(armorStand);

        ItemEffectContext context = new ItemEffectContext(ITEM_NAME, COLLISION_RESULT, damageSource, STARTING_LOCATION, actor);

        performance.setContext(context);
        performance.start();
        boolean performing = performance.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    @DisplayName("perform creates explosion with an armor stand attributor and damage all entities inside the long range")
    void perform_createsExplosionAndDamagesEntities() {
        Location actorLocation = new Location(world, 1, 1, 1);
        Location damageTargetLocation = new Location(world, 8, 1, 1);

        DamageSource damageSource = mock(DamageSource.class);
        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);

        ArmorStand armorStand = mock(ArmorStand.class);
        when(armorStand.getUniqueId()).thenReturn(ARMOR_STAND_ID);

        Actor actor = mock(Actor.class, withSettings().extraInterfaces(Removable.class));
        when(actor.getLocation()).thenReturn(actorLocation);
        when(actor.getWorld()).thenReturn(world);

        ItemEffectContext context = new ItemEffectContext(ITEM_NAME, COLLISION_RESULT, damageSource, STARTING_LOCATION, actor);

        DamageTarget damageTarget = mock(GameEntity.class);
        when(damageTarget.getLocation()).thenReturn(damageTargetLocation);

        when(targetFinder.findTargets(any(TargetQuery.class))).thenReturn(List.of(damageTarget));
        when(world.spawn(actorLocation, ArmorStand.class)).thenReturn(armorStand);

        performance.setContext(context);
        performance.start();

        ArgumentCaptor<TargetQuery> targetQueryCaptor = ArgumentCaptor.forClass(TargetQuery.class);
        verify(targetFinder).findTargets(targetQueryCaptor.capture());

        ArgumentCaptor<DamageContext> damageContextCaptor = ArgumentCaptor.forClass(DamageContext.class);
        verify(damageProcessor).processDamage(damageContextCaptor.capture());

        assertThat(targetQueryCaptor.getValue()).satisfies(targetQuery -> {
           assertThat(targetQuery.getUniqueId()).hasValue(DAMAGE_SOURCE_ID);
           assertThat(targetQuery.getLocation()).hasValue(actorLocation);
           assertThat(targetQuery.getConditions())
                   .hasSize(1)
                   .hasOnlyElementsOfType(ProximityTargetCondition.class);
           assertThat(targetQuery.isEnemiesOnly()).isFalse();
        });

        assertThat(damageContextCaptor.getValue()).satisfies(damageContext -> {
            assertThat(damageContext.source()).isEqualTo(damageSource);
            assertThat(damageContext.target()).isEqualTo(damageTarget);
            assertThat(damageContext.itemName()).isEqualTo(ITEM_NAME);
            assertThat(damageContext.damage()).satisfies(damage -> {
                assertThat(damage.type()).isEqualTo(DamageType.EXPLOSIVE_DAMAGE);
                assertThat(damage.amount()).isEqualTo(LONG_RANGE_DAMAGE);
            });
        });

        verify(world).createExplosion(actorLocation, POWER, SET_FIRE, BREAK_BLOCKS, armorStand);
        verify((Removable) actor).remove();
        verify(explosionAttributorRegistry).addAttributor(argThat(attributor -> attributor.entityId() == ARMOR_STAND_ID));
        verify(explosionAttributorRegistry).removeAttributor(argThat(attributor -> attributor.entityId() == ARMOR_STAND_ID));
        verify(armorStand).setInvisible(true);
        verify(armorStand).setInvulnerable(true);
        verify(armorStand).setMarker(true);
        verify(armorStand).remove();
    }
}
