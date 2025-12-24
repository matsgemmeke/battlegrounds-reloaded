package nl.matsgemmeke.battlegrounds.item.effect.flash;

import nl.matsgemmeke.battlegrounds.entity.PotionEffectReceiver;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.PotionEffectProperties;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.source.Removable;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlashEffectPerformanceTest {

    private static final boolean EXPLOSION_BREAK_BLOCKS = false;
    private static final boolean EXPLOSION_SET_FIRE = false;
    private static final boolean POTION_EFFECT_AMBIENT = true;
    private static final boolean POTION_EFFECT_ICON = false;
    private static final boolean POTION_EFFECT_PARTICLES = true;
    private static final double RANGE = 5.0;
    private static final float EXPLOSION_POWER = 1.0f;
    private static final int POTION_EFFECT_AMPLIFIER = 0;
    private static final int POTION_EFFECT_DURATION = 100;
    private static final Location INITIATION_LOCATION = new Location(null, 0, 0, 0);
    private static final Location EFFECT_SOURCE_LOCATION = new Location(null, 1, 1, 1);
    private static final PotionEffectProperties POTION_EFFECT_PROPERTIES = new PotionEffectProperties(POTION_EFFECT_DURATION, POTION_EFFECT_AMPLIFIER, POTION_EFFECT_AMBIENT, POTION_EFFECT_PARTICLES, POTION_EFFECT_ICON);
    private static final FlashProperties FLASH_PROPERTIES = new FlashProperties(POTION_EFFECT_PROPERTIES, RANGE, EXPLOSION_POWER, EXPLOSION_BREAK_BLOCKS, EXPLOSION_SET_FIRE);

    @Mock
    private DamageSource damageSource;
    @Mock(extraInterfaces = Removable.class)
    private ItemEffectSource effectSource;
    @Mock
    private Scheduler scheduler;
    @Mock
    private TargetFinder targetFinder;

    private FlashEffectPerformance performance;

    @BeforeEach
    void setUp() {
        performance = new FlashEffectPerformance(scheduler, targetFinder, FLASH_PROPERTIES);
    }

    @Test
    void performPerformsEffectAndAppliesBlindnessPotionEffectToAllTargetsInsideLongRangeDistance() {
        World world = mock(World.class);
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, INITIATION_LOCATION);
        Schedule cancelSchedule = mock(Schedule.class);
        PotionEffectReceiver target = mock(PotionEffectReceiver.class);

        when(effectSource.getLocation()).thenReturn(EFFECT_SOURCE_LOCATION);
        when(effectSource.getWorld()).thenReturn(world);
        when(scheduler.createSingleRunSchedule(POTION_EFFECT_DURATION)).thenReturn(cancelSchedule);
        when(targetFinder.findPotionEffectReceivers(EFFECT_SOURCE_LOCATION, RANGE)).thenReturn(List.of(target));

        performance.perform(context);

        ArgumentCaptor<PotionEffect> potionEffectCaptor = ArgumentCaptor.forClass(PotionEffect.class);
        verify(target).addPotionEffect(potionEffectCaptor.capture());

        PotionEffect potionEffect = potionEffectCaptor.getValue();

        assertThat(potionEffect.getType()).isEqualTo(PotionEffectType.BLINDNESS);
        assertThat(potionEffect.getDuration()).isEqualTo(POTION_EFFECT_DURATION);
        assertThat(potionEffect.getAmplifier()).isEqualTo(POTION_EFFECT_AMPLIFIER);
        assertThat(potionEffect.isAmbient()).isEqualTo(POTION_EFFECT_AMBIENT);
        assertThat(potionEffect.hasParticles()).isEqualTo(POTION_EFFECT_PARTICLES);
        assertThat(potionEffect.hasIcon()).isEqualTo(POTION_EFFECT_ICON);

        verify(world).createExplosion(EFFECT_SOURCE_LOCATION, EXPLOSION_POWER, EXPLOSION_SET_FIRE, EXPLOSION_BREAK_BLOCKS);
        verify(cancelSchedule).start();
        verify((Removable) effectSource).remove();
    }

    static Stream<Arguments> potionEffectScenarios() {
        return Stream.of(
                arguments(new Object[] { null }),
                arguments(new PotionEffect(PotionEffectType.BLINDNESS, 1, 1))
        );
    }

    @ParameterizedTest
    @MethodSource("potionEffectScenarios")
    void rollbackDoesNotRemovePotionEffectFromEntities(PotionEffect potionEffect) {
        World world = mock(World.class);
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, INITIATION_LOCATION);

        PotionEffectReceiver target = mock(PotionEffectReceiver.class);
        when(target.getPotionEffect(PotionEffectType.BLINDNESS)).thenReturn(Optional.ofNullable(potionEffect));

        Schedule cancelSchedule = mock(Schedule.class);
        when(cancelSchedule.isRunning()).thenReturn(true);

        when(effectSource.getLocation()).thenReturn(EFFECT_SOURCE_LOCATION);
        when(effectSource.getWorld()).thenReturn(world);
        when(scheduler.createSingleRunSchedule(POTION_EFFECT_DURATION)).thenReturn(cancelSchedule);
        when(targetFinder.findPotionEffectReceivers(EFFECT_SOURCE_LOCATION, RANGE)).thenReturn(List.of(target));

        performance.perform(context);
        performance.rollback();

        verify(target, never()).removePotionEffect(any(PotionEffectType.class));
    }

    @Test
    void rollbackRemovesAppliedPotionEffectsFromEntities() {
        World world = mock(World.class);
        ItemEffectContext context = new ItemEffectContext(damageSource, effectSource, INITIATION_LOCATION);
        PotionEffectReceiver target = mock(PotionEffectReceiver.class);

        Schedule cancelSchedule = mock(Schedule.class);
        when(cancelSchedule.isRunning()).thenReturn(true);

        when(effectSource.getLocation()).thenReturn(EFFECT_SOURCE_LOCATION);
        when(effectSource.getWorld()).thenReturn(world);
        when(scheduler.createSingleRunSchedule(POTION_EFFECT_DURATION)).thenReturn(cancelSchedule);
        when(targetFinder.findPotionEffectReceivers(EFFECT_SOURCE_LOCATION, RANGE)).thenReturn(List.of(target));

        performance.perform(context);

        ArgumentCaptor<PotionEffect> potionEffectCaptor = ArgumentCaptor.forClass(PotionEffect.class);
        verify(target).addPotionEffect(potionEffectCaptor.capture());

        when(target.getPotionEffect(PotionEffectType.BLINDNESS)).thenReturn(Optional.of(potionEffectCaptor.getValue()));

        performance.rollback();

        verify(target).removePotionEffect(PotionEffectType.BLINDNESS);
        verify(cancelSchedule).stop();
    }
}
