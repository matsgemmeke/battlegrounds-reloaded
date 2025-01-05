package nl.matsgemmeke.battlegrounds.item.effect.flash;

import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.PotionEffectProperties;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.Location;
import org.bukkit.World;
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

    private FlashProperties properties;
    private ItemEffectActivation effectActivation;
    private TargetFinder targetFinder;

    @BeforeEach
    public void setUp() {
        effectActivation = mock(ItemEffectActivation.class);
        targetFinder = mock(TargetFinder.class);

        PotionEffectProperties potionEffect = new PotionEffectProperties(POTION_EFFECT_DURATION, POTION_EFFECT_AMPLIFIER, POTION_EFFECT_AMBIENT, POTION_EFFECT_PARTICLES, POTION_EFFECT_ICON);

        properties = new FlashProperties(potionEffect, RANGE, EXPLOSION_POWER, EXPLOSION_BREAK_BLOCKS, EXPLOSION_SET_FIRE);
    }

    @Test
    public void primePerformsEffectAndAppliesBlindnessPotionEffectToAllTargetsInsideTheLongRangeDistance() {
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 1, 1, 1);
        Player player = mock(Player.class);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(player);

        ItemEffectSource source = mock(ItemEffectSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        ItemEffectContext context = new ItemEffectContext(holder, source);

        when(targetFinder.findTargets(holder, sourceLocation, RANGE)).thenReturn(List.of(holder));

        FlashEffect effect = new FlashEffect(effectActivation, properties, targetFinder);
        effect.prime(context);

        ArgumentCaptor<Procedure> procedureArgumentCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(effectActivation).prime(eq(context), procedureArgumentCaptor.capture());

        procedureArgumentCaptor.getValue().apply();

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
        verify(world).createExplosion(sourceLocation, EXPLOSION_POWER, EXPLOSION_SET_FIRE, EXPLOSION_BREAK_BLOCKS, player);
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
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 1, 1, 1);

        Player player = mock(Player.class);
        when(player.getPotionEffect(PotionEffectType.BLINDNESS)).thenReturn(potionEffect);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(player);

        ItemEffectSource source = mock(ItemEffectSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        ItemEffectContext context = new ItemEffectContext(holder, source);

        when(targetFinder.findTargets(holder, sourceLocation, RANGE)).thenReturn(List.of(holder));

        FlashEffect effect = new FlashEffect(effectActivation, properties, targetFinder);
        effect.prime(context);

        ArgumentCaptor<Procedure> procedureArgumentCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(effectActivation).prime(eq(context), procedureArgumentCaptor.capture());

        procedureArgumentCaptor.getValue().apply();
        effect.reset();

        verify(player, never()).removePotionEffect(any(PotionEffectType.class));
    }

    @Test
    public void resetRemovesPotionEffectFromTargetIfItStillHasThePotionEffect() {
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 1, 1, 1);
        Player player = mock(Player.class);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(player);

        ItemEffectSource source = mock(ItemEffectSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        ItemEffectContext context = new ItemEffectContext(holder, source);

        when(targetFinder.findTargets(holder, sourceLocation, RANGE)).thenReturn(List.of(holder));

        FlashEffect effect = new FlashEffect(effectActivation, properties, targetFinder);
        effect.prime(context);

        ArgumentCaptor<Procedure> procedureArgumentCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(effectActivation).prime(eq(context), procedureArgumentCaptor.capture());

        procedureArgumentCaptor.getValue().apply();

        ArgumentCaptor<PotionEffect> potionEffectCaptor = ArgumentCaptor.forClass(PotionEffect.class);
        verify(player).addPotionEffect(potionEffectCaptor.capture());

        when(player.getPotionEffect(PotionEffectType.BLINDNESS)).thenReturn(potionEffectCaptor.getValue());

        effect.reset();

        verify(player).removePotionEffect(PotionEffectType.BLINDNESS);
    }
}
