package nl.matsgemmeke.battlegrounds.item.deploy;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentObjectRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.deploy.activator.Activator;
import nl.matsgemmeke.battlegrounds.item.deploy.object.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.deploy.state.DeploymentState;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.CollisionResultAdapter;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeploymentTest {

    private static final UUID DEPLOYER_UNIQUE_ID = UUID.randomUUID();
    private static final Location ACTOR_LOCATION = new Location(null, 1, 1, 1);
    private static final long COOLDOWN = 10L;

    private static final List<GameSound> MANUAL_ACTIVATION_SOUNDS = List.of();
    private static final ParticleEffect DESTRUCTION_PARTICLE_EFFECT = new ParticleEffect(Particle.FLAME, 0, 0, 0, 0, 0, null, null);
    private static final long MANUAL_ACTIVATION_DELAY = 10L;
    private static final DeploymentProperties DEFAULT_PROPERTIES = new DeploymentProperties(MANUAL_ACTIVATION_SOUNDS, DESTRUCTION_PARTICLE_EFFECT, true, true, true, true, MANUAL_ACTIVATION_DELAY);

    private static final Damage BULLET_DAMAGE = new Damage(10.0, DamageType.BULLET_DAMAGE);
    private static final Damage ENVIRONMENTAL_DAMAGE = new Damage(10.0, DamageType.ENVIRONMENTAL_DAMAGE);

    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private CollisionResultAdapter collisionResultAdapter;
    @Mock
    private DeploymentObjectRegistry deploymentObjectRegistry;
    @Mock
    private DeploymentState state;
    @Mock
    private ItemEffect itemEffect;
    @Spy
    private ParticleEffectSpawner particleEffectSpawner;
    @Mock
    private Scheduler scheduler;

    @Test
    @DisplayName("activate activates trigger runs after a delay")
    void activate() {
        Actor actor = mock(Actor.class);
        Location deployLocation = new Location(null, 1, 1, 1);
        TriggerRun triggerRun = mock(TriggerRun.class);

        Deployer deployer = mock(Deployer.class);
        when(deployer.getDeployLocation()).thenReturn(deployLocation);

        Schedule delaySchedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(delaySchedule).addTask(any(ScheduleTask.class));

        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutor.createTriggerRun(any(TriggerContext.class))).thenReturn(triggerRun);

        when(scheduler.createSingleRunSchedule(MANUAL_ACTIVATION_DELAY)).thenReturn(delaySchedule);

        Deployment deployment = new Deployment(audioEmitter, collisionResultAdapter, deploymentObjectRegistry, particleEffectSpawner, scheduler, DEFAULT_PROPERTIES, state, itemEffect);
        deployment.addTriggerExecutor(triggerExecutor);
        deployment.startTriggerExecutors(deployer, actor);
        deployment.activate(deployer);

        verify(audioEmitter).playSounds(MANUAL_ACTIVATION_SOUNDS, deployLocation);
        verify(delaySchedule).start();
        verify(triggerRun).notifyObservers();
    }

    @Test
    @DisplayName("destroy throws IllegalStateException when deployment is not deployed yet")
    void destroy_notDeployedYet() {
        Deployment deployment = new Deployment(audioEmitter, collisionResultAdapter, deploymentObjectRegistry, particleEffectSpawner, scheduler, DEFAULT_PROPERTIES, state, itemEffect);

        assertThatThrownBy(() -> deployment.destroy(BULLET_DAMAGE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Illegal call to destroy deployment that is not deployed yet");
    }

    @Test
    @DisplayName("destroy does not activate item effect performances when latest damage is environmental")
    void destroy_latestDamageIsEnvironmental() {
        World actorWorld = mock(World.class);
        Location actorLocation = new Location(actorWorld, 1, 1, 1);
        DeploymentObject deploymentObject = mock(DeploymentObject.class);

        Actor actor = mock(Actor.class);
        when(actor.getLocation()).thenReturn(actorLocation);

        DeploymentResult result = new DeploymentResult(null, deploymentObject, actor, 0L);

        Deployment deployment = new Deployment(audioEmitter, collisionResultAdapter, deploymentObjectRegistry, particleEffectSpawner, scheduler, DEFAULT_PROPERTIES, state, itemEffect);
        deployment.processDeploymentResult(result);
        deployment.destroy(ENVIRONMENTAL_DAMAGE);

        verify(itemEffect, never()).activatePerformances();
    }

    @Test
    @DisplayName("destroy does not perform any extra actions when properties are false")
    void destroy_performsNoExtraActions() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        Actor actor = mock(Actor.class);
        DeploymentResult result = new DeploymentResult(null, deploymentObject, actor, 0L);

        DeploymentProperties properties = new DeploymentProperties(List.of(), null, false, false, false, false, 0L);

        Deployment deployment = new Deployment(audioEmitter, collisionResultAdapter, deploymentObjectRegistry, particleEffectSpawner, scheduler, properties, state, itemEffect);
        deployment.processDeploymentResult(result);
        deployment.destroy(BULLET_DAMAGE);

        verify(itemEffect, never()).activatePerformances();
        verify(itemEffect, never()).rollbackPerformances();
        verify(deploymentObject, never()).remove();
        verify(particleEffectSpawner, never()).spawnParticleEffect(any(ParticleEffect.class), any(Location.class));
    }

    @Test
    @DisplayName("destroy activates item effect performances when activateEffectOnDestruction property is true and last damage is not environmental damage")
    void destroy_performsAllExtraActions() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        World actorWorld = mock(World.class);
        Location actorLocation = new Location(actorWorld, 1, 1, 1);

        Actor actor = mock(Actor.class);
        when(actor.getLocation()).thenReturn(actorLocation);

        DeploymentResult result = new DeploymentResult(null, deploymentObject, actor, 0L);

        Deployment deployment = new Deployment(audioEmitter, collisionResultAdapter, deploymentObjectRegistry, particleEffectSpawner, scheduler, DEFAULT_PROPERTIES, state, itemEffect);
        deployment.processDeploymentResult(result);
        deployment.destroy(BULLET_DAMAGE);

        verify(itemEffect).activatePerformances();
        verify(itemEffect).rollbackPerformances();
        verify(deploymentObject).remove();
        verify(particleEffectSpawner).spawnParticleEffect(DESTRUCTION_PARTICLE_EFFECT, actorLocation);
    }

    @Test
    @DisplayName("prepareActivator prepares the activator")
    void prepareActivator() {
        Activator activator = mock(Activator.class);
        Deployer deployer = mock(Deployer.class);

        Deployment deployment = new Deployment(audioEmitter, collisionResultAdapter, deploymentObjectRegistry, particleEffectSpawner, scheduler, DEFAULT_PROPERTIES, state, itemEffect);
        deployment.assignActivator(activator);
        deployment.prepareActivator(deployer);

        verify(activator).prepare(deployer);
    }

    @Test
    @DisplayName("processDeploymentResult delegates logic to state")
    void processDeploymentResult() {
        DeploymentResult result = new DeploymentResult(null, null, null, 0L);

        Deployment deployment = new Deployment(audioEmitter, collisionResultAdapter, deploymentObjectRegistry, particleEffectSpawner, scheduler, DEFAULT_PROPERTIES, state, itemEffect);
        deployment.processDeploymentResult(result);

        verify(state).processAction(deployment, result);
    }

    @Test
    @DisplayName("replaceActor replaces actor in current trigger runs and item performances")
    void replaceActor() {
        TriggerRun triggerRun = mock(TriggerRun.class);
        Actor newActor = mock(Actor.class);
        ItemEffectPerformance effectPerformance = mock(ItemEffectPerformance.class);

        Actor actor = mock(Actor.class);
        when(actor.getLocation()).thenReturn(ACTOR_LOCATION);

        Deployer deployer = mock(Deployer.class);
        when(deployer.getUniqueId()).thenReturn(DEPLOYER_UNIQUE_ID);

        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutor.createTriggerRun(any(TriggerContext.class))).thenReturn(triggerRun);

        when(itemEffect.getLatestPerformance()).thenReturn(Optional.of(effectPerformance));

        Deployment deployment = new Deployment(audioEmitter, collisionResultAdapter, deploymentObjectRegistry, particleEffectSpawner, scheduler, DEFAULT_PROPERTIES, state, itemEffect);
        deployment.addTriggerExecutor(triggerExecutor);
        deployment.startTriggerExecutors(deployer, actor);
        deployment.replaceActor(newActor);

        verify(triggerRun).replaceActor(newActor);
        verify(effectPerformance).changeActor(newActor);
    }

    @Test
    @DisplayName("scheduleDeploymentCooldown")
    void scheduleDeploymentCooldown() {
        Deployer deployer = mock(Deployer.class);

        Schedule schedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(schedule).addTask(any(ScheduleTask.class));

        when(scheduler.createSingleRunSchedule(COOLDOWN)).thenReturn(schedule);

        Deployment deployment = new Deployment(audioEmitter, collisionResultAdapter, deploymentObjectRegistry, particleEffectSpawner, scheduler, DEFAULT_PROPERTIES, state, itemEffect);
        deployment.scheduleDeploymentCooldown(deployer, COOLDOWN);

        verify(deployer).setCanDeploy(false);
        verify(deployer).setCanDeploy(true);
    }

    @Test
    @DisplayName("startTriggerExecutors starts trigger executors that activate item effect")
    void startTriggerExecutors() {
        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        TriggerResult triggerResult = mock(TriggerResult.class);
        CollisionResult collisionResult = new CollisionResult(null, null, null);

        Actor actor = mock(Actor.class);
        when(actor.getLocation()).thenReturn(ACTOR_LOCATION);

        Deployer deployer = mock(Deployer.class);
        when(deployer.getUniqueId()).thenReturn(DEPLOYER_UNIQUE_ID);

        TriggerRun triggerRun = mock(TriggerRun.class);
        doAnswer(MockUtils.answerNotifyTriggerObserver(triggerResult)).when(triggerRun).addObserver(any(TriggerObserver.class));

        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutor.createTriggerRun(any(TriggerContext.class))).thenReturn(triggerRun);

        DeploymentResult result = new DeploymentResult(deployer, deploymentObject, actor, COOLDOWN);

        when(collisionResultAdapter.adapt(triggerResult)).thenReturn(collisionResult);

        Deployment deployment = new Deployment(audioEmitter, collisionResultAdapter, deploymentObjectRegistry, particleEffectSpawner, scheduler, DEFAULT_PROPERTIES, state, itemEffect);
        deployment.addTriggerExecutor(triggerExecutor);
        deployment.processDeploymentResult(result);
        deployment.startTriggerExecutors(deployer, actor);

        ArgumentCaptor<TriggerContext> triggerContextCaptor = ArgumentCaptor.forClass(TriggerContext.class);
        verify(triggerExecutor).createTriggerRun(triggerContextCaptor.capture());

        assertThat(triggerContextCaptor.getValue()).satisfies(triggerContext -> {
            assertThat(triggerContext.sourceId()).isEqualTo(DEPLOYER_UNIQUE_ID);
            assertThat(triggerContext.actor()).isEqualTo(actor);
        });

        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        assertThat(itemEffectContextCaptor.getValue()).satisfies(itemEffectContext -> {
            assertThat(itemEffectContext.getActor()).isEqualTo(actor);
            assertThat(itemEffectContext.getCollisionResult()).isEqualTo(collisionResult);
            assertThat(itemEffectContext.getDamageSource()).isEqualTo(deployer);
            assertThat(itemEffectContext.getStartingLocation()).isEqualTo(ACTOR_LOCATION);
        });

        verify(triggerRun).start();
    }
}
