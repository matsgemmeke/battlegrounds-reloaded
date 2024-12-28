package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExplosionEffectTest {

    private static final boolean BREAK_BLOCKS = false;
    private static final boolean SET_FIRE = false;
    private static final double LONG_RANGE_DAMAGE = 25.0;
    private static final double LONG_RANGE_DISTANCE = 10.0;
    private static final double MEDIUM_RANGE_DAMAGE = 75.0;
    private static final double MEDIUM_RANGE_DISTANCE = 5.0;
    private static final double SHORT_RANGE_DAMAGE = 150.0;
    private static final double SHORT_RANGE_DISTANCE = 2.5;
    private static final float POWER = 1.0F;

    private ExplosionProperties properties;
    private ItemEffectActivation effectActivation;
    private RangeProfile rangeProfile;
    private TargetFinder targetFinder;

    @BeforeEach
    public void setUp() {
        properties = new ExplosionProperties(POWER, SET_FIRE, BREAK_BLOCKS);
        effectActivation = mock(ItemEffectActivation.class);
        rangeProfile = new RangeProfile(LONG_RANGE_DAMAGE, LONG_RANGE_DISTANCE, MEDIUM_RANGE_DAMAGE, MEDIUM_RANGE_DISTANCE, SHORT_RANGE_DAMAGE, SHORT_RANGE_DISTANCE);
        targetFinder = mock(TargetFinder.class);
    }

    @Test
    public void activateInstantlyPerformsEffectIfContextIsNotNull() {
        Player player = mock(Player.class);
        World sourceWorld = mock(World.class);
        Location sourceLocation = new Location(sourceWorld, 1, 1, 1);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(player);

        EffectSource source = mock(EffectSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(sourceWorld);

        ItemEffectContext context = new ItemEffectContext(holder, source);

        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, rangeProfile, targetFinder);
        effect.prime(context);
        effect.activateInstantly();

        verify(source).remove();
        verify(sourceWorld).createExplosion(sourceLocation, POWER, SET_FIRE, BREAK_BLOCKS, player);
    }

    @Test
    public void deployChangesTheSourceOfTheContext() {
        ItemHolder holder = mock(ItemHolder.class);
        EffectSource oldSource = mock(EffectSource.class);
        EffectSource newSource = mock(EffectSource.class);

        ItemEffectContext context = new ItemEffectContext(holder, oldSource);

        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, rangeProfile, targetFinder);
        effect.prime(context);
        effect.deploy(newSource);

        assertEquals(newSource, context.getSource());
    }

    @Test
    public void deployDoesNothingIfEffectIsNotPrimedYet() {
        EffectSource source = mock(EffectSource.class);

        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, rangeProfile, targetFinder);

        // This method currently has no side effects to verify, refactor later?
        assertDoesNotThrow(() -> effect.deploy(source));
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseIfEffectIsNotPrimed() {
        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, rangeProfile, targetFinder);
        boolean awaitingDeployment = effect.isAwaitingDeployment();

        assertFalse(awaitingDeployment);
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseIfContextSourceIsDeployed() {
        EffectSource source = mock(EffectSource.class);
        when(source.isDeployed()).thenReturn(true);

        ItemHolder holder = mock(ItemHolder.class);
        ItemEffectContext context = new ItemEffectContext(holder, source);

        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, rangeProfile, targetFinder);
        effect.prime(context);
        boolean awaitingDeployment = effect.isAwaitingDeployment();

        assertFalse(awaitingDeployment);
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseIfContextSourceIsNotDeployed() {
        EffectSource source = mock(EffectSource.class);
        when(source.isDeployed()).thenReturn(false);

        ItemHolder holder = mock(ItemHolder.class);
        ItemEffectContext context = new ItemEffectContext(holder, source);

        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, rangeProfile, targetFinder);
        effect.prime(context);
        boolean awaitingDeployment = effect.isAwaitingDeployment();

        assertTrue(awaitingDeployment);
    }

    @Test
    public void isPrimedReturnsFalseIfEffectWasNotPrimed() {
        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, rangeProfile, targetFinder);
        boolean primed = effect.isPrimed();

        assertFalse(primed);
    }

    @Test
    public void isPrimedReturnsTrueIfEffectWasPrimed() {
        ItemHolder holder = mock(ItemHolder.class);
        EffectSource source = mock(EffectSource.class);
        ItemEffectContext context = new ItemEffectContext(holder, source);

        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, rangeProfile, targetFinder);
        effect.prime(context);
        boolean primed = effect.isPrimed();

        assertTrue(primed);
    }

    @Test
    public void primePrimesEffectActivationOnce() {
        ItemHolder holder = mock(ItemHolder.class);
        EffectSource source = mock(EffectSource.class);
        ItemEffectContext context = new ItemEffectContext(holder, source);

        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, rangeProfile, targetFinder);
        effect.prime(context);
        effect.prime(context);

        verify(effectActivation).prime(eq(context), any(Procedure.class));
    }

    @Test
    public void primesCreatesExplosionAtSourceLocationAndDamagesAllEntitiesInsideTheLongRangeDistance() {
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 1, 1, 1);
        Location targetLocation = new Location(world, 8, 1, 1);

        Player holderEntity = mock(Player.class);
        when(holderEntity.getLocation()).thenReturn(sourceLocation);
        when(holderEntity.getWorld()).thenReturn(world);

        Player targetEntity = mock(Player.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);
        when(targetEntity.getWorld()).thenReturn(world);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(holderEntity);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        EffectSource source = mock(EffectSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        when(targetFinder.findTargets(holder, sourceLocation, LONG_RANGE_DISTANCE)).thenReturn(List.of(holder, target));

        ExplosionProperties properties = new ExplosionProperties(POWER, SET_FIRE, BREAK_BLOCKS);
        ItemEffectContext context = new ItemEffectContext(holder, source);

        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, rangeProfile, targetFinder);
        effect.prime(context);

        ArgumentCaptor<Procedure> procedureCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(effectActivation).prime(eq(context), procedureCaptor.capture());

        procedureCaptor.getValue().apply();

        verify(source).remove();
        verify(holder).damage(SHORT_RANGE_DAMAGE);
        verify(target).damage(LONG_RANGE_DAMAGE);
    }
}
