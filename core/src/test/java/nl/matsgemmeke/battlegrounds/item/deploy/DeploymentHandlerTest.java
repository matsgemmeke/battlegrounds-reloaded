package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.deploy.activator.Activator;
import nl.matsgemmeke.battlegrounds.item.effect.*;
import nl.matsgemmeke.battlegrounds.item.trigger.tracking.TriggerTarget;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.Particle;
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
    private static final long COOLDOWN = 3L;
    private static final long MANUAL_ACTIVATION_DELAY = 10L;
    private static final ParticleEffect DESTRUCTION_PARTICLE_EFFECT = new ParticleEffect(Particle.ASH, 1, 0, 0, 0, 0, null, null);
    private static final DeploymentProperties PROPERTIES = new DeploymentProperties(ACTIVATION_SOUNDS, DESTRUCTION_PARTICLE_EFFECT, ACTIVATE_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_DESTRUCTION, UNDO_EFFECT_ON_DESTRUCTION, REMOVE_DEPLOYMENT_ON_CLEANUP, MANUAL_ACTIVATION_DELAY);

    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private Deployer deployer;
    @Mock
    private DeploymentObject deploymentObject;
    @Mock
    private ItemEffect itemEffect;
    @Mock
    private ParticleEffectSpawner particleEffectSpawner;
    @Mock
    private Scheduler scheduler;
    @Mock
    private TriggerTarget triggerTarget;

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
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);
        DeploymentProperties properties = new DeploymentProperties(List.of(), null, true, true, true, false, 0L);
        Schedule delaySchedule = mock(Schedule.class);

        when(deploymentObject.isPhysical()).thenReturn(true);
        when(scheduler.createSingleRunSchedule(COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, properties, itemEffect);
        deploymentHandler.processDeploymentResult(deploymentResult);
        deploymentHandler.cleanupDeployment();

        assertThat(deploymentHandler.isPerforming()).isTrue();

        verify(deploymentObject, never()).remove();
    }

    @Test
    void cleanupDeploymentRemovesDeploymentObjectWhenDeployedAndRemoveDeploymentOnCleanupIsTrue() {
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);
        Schedule delaySchedule = mock(Schedule.class);

        when(deploymentObject.isPhysical()).thenReturn(true);
        when(scheduler.createSingleRunSchedule(COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.processDeploymentResult(deploymentResult);
        deploymentHandler.cleanupDeployment();

        assertThat(deploymentHandler.isPerforming()).isFalse();

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
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);
        DeploymentProperties properties = new DeploymentProperties(ACTIVATION_SOUNDS, DESTRUCTION_PARTICLE_EFFECT, false, false, false, false, MANUAL_ACTIVATION_DELAY);
        Schedule delaySchedule = mock(Schedule.class);

        when(deploymentObject.isPhysical()).thenReturn(true);
        when(scheduler.createSingleRunSchedule(COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, properties, itemEffect);
        deploymentHandler.processDeploymentResult(deploymentResult);
        deploymentHandler.destroyDeployment();

        verify(itemEffect).cancelPerformances();
        verify(itemEffect, never()).activatePerformances();
    }

    @Test
    void destroyDeploymentDoesNotActivateEffectWhenDeploymentObjectLastDamageTypeIsEnvironmentalDamage() {
        Damage lastDamage = new Damage(10, DamageType.ENVIRONMENTAL_DAMAGE);
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);
        Schedule delaySchedule = mock(Schedule.class);

        when(deploymentObject.getLastDamage()).thenReturn(lastDamage);
        when(deploymentObject.isPhysical()).thenReturn(true);
        when(scheduler.createSingleRunSchedule(COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.processDeploymentResult(deploymentResult);
        deploymentHandler.destroyDeployment();

        verify(itemEffect).cancelPerformances();
        verify(itemEffect, never()).activatePerformances();
    }

    @Test
    void destroyDeploymentActivatesEffectWhenDeploymentObjectLastDamageIsNull() {
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);
        Schedule delaySchedule = mock(Schedule.class);

        when(deploymentObject.getLastDamage()).thenReturn(null);
        when(deploymentObject.isPhysical()).thenReturn(true);
        when(scheduler.createSingleRunSchedule(COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.processDeploymentResult(deploymentResult);
        deploymentHandler.destroyDeployment();

        verify(itemEffect).cancelPerformances();
        verify(itemEffect).activatePerformances();
    }

    @Test
    void destroyDeploymentActivatesEffectWhenDeploymentObjectLastDamageTypeIsNotEnvironmentalDamage() {
        Damage lastDamage = new Damage(10, DamageType.BULLET_DAMAGE);
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);
        Schedule delaySchedule = mock(Schedule.class);

        when(deploymentObject.getLastDamage()).thenReturn(lastDamage);
        when(deploymentObject.isPhysical()).thenReturn(true);
        when(scheduler.createSingleRunSchedule(COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.processDeploymentResult(deploymentResult);
        deploymentHandler.destroyDeployment();

        verify(itemEffect).cancelPerformances();
        verify(itemEffect).activatePerformances();
    }

    @Test
    void destroyDeploymentDoesNotRemoveDeploymentObjectWhenRemoveDeploymentOnDestructionPropertyIsFalse() {
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);
        DeploymentProperties properties = new DeploymentProperties(ACTIVATION_SOUNDS, DESTRUCTION_PARTICLE_EFFECT, false, false, false, false, MANUAL_ACTIVATION_DELAY);
        Schedule delaySchedule = mock(Schedule.class);

        when(deploymentObject.isPhysical()).thenReturn(true);
        when(scheduler.createSingleRunSchedule(COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, properties, itemEffect);
        deploymentHandler.processDeploymentResult(deploymentResult);
        deploymentHandler.destroyDeployment();

        verify(deploymentObject, never()).remove();
        verify(itemEffect).cancelPerformances();
    }

    @Test
    void destroyDeploymentRemovesDeploymentObjectWhenRemoveDeploymentOnDestructionPropertyIsTrue() {
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);
        Schedule delaySchedule = mock(Schedule.class);

        when(deploymentObject.isPhysical()).thenReturn(true);
        when(scheduler.createSingleRunSchedule(COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.processDeploymentResult(deploymentResult);
        deploymentHandler.destroyDeployment();

        verify(deploymentObject).remove();
        verify(itemEffect).cancelPerformances();
    }

    @Test
    void destroyDeploymentDoesNotResetEffectWhenResetEffectOnDestructionPropertyIsFalse() {
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);
        DeploymentProperties properties = new DeploymentProperties(ACTIVATION_SOUNDS, DESTRUCTION_PARTICLE_EFFECT, false, false, false, false, MANUAL_ACTIVATION_DELAY);
        Schedule delaySchedule = mock(Schedule.class);

        when(deploymentObject.isPhysical()).thenReturn(true);
        when(scheduler.createSingleRunSchedule(COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, properties, itemEffect);
        deploymentHandler.processDeploymentResult(deploymentResult);
        deploymentHandler.destroyDeployment();

        verify(itemEffect).cancelPerformances();
        verify(itemEffect, never()).rollbackPerformances();
    }

    @Test
    void destroyDeploymentUndoesEffectWhenResetEffectOnDestructionPropertyIsTrue() {
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);
        Schedule delaySchedule = mock(Schedule.class);

        when(deploymentObject.isPhysical()).thenReturn(true);
        when(scheduler.createSingleRunSchedule(COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.processDeploymentResult(deploymentResult);
        deploymentHandler.destroyDeployment();

        verify(itemEffect).cancelPerformances();
        verify(itemEffect).rollbackPerformances();
    }

    @Test
    void destroyDeploymentDoesNotDisplayParticleEffectWhenDestructionParticleEffectPropertyIsNull() {
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);
        DeploymentProperties properties = new DeploymentProperties(ACTIVATION_SOUNDS, null, false, false, false, false, MANUAL_ACTIVATION_DELAY);
        Schedule delaySchedule = mock(Schedule.class);

        when(deploymentObject.isPhysical()).thenReturn(true);
        when(scheduler.createSingleRunSchedule(COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, properties, itemEffect);
        deploymentHandler.processDeploymentResult(deploymentResult);
        deploymentHandler.destroyDeployment();

        verify(itemEffect).cancelPerformances();
        verifyNoInteractions(particleEffectSpawner);
    }

    @Test
    void destroyDeploymentDisplaysParticleEffectWhenDestructionParticleEffectPropertyIsNotNull() {
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);
        Location objectLocation = new Location(null, 1, 1, 1);
        Schedule delaySchedule = mock(Schedule.class);

        when(deploymentObject.getLocation()).thenReturn(objectLocation);
        when(deploymentObject.isPhysical()).thenReturn(true);
        when(scheduler.createSingleRunSchedule(COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.processDeploymentResult(deploymentResult);
        deploymentHandler.destroyDeployment();

        verify(itemEffect).cancelPerformances();
        verify(particleEffectSpawner).spawnParticleEffect(DESTRUCTION_PARTICLE_EFFECT, objectLocation);
    }

    @Test
    void isAwaitingDeploymentReturnsFalseWhenNoDeploymentHasBeenPerformed() {
        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        boolean awaitingDeployment = deploymentHandler.isAwaitingDeployment();

        assertThat(awaitingDeployment).isFalse();
    }

    @Test
    void isAwaitingDeploymentReturnsFalseWhenCompleteDeploymentIsPerformed() {
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);
        Schedule delaySchedule = mock(Schedule.class);

        when(deploymentObject.isPhysical()).thenReturn(true);
        when(scheduler.createSingleRunSchedule(COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.processDeploymentResult(deploymentResult);
        boolean awaitingDeployment = deploymentHandler.isAwaitingDeployment();

        assertThat(awaitingDeployment).isFalse();
    }

    @Test
    void isAwaitingDeploymentReturnsFalseWhenPendingDeploymentIsPerformed() {
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);

        when(deploymentObject.isPhysical()).thenReturn(false);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.processDeploymentResult(deploymentResult);
        boolean awaitingDeployment = deploymentHandler.isAwaitingDeployment();

        assertThat(awaitingDeployment).isTrue();
    }

    @Test
    void isPerformingReturnsFalseWhenNoDeploymentIsPerformed() {
        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        boolean deployed = deploymentHandler.isPerforming();

        assertThat(deployed).isFalse();
    }

    @Test
    void isPerformingReturnsTrueWhenAnyDeploymentIsPerformed() {
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);

        when(deploymentObject.isPhysical()).thenReturn(false);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.processDeploymentResult(deploymentResult);
        boolean deployed = deploymentHandler.isPerforming();

        assertThat(deployed).isTrue();
    }

    @Test
    void processDeploymentResultChangesEffectSourceWhenLatestPerformanceIsNotNull() {
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);
        ItemEffectPerformance effectPerformance = mock(ItemEffectPerformance.class);

        when(itemEffect.getLatestPerformance()).thenReturn(Optional.of(effectPerformance));

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.processDeploymentResult(deploymentResult);

        verify(effectPerformance).changeActor(deploymentObject);
    }

    @Test
    void processDeploymentResultPerformsPendingDeploymentWhenNoDeploymentObjectIsProducedYet() {
        Location deploymentObjectLocation = new Location(null, 1, 1, 1);
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);

        when(deploymentObject.getLocation()).thenReturn(deploymentObjectLocation);
        when(deploymentObject.isPhysical()).thenReturn(false);
        when(itemEffect.getLatestPerformance()).thenReturn(Optional.empty());

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.processDeploymentResult(deploymentResult);

        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        assertThat(itemEffectContextCaptor.getValue()).satisfies(itemEffectContext -> {
            assertThat(itemEffectContext.getDamageSource()).isEqualTo(deployer);
            assertThat(itemEffectContext.getEffectSource()).isEqualTo(deploymentObject);
            assertThat(itemEffectContext.getInitiationLocation()).isEqualTo(deploymentObjectLocation);
        });
    }

    @Test
    void processDeploymentResultPerformsCompleteDeploymentWhenDeploymentObjectIsProduced() {
        Activator activator = mock(Activator.class);
        Location deployLocation = new Location(null, 1, 1, 1);
        DeploymentResult deploymentResult = new DeploymentResult(deployer, deploymentObject, triggerTarget, COOLDOWN);

        Schedule delaySchedule = mock(Schedule.class);
        doAnswer(RUN_SCHEDULE_TASK).when(delaySchedule).addTask(any(ScheduleTask.class));

        when(deployer.getDeployLocation()).thenReturn(deployLocation);
        when(deploymentObject.isPhysical()).thenReturn(true);
        when(itemEffect.getLatestPerformance()).thenReturn(Optional.empty());
        when(scheduler.createSingleRunSchedule(COOLDOWN)).thenReturn(delaySchedule);

        DeploymentHandler deploymentHandler = new DeploymentHandler(audioEmitter, particleEffectSpawner, scheduler, PROPERTIES, itemEffect);
        deploymentHandler.setActivator(activator);
        deploymentHandler.processDeploymentResult(deploymentResult);

        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        assertThat(itemEffectContextCaptor.getValue()).satisfies(itemEffectContext -> {
            assertThat(itemEffectContext.getDamageSource()).isEqualTo(deployer);
            assertThat(itemEffectContext.getEffectSource()).isEqualTo(deploymentObject);
            assertThat(itemEffectContext.getInitiationLocation()).isEqualTo(deployLocation);
        });

        verify(activator).prepare(deployer);
        verify(deployer).setCanDeploy(false);
        verify(deployer).setCanDeploy(true);
    }
}
