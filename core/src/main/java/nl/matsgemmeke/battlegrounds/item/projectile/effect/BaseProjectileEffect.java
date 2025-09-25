package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseProjectileEffect implements ProjectileEffect {

    @NotNull
    private final Set<TriggerExecutor> triggerExecutors;
    @NotNull
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

    public void onLaunch(@NotNull Entity deployerEntity, @NotNull Projectile projectile) {
        TriggerContext context = new TriggerContext(deployerEntity, projectile);

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

    public abstract void performEffect(@NotNull Projectile projectile);
}
