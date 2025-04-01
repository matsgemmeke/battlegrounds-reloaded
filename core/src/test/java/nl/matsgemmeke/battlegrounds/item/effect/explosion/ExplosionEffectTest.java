package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

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

    private DamageProcessor damageProcessor;
    private Deployer deployer;
    private Entity entity;
    private ExplosionProperties properties;
    private ItemEffectActivation effectActivation;
    private ItemEffectContext context;
    private ItemEffectSource source;
    private RangeProfile rangeProfile;
    private TargetFinder targetFinder;

    @BeforeEach
    public void setUp() {
        damageProcessor = mock(DamageProcessor.class);
        properties = new ExplosionProperties(POWER, SET_FIRE, BREAK_BLOCKS);
        effectActivation = mock(ItemEffectActivation.class);
        rangeProfile = new RangeProfile(LONG_RANGE_DAMAGE, LONG_RANGE_DISTANCE, MEDIUM_RANGE_DAMAGE, MEDIUM_RANGE_DISTANCE, SHORT_RANGE_DAMAGE, SHORT_RANGE_DISTANCE);
        targetFinder = mock(TargetFinder.class);

        deployer = mock(Deployer.class);
        entity = mock(Entity.class);
        source = mock(ItemEffectSource.class);
        context = new ItemEffectContext(deployer, entity, source);
    }

    @Test
    public void activateInstantlyDoesNotPerformEffectIfItWasAlreadyActivated() {
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 1, 1, 1);

        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, damageProcessor, rangeProfile, targetFinder);
        effect.prime(context);

        ArgumentCaptor<Procedure> procedureCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(effectActivation).prime(eq(context), procedureCaptor.capture());

        procedureCaptor.getValue().apply();
        effect.activateInstantly();

        verify(effectActivation, never()).cancel();
    }

    @Test
    public void activateInstantlyPerformsEffectIfContextSourceExists() {
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 1, 1, 1);

        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, damageProcessor, rangeProfile, targetFinder);
        effect.prime(context);
        effect.activateInstantly();

        verify(source).remove();
        verify(world).createExplosion(sourceLocation, POWER, SET_FIRE, BREAK_BLOCKS, entity);
    }

    @Test
    public void cancelActivationDoesNotCancelActivationIfNotPrimed() {
        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, damageProcessor, rangeProfile, targetFinder);
        effect.cancelActivation();

        verify(effectActivation, never()).cancel();
    }

    @Test
    public void cancelActivationDoesNotCancelActivationIfAlreadyActivated() {
        Location sourceLocation = new Location(null, 1, 1, 1);
        World world = mock(World.class);

        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, damageProcessor, rangeProfile, targetFinder);
        effect.prime(context);

        ArgumentCaptor<Procedure> procedureCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(effectActivation).prime(eq(context), procedureCaptor.capture());

        procedureCaptor.getValue().apply();
        effect.cancelActivation();

        verify(effectActivation, never()).cancel();
    }

    @Test
    public void cancelActivationCancelsActivationIfPrimed() {
        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, damageProcessor, rangeProfile, targetFinder);
        effect.prime(context);
        effect.cancelActivation();

        verify(effectActivation).cancel();
    }

    @Test
    public void deployChangesTheSourceOfTheContext() {
        ItemEffectSource newSource = mock(ItemEffectSource.class);

        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, damageProcessor, rangeProfile, targetFinder);
        effect.prime(context);
        effect.deploy(newSource);

        assertEquals(newSource, context.getSource());
    }

    @Test
    public void deployDoesNothingIfEffectIsNotPrimedYet() {
        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, damageProcessor, rangeProfile, targetFinder);

        // This method currently has no side effects to verify, refactor later?
        assertDoesNotThrow(() -> effect.deploy(source));
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseIfEffectIsNotPrimed() {
        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, damageProcessor, rangeProfile, targetFinder);
        boolean awaitingDeployment = effect.isAwaitingDeployment();

        assertFalse(awaitingDeployment);
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseIfContextSourceIsDeployed() {
        when(source.isDeployed()).thenReturn(true);

        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, damageProcessor, rangeProfile, targetFinder);
        effect.prime(context);
        boolean awaitingDeployment = effect.isAwaitingDeployment();

        assertFalse(awaitingDeployment);
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseIfContextSourceIsNotDeployed() {
        when(source.isDeployed()).thenReturn(false);

        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, damageProcessor, rangeProfile, targetFinder);
        effect.prime(context);
        boolean awaitingDeployment = effect.isAwaitingDeployment();

        assertTrue(awaitingDeployment);
    }

    @Test
    public void isPrimedReturnsFalseIfEffectWasNotPrimed() {
        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, damageProcessor, rangeProfile, targetFinder);
        boolean primed = effect.isPrimed();

        assertFalse(primed);
    }

    @Test
    public void isPrimedReturnsTrueIfEffectWasPrimed() {
        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, damageProcessor, rangeProfile, targetFinder);
        effect.prime(context);
        boolean primed = effect.isPrimed();

        assertTrue(primed);
    }

    @Test
    public void primePrimesEffectActivationOnce() {
        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, damageProcessor, rangeProfile, targetFinder);
        effect.prime(context);
        effect.prime(context);

        verify(effectActivation).prime(eq(context), any(Procedure.class));
    }

    @Test
    public void primeCreatesExplosionAtSourceLocationAndDamagesAllEntitiesInsideTheLongRangeDistance() {
        UUID entityId = UUID.randomUUID();
        World world = mock(World.class);
        Location objectLocation = new Location(world, 2, 1, 1);
        Location sourceLocation = new Location(world, 1, 1, 1);
        Location targetLocation = new Location(world, 8, 1, 1);

        when(entity.getUniqueId()).thenReturn(entityId);
        when(entity.getWorld()).thenReturn(world);

        GameEntity deployerEntity = mock(GameEntity.class);
        when(deployerEntity.getLocation()).thenReturn(sourceLocation);

        GameEntity target = mock(GameEntity.class);
        when(target.getLocation()).thenReturn(targetLocation);

        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getLocation()).thenReturn(objectLocation);

        when(targetFinder.findDeploymentObjects(entityId, sourceLocation, LONG_RANGE_DISTANCE)).thenReturn(List.of(deploymentObject));
        when(targetFinder.findTargets(entityId, sourceLocation, LONG_RANGE_DISTANCE)).thenReturn(List.of(deployerEntity, target));

        ExplosionProperties properties = new ExplosionProperties(POWER, SET_FIRE, BREAK_BLOCKS);

        ExplosionEffect effect = new ExplosionEffect(effectActivation, properties, damageProcessor, rangeProfile, targetFinder);
        effect.prime(context);

        ArgumentCaptor<Procedure> procedureCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(effectActivation).prime(eq(context), procedureCaptor.capture());

        procedureCaptor.getValue().apply();

        verify(damageProcessor).processDeploymentObjectDamage(deploymentObject, new Damage(SHORT_RANGE_DAMAGE, DamageType.EXPLOSIVE_DAMAGE));
        verify(source).remove();
        verify(deployerEntity).damage(new Damage(SHORT_RANGE_DAMAGE, DamageType.EXPLOSIVE_DAMAGE));
        verify(target).damage(new Damage(LONG_RANGE_DAMAGE, DamageType.EXPLOSIVE_DAMAGE));
        verify(world).createExplosion(sourceLocation, POWER, SET_FIRE, BREAK_BLOCKS, entity);
    }
}
