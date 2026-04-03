package nl.matsgemmeke.battlegrounds.item.deploynew;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentObjectRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.deploynew.state.DeploymentState;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.CollisionResultAdapter;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a deployment process.
 */
public class Deployment {

    private final CollisionResultAdapter collisionResultAdapter;
    private final DeploymentObjectRegistry deploymentObjectRegistry;
    private final ItemEffect itemEffect;
    private final Scheduler scheduler;
    private final Set<TriggerExecutor> triggerExecutors;
    private final Set<TriggerRun> triggerRuns;
    private boolean performing;
    private DeploymentState state;

    @Inject
    public Deployment(
            CollisionResultAdapter collisionResultAdapter,
            DeploymentObjectRegistry deploymentObjectRegistry,
            Scheduler scheduler,
            @Assisted DeploymentState state,
            @Assisted ItemEffect itemEffect
    ) {
        this.collisionResultAdapter = collisionResultAdapter;
        this.deploymentObjectRegistry = deploymentObjectRegistry;
        this.scheduler = scheduler;
        this.state = state;
        this.itemEffect = itemEffect;
        this.triggerExecutors = new HashSet<>();
        this.triggerRuns = new HashSet<>();
    }

    public boolean isPerforming() {
        return performing;
    }

    public void setPerforming(boolean performing) {
        this.performing = performing;
    }

    public void addTriggerExecutor(TriggerExecutor triggerExecutor) {
        triggerExecutors.add(triggerExecutor);
    }

    public void processAction(DeploymentAction action) {
        state = state.processAction(this, action);
    }

    public void scheduleDeploymentCooldown(Deployer deployer, long cooldown) {
        deployer.setCanDeploy(false);

        Schedule delaySchedule = scheduler.createSingleRunSchedule(cooldown);
        delaySchedule.addTask(() -> deployer.setCanDeploy(true));
        delaySchedule.start();
    }

    public void startTriggerExecutors(Deployer deployer, Actor actor) {
        UUID sourceId = deployer.getUniqueId();
        Location startingLocation = actor.getLocation();
        TriggerContext triggerContext = new TriggerContext(sourceId, actor);

        for (TriggerExecutor triggerExecutor : triggerExecutors) {
            TriggerRun triggerRun = triggerExecutor.createTriggerRun(triggerContext);
            triggerRun.addObserver(triggerResult -> this.activateEffect(triggerResult, deployer, actor, startingLocation));
            triggerRun.start();

            triggerRuns.add(triggerRun);
        }
    }

    private void activateEffect(TriggerResult triggerResult, DamageSource damageSource, Actor actor, Location startingLocation) {
        CollisionResult collisionResult = collisionResultAdapter.adapt(triggerResult);
        ItemEffectContext effectContext = new ItemEffectContext(collisionResult, damageSource, actor, startingLocation);

        itemEffect.startPerformance(effectContext);
    }
}
