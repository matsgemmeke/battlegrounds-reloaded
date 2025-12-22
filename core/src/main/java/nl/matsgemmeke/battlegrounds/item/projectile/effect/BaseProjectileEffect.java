package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import org.bukkit.entity.Entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class BaseProjectileEffect implements ProjectileEffect {

    private final Set<TriggerExecutor> triggerExecutors;
    private final Set<TriggerRun> triggerRuns;

    public BaseProjectileEffect() {
        this.triggerExecutors = new HashSet<>();
        this.triggerRuns = new HashSet<>();
    }

    public void addTriggerExecutor(TriggerExecutor triggerExecutor) {
        triggerExecutors.add(triggerExecutor);
    }

    protected void cancelTriggerRuns() {
        triggerRuns.forEach(TriggerRun::cancel);
        triggerRuns.clear();
    }

    public void onLaunch(Entity deployerEntity, Projectile projectile) {
        UUID sourceId = deployerEntity.getUniqueId();
        TriggerContext context = new TriggerContext(sourceId, projectile);

        for (TriggerExecutor triggerExecutor : triggerExecutors) {
            TriggerRun triggerRun = triggerExecutor.createTriggerRun(context);
            triggerRun.addObserver(() -> this.verifyAndPerformEffect(projectile));
            triggerRun.start();

            triggerRuns.add(triggerRun);
        }
    }

    private void verifyAndPerformEffect(Projectile projectile) {
        if (!projectile.exists()) {
            this.cancelTriggerRuns();
            return;
        }

        this.performEffect(projectile);
    }

    public abstract void performEffect(Projectile projectile);
}
