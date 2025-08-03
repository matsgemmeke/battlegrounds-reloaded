package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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
    private static final Location INITIATION_LOCATION = new Location(null, 0, 0, 0);

    private DamageProcessor damageProcessor;
    private Entity entity;
    private ExplosionProperties properties;
    private ItemEffectSource source;
    private RangeProfile rangeProfile;
    private TargetFinder targetFinder;
    private Trigger trigger;

    @BeforeEach
    public void setUp() {
        damageProcessor = mock(DamageProcessor.class);
        entity = mock(Entity.class);
        properties = new ExplosionProperties(POWER, SET_FIRE, BREAK_BLOCKS);
        source = mock(ItemEffectSource.class);
        rangeProfile = new RangeProfile(SHORT_RANGE_DAMAGE, SHORT_RANGE_DISTANCE, MEDIUM_RANGE_DAMAGE, MEDIUM_RANGE_DISTANCE, LONG_RANGE_DAMAGE, LONG_RANGE_DISTANCE);
        targetFinder = mock(TargetFinder.class);
        trigger = mock(Trigger.class);
    }

    @Test
    public void activateInstantlyPerformsEffectAndDeactivatesTriggersWhenContextSourceExists() {
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 1, 1, 1);
        ItemEffectContext context = new ItemEffectContext(entity, source, INITIATION_LOCATION);

        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        ExplosionEffect effect = new ExplosionEffect(properties, damageProcessor, rangeProfile, targetFinder);
        effect.addTrigger(trigger);
        effect.prime(context);
        effect.activateInstantly();

        verify(source).remove();
        verify(trigger).stop();
        verify(world).createExplosion(sourceLocation, POWER, SET_FIRE, BREAK_BLOCKS, entity);
    }

    @Test
    public void cancelActivationDoesNotDeactivateTriggersWhenNotPrimed() {
        ExplosionEffect effect = new ExplosionEffect(properties, damageProcessor, rangeProfile, targetFinder);
        effect.addTrigger(trigger);
        effect.cancelActivation();

        verify(trigger, never()).stop();
    }

    @Test
    public void cancelActivationDeactivatesTriggersWhenPrimed() {
        ItemEffectContext context = new ItemEffectContext(entity, source, INITIATION_LOCATION);

        ExplosionEffect effect = new ExplosionEffect(properties, damageProcessor, rangeProfile, targetFinder);
        effect.addTrigger(trigger);
        effect.prime(context);
        effect.cancelActivation();

        verify(trigger).stop();
    }

    @Test
    public void deployChangesSourceOfContext() {
        ItemEffectContext context = new ItemEffectContext(entity, source, INITIATION_LOCATION);
        ItemEffectSource newSource = mock(ItemEffectSource.class);

        ExplosionEffect effect = new ExplosionEffect(properties, damageProcessor, rangeProfile, targetFinder);
        effect.prime(context);
        effect.deploy(newSource);

        assertThat(context.getSource()).isEqualTo(newSource);
    }

    @Test
    public void deployDoesNothingIfEffectIsNotPrimedYet() {
        ExplosionEffect effect = new ExplosionEffect(properties, damageProcessor, rangeProfile, targetFinder);

        // This method currently has no side effects to verify, refactor later?
        assertThatCode(() -> effect.deploy(source)).doesNotThrowAnyException();
    }

    @Test
    public void isPrimedReturnsFalseIfEffectWasNotPrimed() {
        ExplosionEffect effect = new ExplosionEffect(properties, damageProcessor, rangeProfile, targetFinder);
        boolean primed = effect.isPrimed();

        assertThat(primed).isFalse();
    }

    @Test
    public void isPrimedReturnsTrueIfEffectWasPrimed() {
        ItemEffectContext context = new ItemEffectContext(entity, source, INITIATION_LOCATION);

        ExplosionEffect effect = new ExplosionEffect(properties, damageProcessor, rangeProfile, targetFinder);
        effect.prime(context);
        boolean primed = effect.isPrimed();

        assertThat(primed).isTrue();
    }

    @Test
    public void primePrimesEffectActivationOnce() {
        ItemEffectContext context = new ItemEffectContext(entity, source, INITIATION_LOCATION);

        ExplosionEffect effect = new ExplosionEffect(properties, damageProcessor, rangeProfile, targetFinder);
        effect.addTrigger(trigger);
        effect.prime(context);
        effect.prime(context);

        ArgumentCaptor<TriggerContext> triggerContextCaptor = ArgumentCaptor.forClass(TriggerContext.class);
        verify(trigger, times(1)).start(triggerContextCaptor.capture());

        TriggerContext triggerContext = triggerContextCaptor.getValue();
        assertThat(triggerContext.entity()).isEqualTo(entity);
        assertThat(triggerContext.target()).isEqualTo(source);
    }

    @Test
    public void primeCreatesExplosionAtSourceLocationAndDamagesAllEntitiesInsideTheLongRangeDistance() {
        UUID entityId = UUID.randomUUID();
        World world = mock(World.class);
        Location objectLocation = new Location(world, 2, 1, 1);
        Location sourceLocation = new Location(world, 1, 1, 1);
        Location targetLocation = new Location(world, 8, 1, 1);
        ItemEffectContext context = new ItemEffectContext(entity, source, INITIATION_LOCATION);

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

        ExplosionEffect effect = new ExplosionEffect(properties, damageProcessor, rangeProfile, targetFinder);
        effect.addTrigger(trigger);
        effect.prime(context);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        verify(damageProcessor).processDeploymentObjectDamage(deploymentObject, new Damage(SHORT_RANGE_DAMAGE, DamageType.EXPLOSIVE_DAMAGE));
        verify(source).remove();
        verify(deployerEntity).damage(new Damage(SHORT_RANGE_DAMAGE, DamageType.EXPLOSIVE_DAMAGE));
        verify(target).damage(new Damage(LONG_RANGE_DAMAGE, DamageType.EXPLOSIVE_DAMAGE));
        verify(world).createExplosion(sourceLocation, POWER, SET_FIRE, BREAK_BLOCKS, entity);
    }
}
