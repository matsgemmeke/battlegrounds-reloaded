package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.effect.*;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static nl.matsgemmeke.battlegrounds.MockUtils.RUN_SCHEDULE_TASK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeploymentHandlerTest {

    private static final boolean ACTIVATE_EFFECT_ON_DESTRUCTION = true;
    private static final boolean REMOVE_DEPLOYMENT_ON_DESTRUCTION = true;
    private static final boolean UNDO_EFFECT_ON_DESTRUCTION = true;
    private static final boolean REMOVE_DEPLOYMENT_ON_CLEANUP = true;
    private static final List<GameSound> ACTIVATION_SOUNDS = Collections.emptyList();
    private static final Location DEPLOYMENT_LOCATION = new Location(null, 1, 1, 1);
    private static final long DEPLOYMENT_OBJECT_COOLDOWN = 3L;
    private static final long MANUAL_ACTIVATION_DELAY = 10L;
    private static final ParticleEffect DESTRUCTION_PARTICLE_EFFECT = new ParticleEffect(Particle.ASH, 1, 0, 0, 0, 0, null, null);
    private static final DeploymentProperties PROPERTIES = new DeploymentProperties(ACTIVATION_SOUNDS, DESTRUCTION_PARTICLE_EFFECT, ACTIVATE_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_DESTRUCTION, UNDO_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_CLEANUP, MANUAL_ACTIVATION_DELAY);

    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private Deployer deployer;
    @Mock
    private Deployment deployment;
    @Mock
    private DeploymentObject deploymentObject;
    @Mock
    private Entity deployerEntity;
    @Mock
    private ItemEffect itemEffect;
    @Mock
    private ParticleEffectSpawner particleEffectSpawner;
    @Mock
    private Scheduler scheduler;

    @Test
    void activateDeploymentActivatesEffectAfterActivationDelay() {
        Schedule delaySchedule = mock(Schedule.class);
        doAnswer(invocation -> {
            ScheduleTask task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(delaySchedule).addTask(any(ScheduleTask.class));

        when(deployer.getDeployLocation()).thenReturn(DEPLOYMENT_LOCATION);
        when(scheduler.createSingleRunSchedule(MANUAL_ACTIVATION_DELAY)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.activateDeployment(deployer);

        verify(audioEmitter).playSounds(ACTIVATION_SOUNDS, DEPLOYMENT_LOCATION);
        verify(deployer).setHeldItem(null);
        verify(itemEffect).activatePerformances();
    }

    @Test
    void cleanupDeploymentDoesNotRemoveDeploymentObjectWhenDeployedAndRemoveDeploymentOnCleanupIsFalse() {
        DeploymentProperties properties = new DeploymentProperties(List.of(), null, true, true, true, false, 0L);
        Schedule delaySchedule = mock(Schedule.class);

        when(deploymentObject.getCooldown()).thenReturn(DEPLOYMENT_OBJECT_COOLDOWN);
        when(deploymentObject.isDeployed()).thenReturn(true);
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(scheduler.createSingleRunSchedule(DEPLOYMENT_OBJECT_COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, properties, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.cleanupDeployment();

        assertThat(deploymentHandler.isDeployed()).isTrue();

        verify(deploymentObject, never()).remove();
    }

    @Test
    void cleanupDeploymentRemovesDeploymentObjectWhenDeployedAndRemoveDeploymentOnCleanupIsTrue() {
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.cleanupDeployment();

        assertThat(deploymentHandler.isDeployed()).isFalse();

        verify(deploymentObject).remove();
    }

    @Test
    void destroyDeploymentDoesNotCancelEffectWhenNoDeploymentsHaveBeenPerformed() {
        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.destroyDeployment();

        verifyNoInteractions(itemEffect);
    }

    @Test
    void destroyDeploymentDoesNotActivateEffectWhenActivateEffectOnDestructionPropertyIsFalse() {
        DeploymentProperties properties = new DeploymentProperties(ACTIVATION_SOUNDS, DESTRUCTION_PARTICLE_EFFECT, false, false, false, false, MANUAL_ACTIVATION_DELAY);

        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, properties, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(itemEffect).cancelPerformances();
        verify(itemEffect, never()).activatePerformances();
    }

    @Test
    void destroyDeploymentDoesNotActivateEffectWhenDeploymentObjectLastDamageTypeIsEnvironmentalDamage() {
        Damage lastDamage = new Damage(10, DamageType.ENVIRONMENTAL_DAMAGE);

        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.getLastDamage()).thenReturn(lastDamage);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(itemEffect).cancelPerformances();
        verify(itemEffect, never()).activatePerformances();
    }

    @Test
    void destroyDeploymentActivatesEffectWhenDeploymentObjectLastDamageIsNull() {
        when(deploymentObject.getLastDamage()).thenReturn(null);
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(itemEffect).cancelPerformances();
        verify(itemEffect).activatePerformances();
    }

    @Test
    void destroyDeploymentActivatesEffectWhenDeploymentObjectLastDamageTypeIsNotEnvironmentalDamage() {
        Damage lastDamage = new Damage(10, DamageType.BULLET_DAMAGE);

        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.getLastDamage()).thenReturn(lastDamage);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(itemEffect).cancelPerformances();
        verify(itemEffect).activatePerformances();
    }

    @Test
    void destroyDeploymentDoesNotRemoveDeploymentObjectWhenRemoveDeploymentOnDestructionPropertyIsFalse() {
        DeploymentProperties properties = new DeploymentProperties(ACTIVATION_SOUNDS, DESTRUCTION_PARTICLE_EFFECT, false, false, false, false, MANUAL_ACTIVATION_DELAY);

        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, properties, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(deploymentObject, never()).remove();
        verify(itemEffect).cancelPerformances();
    }

    @Test
    void destroyDeploymentRemovesDeploymentObjectWhenRemoveDeploymentOnDestructionPropertyIsTrue() {
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(deploymentObject).remove();
        verify(itemEffect).cancelPerformances();
    }

    @Test
    void destroyDeploymentDoesNotResetEffectWhenResetEffectOnDestructionPropertyIsFalse() {
        DeploymentProperties properties = new DeploymentProperties(ACTIVATION_SOUNDS, DESTRUCTION_PARTICLE_EFFECT, false, false, false, false, MANUAL_ACTIVATION_DELAY);

        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, properties, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(itemEffect).cancelPerformances();
        verify(itemEffect, never()).rollbackPerformances();
    }

    @Test
    void destroyDeploymentUndoesEffectWhenResetEffectOnDestructionPropertyIsTrue() {
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(itemEffect).cancelPerformances();
        verify(itemEffect).rollbackPerformances();
    }

    @Test
    void destroyDeploymentDoesNotDisplayParticleEffectWhenDestructionParticleEffectPropertyIsNull() {
        DeploymentProperties properties = new DeploymentProperties(ACTIVATION_SOUNDS, null, false, false, false, false, MANUAL_ACTIVATION_DELAY);

        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, properties, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(itemEffect).cancelPerformances();
        verifyNoInteractions(particleEffectSpawner);
    }

    @Test
    void destroyDeploymentDisplaysParticleEffectWhenDestructionParticleEffectPropertyIsNotNull() {
        Location objectLocation = new Location(null, 1, 1, 1);

        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.getLocation()).thenReturn(objectLocation);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        deploymentHandler.destroyDeployment();

        verify(itemEffect).cancelPerformances();
        verify(particleEffectSpawner).spawnParticleEffect(DESTRUCTION_PARTICLE_EFFECT, objectLocation);
    }

    @Test
    void handleDeploymentDoesNotStartEffectWhenDeploymentResultIsNotSuccessful() {
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.failure());

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);

        verifyNoInteractions(itemEffect, scheduler);
    }

    @Test
    void handleDeploymentChangesLatestPerformanceSourceWhenLatestPerformanceIsNotReleased() {
        ItemEffectPerformance performance = mock(ItemEffectPerformance.class);
        when(performance.isReleased()).thenReturn(false);

        Schedule delaySchedule = mock(Schedule.class);
        doAnswer(invocation -> {
            ScheduleTask task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(delaySchedule).addTask(any(ScheduleTask.class));

        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.getCooldown()).thenReturn(DEPLOYMENT_OBJECT_COOLDOWN);
        when(deploymentObject.isDeployed()).thenReturn(true);
        when(itemEffect.getLatestPerformance()).thenReturn(Optional.of(performance));
        when(scheduler.createSingleRunSchedule(DEPLOYMENT_OBJECT_COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);

        verify(deployer).setCanDeploy(false);
        verify(deployer).setCanDeploy(true);
        verify(performance).changeSource(deploymentObject);
    }

    @Test
    void handleDeploymentStartsNewPerformanceWhenLatestPerformanceIsReleased() {
        long cooldown = 10L;
        Location deployLocation = new Location(null, 1, 1, 1);

        ItemEffectPerformance performance = mock(ItemEffectPerformance.class);
        when(performance.isReleased()).thenReturn(true);

        Schedule delaySchedule = mock(Schedule.class);
        doAnswer(invocation -> {
            ScheduleTask task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(delaySchedule).addTask(any(ScheduleTask.class));

        when(deployer.getDeployLocation()).thenReturn(deployLocation);
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.getCooldown()).thenReturn(cooldown);
        when(deploymentObject.isDeployed()).thenReturn(true);
        when(itemEffect.getLatestPerformance()).thenReturn(Optional.of(performance));
        when(scheduler.createSingleRunSchedule(cooldown)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);

        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        ItemEffectContext context = itemEffectContextCaptor.getValue();
        assertThat(context.getEntity()).isEqualTo(deployerEntity);
        assertThat(context.getSource()).isEqualTo(deploymentObject);
        assertThat(context.getInitiationLocation()).isEqualTo(deployLocation);

        verify(deployer).setCanDeploy(false);
        verify(deployer).setCanDeploy(true);
    }

    @Test
    void handleDeploymentPreparesActivatorItemWhenActivatorIsNotNull() {
        Activator activator = mock(Activator.class);
        Schedule delaySchedule = mock(Schedule.class);

        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.getCooldown()).thenReturn(DEPLOYMENT_OBJECT_COOLDOWN);
        when(deploymentObject.isDeployed()).thenReturn(true);
        when(itemEffect.getLatestPerformance()).thenReturn(Optional.empty());
        when(scheduler.createSingleRunSchedule(DEPLOYMENT_OBJECT_COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.setActivator(activator);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);

        verify(activator).prepare(deployer);
    }

    @Test
    void isAwaitingDeploymentReturnsFalseWhenNoDeploymentHasBeenPerformed() {
        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        boolean awaitingDeployment = deploymentHandler.isAwaitingDeployment();

        assertThat(awaitingDeployment).isFalse();
    }

    @Test
    void isAwaitingDeploymentReturnsFalseWhenDeploymentHasBeenPerformedWithObjectThatIsAlreadyDeployed() {
        Schedule delaySchedule = mock(Schedule.class);

        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.getCooldown()).thenReturn(DEPLOYMENT_OBJECT_COOLDOWN);
        when(deploymentObject.isDeployed()).thenReturn(true);
        when(scheduler.createSingleRunSchedule(DEPLOYMENT_OBJECT_COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        boolean awaitingDeployment = deploymentHandler.isAwaitingDeployment();

        assertThat(awaitingDeployment).isFalse();
    }

    @Test
    void isAwaitingDeploymentReturnsTrueWhenDeploymentHasBeenPerformedWithObjectThatIsNotDeployed() {
        when(deployment.perform(deployer, deployerEntity)).thenReturn(DeploymentResult.success(deploymentObject));
        when(deploymentObject.isDeployed()).thenReturn(false);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        boolean awaitingDeployment = deploymentHandler.isAwaitingDeployment();

        assertThat(awaitingDeployment).isTrue();
    }

    @Test
    void isDeployedReturnsFalseWhenNoDeploymentIsPerformed() {
        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        boolean deployed = deploymentHandler.isDeployed();

        assertThat(deployed).isFalse();
    }

    @Test
    void isDeployedReturnsTrueWhenAnyDeploymentIsPerformed() {
        DeploymentResult result = DeploymentResult.success(deploymentObject);
        Schedule delaySchedule = mock(Schedule.class);

        when(deploymentObject.getCooldown()).thenReturn(DEPLOYMENT_OBJECT_COOLDOWN);
        when(deploymentObject.isDeployed()).thenReturn(true);
        when(deployment.perform(deployer, deployerEntity)).thenReturn(result);
        when(scheduler.createSingleRunSchedule(DEPLOYMENT_OBJECT_COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.handleDeployment(deployment, deployer, deployerEntity);
        boolean deployed = deploymentHandler.isDeployed();

        assertThat(deployed).isTrue();
    }

    @Test
    void performDeploymentPerformsPendingDeploymentWhenNoDeploymentObjectIsProducedYet() {
        Location effectSourceLocation = new Location(null, 1, 1, 1);

        ItemEffectSource effectSource = mock(ItemEffectSource.class);
        when(effectSource.getLocation()).thenReturn(effectSourceLocation);

        DeploymentContext context = new DeploymentContext(deployerEntity, effectSource, deployer, null);

        when(itemEffect.getLatestPerformance()).thenReturn(Optional.empty());

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.performDeployment(context);

        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        assertThat(itemEffectContextCaptor.getValue()).satisfies(itemEffectContext -> {
            assertThat(itemEffectContext.getEntity()).isEqualTo(deployerEntity);
            assertThat(itemEffectContext.getInitiationLocation()).isEqualTo(effectSourceLocation);
            assertThat(itemEffectContext.getSource()).isEqualTo(effectSource);
        });
    }

    @Test
    void performDeploymentPerformsCompleteDeploymentWhenDeploymentObjectIsProduced() {
        Activator activator = mock(Activator.class);
        ItemEffectSource effectSource = mock(ItemEffectSource.class);
        Location deployLocation = new Location(null, 1, 1, 1);
        DeploymentContext context = new DeploymentContext(deployerEntity, effectSource, deployer, deploymentObject);

        Schedule delaySchedule = mock(Schedule.class);
        doAnswer(RUN_SCHEDULE_TASK).when(delaySchedule).addTask(any(ScheduleTask.class));

        when(deployer.getDeployLocation()).thenReturn(deployLocation);
        when(deploymentObject.getCooldown()).thenReturn(DEPLOYMENT_OBJECT_COOLDOWN);
        when(itemEffect.getLatestPerformance()).thenReturn(Optional.empty());
        when(scheduler.createSingleRunSchedule(DEPLOYMENT_OBJECT_COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.setActivator(activator);
        deploymentHandler.performDeployment(context);

        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        assertThat(itemEffectContextCaptor.getValue()).satisfies(itemEffectContext -> {
            assertThat(itemEffectContext.getEntity()).isEqualTo(deployerEntity);
            assertThat(itemEffectContext.getInitiationLocation()).isEqualTo(deployLocation);
            assertThat(itemEffectContext.getSource()).isEqualTo(effectSource);
        });

        verify(activator).prepare(deployer);
        verify(deployer).setCanDeploy(false);
        verify(deployer).setCanDeploy(true);
    }
}
