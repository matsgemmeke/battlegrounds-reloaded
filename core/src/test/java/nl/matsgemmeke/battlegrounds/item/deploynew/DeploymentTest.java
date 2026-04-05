package nl.matsgemmeke.battlegrounds.item.deploynew;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentResult;
import nl.matsgemmeke.battlegrounds.item.deploynew.state.DeploymentState;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.CollisionResultAdapter;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeploymentTest {

    private static final UUID DEPLOYER_UNIQUE_ID = UUID.randomUUID();
    private static final Location ACTOR_LOCATION = new Location(null, 1, 1, 1);
    private static final long COOLDOWN = 10L;

    @Mock
    private CollisionResultAdapter collisionResultAdapter;
    @Mock
    private DeploymentState state;
    @Mock
    private ItemEffect itemEffect;
    @Mock
    private Scheduler scheduler;
    @InjectMocks
    private Deployment deployment;

    @Test
    @DisplayName("processDeploymentResult delegates logic to state")
    void processDeploymentResult() {
        DeploymentResult result = new DeploymentResult(null, null, null, 0L);

        deployment.processDeploymentResult(result);

        verify(state).processAction(deployment, result);
    }

    @Test
    @DisplayName("scheduleDeploymentCooldown")
    void scheduleDeploymentCooldown() {
        Deployer deployer = mock(Deployer.class);

        Schedule schedule = mock(Schedule.class);
        doAnswer(MockUtils.answerRunScheduleTask()).when(schedule).addTask(any(ScheduleTask.class));

        when(scheduler.createSingleRunSchedule(COOLDOWN)).thenReturn(schedule);

        deployment.scheduleDeploymentCooldown(deployer, COOLDOWN);

        verify(deployer).setCanDeploy(false);
        verify(deployer).setCanDeploy(true);
    }

    @Test
    @DisplayName("startTriggerExecutors starts trigger executors that activate item effect")
    void startTriggerExecutors() {
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

        when(collisionResultAdapter.adapt(triggerResult)).thenReturn(collisionResult);

        deployment.addTriggerExecutor(triggerExecutor);
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
