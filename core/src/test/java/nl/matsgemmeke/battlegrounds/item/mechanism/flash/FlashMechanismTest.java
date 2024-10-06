package nl.matsgemmeke.battlegrounds.item.mechanism.flash;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class FlashMechanismTest {

    private static final boolean EFFECT_AMBIENT = true;
    private static final boolean EFFECT_ICON = false;
    private static final boolean EFFECT_PARTICLES = true;
    private static final boolean EXPLOSION_BREAK_BLOCKS = false;
    private static final boolean EXPLOSION_SET_FIRE = false;
    private static final double RANGE = 5.0;
    private static final float EXPLOSION_POWER = 1.0f;
    private static final int EFFECT_AMPLIFIER = 0;
    private static final int EFFECT_DURATION = 100;

    private TargetFinder targetFinder;

    @Before
    public void setUp() {
        targetFinder = mock(TargetFinder.class);
    }

    @Test
    public void shouldCreateFlashEffectAtHolderLocationAndRemoveItemStack() {
        World world = mock(World.class);
        Location holderLocation = new Location(world, 1, 1, 1);

        Entity entity = mock(Entity.class);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(entity);
        when(holder.getLocation()).thenReturn(holderLocation);
        when(holder.getWorld()).thenReturn(world);

        FlashSettings settings = new FlashSettings(RANGE, EFFECT_DURATION, EFFECT_AMPLIFIER, EFFECT_AMBIENT, EFFECT_PARTICLES, EFFECT_ICON, EXPLOSION_POWER, EXPLOSION_BREAK_BLOCKS, EXPLOSION_SET_FIRE);
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        FlashMechanism mechanism = new FlashMechanism(settings, targetFinder);
        mechanism.activate(holder, itemStack);

        verify(holder).removeItem(itemStack);
        verify(world).createExplosion(holderLocation, EXPLOSION_POWER, EXPLOSION_SET_FIRE, EXPLOSION_BREAK_BLOCKS, entity);
    }

    @Test
    public void shouldCreateFlashEffectAtDeployedObjectLocation() {
        World world = mock(World.class);
        Location objectLocation = new Location(world, 1, 1, 1);

        Entity entity = mock(Entity.class);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(entity);

        Deployable object = mock(Deployable.class);
        when(object.getLocation()).thenReturn(objectLocation);
        when(object.getWorld()).thenReturn(world);

        FlashSettings settings = new FlashSettings(RANGE, EFFECT_DURATION, EFFECT_AMPLIFIER, EFFECT_AMBIENT, EFFECT_PARTICLES, EFFECT_ICON, EXPLOSION_POWER, EXPLOSION_BREAK_BLOCKS, EXPLOSION_SET_FIRE);

        FlashMechanism mechanism = new FlashMechanism(settings, targetFinder);
        mechanism.activate(holder, object);

        verify(world).createExplosion(objectLocation, EXPLOSION_POWER, EXPLOSION_SET_FIRE, EXPLOSION_BREAK_BLOCKS, entity);
    }

    @Test
    public void shouldApplyBlindnessPotionEffectToAllLivingEntitiesInsideTheLongRangeDistance() {
        World world = mock(World.class);
        Location objectLocation = new Location(world, 1, 1, 1);

        LivingEntity livingEntity = mock(LivingEntity.class);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(livingEntity);

        Deployable object = mock(Deployable.class);
        when(object.getLocation()).thenReturn(objectLocation);
        when(object.getWorld()).thenReturn(world);

        Arrow arrow = mock(Arrow.class);

        GameEntity gameEntity = mock(GameEntity.class);
        when(gameEntity.getEntity()).thenReturn(arrow);

        when(targetFinder.findTargets(holder, objectLocation, RANGE)).thenReturn(List.of(holder, gameEntity));

        FlashSettings settings = new FlashSettings(RANGE, EFFECT_DURATION, EFFECT_AMPLIFIER, EFFECT_AMBIENT, EFFECT_PARTICLES, EFFECT_ICON, EXPLOSION_POWER, EXPLOSION_BREAK_BLOCKS, EXPLOSION_SET_FIRE);

        FlashMechanism mechanism = new FlashMechanism(settings, targetFinder);
        mechanism.activate(holder, object);

        ArgumentCaptor<PotionEffect> potionEffectCaptor = ArgumentCaptor.forClass(PotionEffect.class);
        verify(livingEntity).addPotionEffect(potionEffectCaptor.capture());

        PotionEffect potionEffect = potionEffectCaptor.getValue();

        assertEquals(PotionEffectType.BLINDNESS, potionEffect.getType());
        assertEquals(EFFECT_DURATION, potionEffect.getDuration());
        assertEquals(EFFECT_AMPLIFIER, potionEffect.getAmplifier());
        assertEquals(EFFECT_AMBIENT, potionEffect.isAmbient());
        assertEquals(EFFECT_PARTICLES, potionEffect.hasParticles());
        assertEquals(EFFECT_ICON, potionEffect.hasIcon());

        verifyNoInteractions(arrow);
    }
}
