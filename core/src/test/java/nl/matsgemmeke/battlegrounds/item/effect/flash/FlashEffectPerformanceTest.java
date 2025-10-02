package nl.matsgemmeke.battlegrounds.item.effect.flash;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.PotionEffectProperties;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
import java.util.UUID;
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
    private static final Location SOURCE_LOCATION = new Location(null, 1, 1, 1);
    private static final PotionEffectProperties POTION_EFFECT_PROPERTIES = new PotionEffectProperties(POTION_EFFECT_DURATION, POTION_EFFECT_AMPLIFIER, POTION_EFFECT_AMBIENT, POTION_EFFECT_PARTICLES, POTION_EFFECT_ICON);
    private static final FlashProperties FLASH_PROPERTIES = new FlashProperties(POTION_EFFECT_PROPERTIES, RANGE, EXPLOSION_POWER, EXPLOSION_BREAK_BLOCKS, EXPLOSION_SET_FIRE);

    @Mock
    private Entity entity;
    @Mock
    private ItemEffectSource source;
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
        UUID entityId = UUID.randomUUID();
        World world = mock(World.class);
        Player player = mock(Player.class);
        ItemEffectContext context = new ItemEffectContext(entity, source, INITIATION_LOCATION);
        Schedule cancelSchedule = mock(Schedule.class);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(player);

        when(entity.getUniqueId()).thenReturn(entityId);
        when(scheduler.createSingleRunSchedule(POTION_EFFECT_DURATION)).thenReturn(cancelSchedule);
        when(source.getLocation()).thenReturn(SOURCE_LOCATION);
        when(source.getWorld()).thenReturn(world);
        when(targetFinder.findTargets(entityId, SOURCE_LOCATION, RANGE)).thenReturn(List.of(target));

        performance.perform(context);

        ArgumentCaptor<PotionEffect> potionEffectCaptor = ArgumentCaptor.forClass(PotionEffect.class);
        verify(player).addPotionEffect(potionEffectCaptor.capture());

        PotionEffect potionEffect = potionEffectCaptor.getValue();

        assertThat(potionEffect.getType()).isEqualTo(PotionEffectType.BLINDNESS);
        assertThat(potionEffect.getDuration()).isEqualTo(POTION_EFFECT_DURATION);
        assertThat(potionEffect.getAmplifier()).isEqualTo(POTION_EFFECT_AMPLIFIER);
        assertThat(potionEffect.isAmbient()).isEqualTo(POTION_EFFECT_AMBIENT);
        assertThat(potionEffect.hasParticles()).isEqualTo(POTION_EFFECT_PARTICLES);
        assertThat(potionEffect.hasIcon()).isEqualTo(POTION_EFFECT_ICON);

        verify(source).remove();
        verify(world).createExplosion(SOURCE_LOCATION, EXPLOSION_POWER, EXPLOSION_SET_FIRE, EXPLOSION_BREAK_BLOCKS, entity);
        verify(cancelSchedule).start();
    }

    @Test
    void cancelDoesNothingWhenNotPerforming() {
        TriggerRun triggerRun = mock(TriggerRun.class);

        performance.addTriggerRun(triggerRun);
        performance.cancel();

        verify(triggerRun, never()).cancel();
    }

    static Stream<Arguments> potionEffectScenarios() {
        return Stream.of(
                arguments(new Object[] { null }),
                arguments(new PotionEffect(PotionEffectType.BLINDNESS, 1, 1))
        );
    }

    @ParameterizedTest
    @MethodSource("potionEffectScenarios")
    void cancelDoesNotRemovePotionEffectFromEntities(PotionEffect potionEffect) {
        UUID entityId = UUID.randomUUID();
        World world = mock(World.class);
        ItemEffectContext context = new ItemEffectContext(entity, source, INITIATION_LOCATION);

        Player player = mock(Player.class);
        when(player.getPotionEffect(PotionEffectType.BLINDNESS)).thenReturn(potionEffect);

        GameEntity gameEntity = mock(GameEntity.class);
        when(gameEntity.getEntity()).thenReturn(player);

        Schedule cancelSchedule = mock(Schedule.class);
        when(cancelSchedule.isRunning()).thenReturn(true);

        when(entity.getUniqueId()).thenReturn(entityId);
        when(scheduler.createSingleRunSchedule(POTION_EFFECT_DURATION)).thenReturn(cancelSchedule);
        when(source.getLocation()).thenReturn(SOURCE_LOCATION);
        when(source.getWorld()).thenReturn(world);
        when(targetFinder.findTargets(entityId, SOURCE_LOCATION, RANGE)).thenReturn(List.of(gameEntity));

        performance.perform(context);
        performance.cancel();

        verify(player, never()).removePotionEffect(any(PotionEffectType.class));
    }

    @Test
    void cancelRemovesAppliedPotionEffectsFromEntities() {
        UUID entityId = UUID.randomUUID();
        World world = mock(World.class);
        Player player = mock(Player.class);
        ItemEffectContext context = new ItemEffectContext(entity, source, INITIATION_LOCATION);

        GameEntity gameEntity = mock(GameEntity.class);
        when(gameEntity.getEntity()).thenReturn(player);

        Schedule cancelSchedule = mock(Schedule.class);
        when(cancelSchedule.isRunning()).thenReturn(true);

        when(entity.getUniqueId()).thenReturn(entityId);
        when(scheduler.createSingleRunSchedule(POTION_EFFECT_DURATION)).thenReturn(cancelSchedule);
        when(source.getLocation()).thenReturn(SOURCE_LOCATION);
        when(source.getWorld()).thenReturn(world);
        when(targetFinder.findTargets(entityId, SOURCE_LOCATION, RANGE)).thenReturn(List.of(gameEntity));

        performance.perform(context);

        ArgumentCaptor<PotionEffect> potionEffectCaptor = ArgumentCaptor.forClass(PotionEffect.class);
        verify(player).addPotionEffect(potionEffectCaptor.capture());

        when(player.getPotionEffect(PotionEffectType.BLINDNESS)).thenReturn(potionEffectCaptor.getValue());

        performance.cancel();

        verify(player).removePotionEffect(PotionEffectType.BLINDNESS);
        verify(cancelSchedule).stop();
    }
}
