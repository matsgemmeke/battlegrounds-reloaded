package nl.matsgemmeke.battlegrounds.item.effect.flash;

import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.PotionEffectProperties;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private TargetFinder targetFinder;

    @BeforeEach
    public void setUp() {
        targetFinder = mock(TargetFinder.class);

        PotionEffectProperties potionEffect = new PotionEffectProperties(POTION_EFFECT_DURATION, POTION_EFFECT_AMPLIFIER, POTION_EFFECT_AMBIENT, POTION_EFFECT_PARTICLES, POTION_EFFECT_ICON);

        properties = new FlashProperties(potionEffect, RANGE, EXPLOSION_POWER, EXPLOSION_BREAK_BLOCKS, EXPLOSION_SET_FIRE);
    }

    @Test
    public void activateCreatesExplosionAtSourceLocation() {
        LivingEntity holderEntity = mock(LivingEntity.class);
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 1, 1, 1);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(holderEntity);

        EffectSource source = mock(EffectSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        ItemEffectContext context = new ItemEffectContext(holder, source);

        FlashEffect effect = new FlashEffect(properties, targetFinder);
        effect.activate(context);

        verify(source).remove();
        verify(world).createExplosion(sourceLocation, EXPLOSION_POWER, EXPLOSION_SET_FIRE, EXPLOSION_BREAK_BLOCKS, holderEntity);
    }

    @Test
    public void activateAppliesBlindnessPotionEffectToAllTargetsInsideTheLongRangeDistance() {
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 1, 1, 1);

        LivingEntity entity = mock(LivingEntity.class);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(entity);

        EffectSource source = mock(EffectSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        ItemEffectContext context = new ItemEffectContext(holder, source);

        when(targetFinder.findTargets(holder, sourceLocation, RANGE)).thenReturn(List.of(holder));

        FlashEffect effect = new FlashEffect(properties, targetFinder);
        effect.activate(context);

        ArgumentCaptor<PotionEffect> potionEffectCaptor = ArgumentCaptor.forClass(PotionEffect.class);
        verify(entity).addPotionEffect(potionEffectCaptor.capture());

        PotionEffect potionEffect = potionEffectCaptor.getValue();

        assertEquals(PotionEffectType.BLINDNESS, potionEffect.getType());
        assertEquals(POTION_EFFECT_DURATION, potionEffect.getDuration());
        assertEquals(POTION_EFFECT_AMPLIFIER, potionEffect.getAmplifier());
        assertEquals(POTION_EFFECT_AMBIENT, potionEffect.isAmbient());
        assertEquals(POTION_EFFECT_PARTICLES, potionEffect.hasParticles());
        assertEquals(POTION_EFFECT_ICON, potionEffect.hasIcon());

        verify(source).remove();
    }
}
