package nl.matsgemmeke.battlegrounds.item.effect.flash;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.PotionEffectProperties;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

public class FlashEffectTest {

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

    private Entity entity;
    private FlashProperties properties;
    private ItemEffectSource source;
    private TargetFinder targetFinder;
    private Trigger trigger;

    @BeforeEach
    public void setUp() {
        entity = mock(Entity.class);
        source = mock(ItemEffectSource.class);
        targetFinder = mock(TargetFinder.class);
        trigger = mock(Trigger.class);

        PotionEffectProperties potionEffect = new PotionEffectProperties(POTION_EFFECT_DURATION, POTION_EFFECT_AMPLIFIER, POTION_EFFECT_AMBIENT, POTION_EFFECT_PARTICLES, POTION_EFFECT_ICON);

        properties = new FlashProperties(potionEffect, RANGE, EXPLOSION_POWER, EXPLOSION_BREAK_BLOCKS, EXPLOSION_SET_FIRE);
    }

    @Test
    public void primePerformsEffectAndAppliesBlindnessPotionEffectToAllTargetsInsideTheLongRangeDistance() {
        UUID entityId = UUID.randomUUID();
        World world = mock(World.class);
        Player player = mock(Player.class);
        ItemEffectContext context = new ItemEffectContext(entity, source, INITIATION_LOCATION);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(player);

        when(entity.getUniqueId()).thenReturn(entityId);
        when(source.getLocation()).thenReturn(SOURCE_LOCATION);
        when(source.getWorld()).thenReturn(world);
        when(targetFinder.findTargets(entityId, SOURCE_LOCATION, RANGE)).thenReturn(List.of(target));

        FlashEffect effect = new FlashEffect(targetFinder, properties);
        effect.addTrigger(trigger);
        effect.prime(context);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        ArgumentCaptor<PotionEffect> potionEffectCaptor = ArgumentCaptor.forClass(PotionEffect.class);
        verify(player).addPotionEffect(potionEffectCaptor.capture());

        PotionEffect potionEffect = potionEffectCaptor.getValue();

        assertEquals(PotionEffectType.BLINDNESS, potionEffect.getType());
        assertEquals(POTION_EFFECT_DURATION, potionEffect.getDuration());
        assertEquals(POTION_EFFECT_AMPLIFIER, potionEffect.getAmplifier());
        assertEquals(POTION_EFFECT_AMBIENT, potionEffect.isAmbient());
        assertEquals(POTION_EFFECT_PARTICLES, potionEffect.hasParticles());
        assertEquals(POTION_EFFECT_ICON, potionEffect.hasIcon());

        verify(source).remove();
        verify(world).createExplosion(SOURCE_LOCATION, EXPLOSION_POWER, EXPLOSION_SET_FIRE, EXPLOSION_BREAK_BLOCKS, entity);
    }

    @NotNull
    private static Stream<Arguments> potionEffectScenarios() {
        return Stream.of(
                arguments(new Object[] { null }),
                arguments(new PotionEffect(PotionEffectType.BLINDNESS, 1, 1))
        );
    }

    @ParameterizedTest
    @MethodSource("potionEffectScenarios")
    public void resetDoesNotRemovePotionEffectFromTarget(PotionEffect potionEffect) {
        UUID entityId = UUID.randomUUID();
        World world = mock(World.class);
        ItemEffectContext context = new ItemEffectContext(entity, source, INITIATION_LOCATION);

        Player player = mock(Player.class);
        when(player.getPotionEffect(PotionEffectType.BLINDNESS)).thenReturn(potionEffect);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(player);

        when(entity.getUniqueId()).thenReturn(entityId);
        when(source.getLocation()).thenReturn(SOURCE_LOCATION);
        when(source.getWorld()).thenReturn(world);
        when(targetFinder.findTargets(entityId, SOURCE_LOCATION, RANGE)).thenReturn(List.of(target));

        FlashEffect effect = new FlashEffect(targetFinder, properties);
        effect.addTrigger(trigger);
        effect.prime(context);

        ArgumentCaptor<TriggerObserver> triggerObserverArgumentCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverArgumentCaptor.capture());

        triggerObserverArgumentCaptor.getValue().onActivate();
        effect.reset();

        verify(player, never()).removePotionEffect(any(PotionEffectType.class));
    }

    @Test
    public void resetRemovesPotionEffectFromTargetIfItStillHasThePotionEffect() {
        UUID entityId = UUID.randomUUID();
        World world = mock(World.class);
        Player player = mock(Player.class);
        ItemEffectContext context = new ItemEffectContext(entity, source, INITIATION_LOCATION);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(player);

        when(entity.getUniqueId()).thenReturn(entityId);
        when(source.getLocation()).thenReturn(SOURCE_LOCATION);
        when(source.getWorld()).thenReturn(world);

        when(targetFinder.findTargets(entityId, SOURCE_LOCATION, RANGE)).thenReturn(List.of(target));

        FlashEffect effect = new FlashEffect(targetFinder, properties);
        effect.addTrigger(trigger);
        effect.prime(context);

        ArgumentCaptor<TriggerObserver> triggerObserverArgumentCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverArgumentCaptor.capture());

        triggerObserverArgumentCaptor.getValue().onActivate();

        ArgumentCaptor<PotionEffect> potionEffectCaptor = ArgumentCaptor.forClass(PotionEffect.class);
        verify(player).addPotionEffect(potionEffectCaptor.capture());

        when(player.getPotionEffect(PotionEffectType.BLINDNESS)).thenReturn(potionEffectCaptor.getValue());

        effect.reset();

        verify(player).removePotionEffect(PotionEffectType.BLINDNESS);
    }
}
