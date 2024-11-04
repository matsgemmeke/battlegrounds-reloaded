package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Activation that initiates the effect after a specific delay.
 */
public class DelayedActivation extends BaseItemEffectActivation {

    private long delayUntilActivation;
    @NotNull
    private TaskRunner taskRunner;

    public DelayedActivation(@NotNull ItemEffect effect, @NotNull TaskRunner taskRunner, long delayUntilActivation) {
        super(effect);
        this.taskRunner = taskRunner;
        this.delayUntilActivation = delayUntilActivation;
    }

    public void prime(@NotNull ItemHolder holder, @NotNull EffectSource source) {
        ItemEffectContext mostRecentContext = this.getMostRecentContext();

        if (source.isDeployed()) {
            holder.setHeldItem(null);
        }

        // If the most recent context is a pending deployment, do not schedule a new delay and only replace the source
        if (mostRecentContext != null && !mostRecentContext.getSource().isDeployed()) {
            mostRecentContext.setSource(source);
            return;
        }

        ItemEffectContext context = new ItemEffectContext(holder, source);
        contexts.add(context);

        BukkitTask task = taskRunner.runTaskLater(this::activateEffect, delayUntilActivation);
        tasks.add(task);
    }

    @Nullable
    private ItemEffectContext getMostRecentContext() {
        if (contexts.isEmpty()) {
            return null;
        }

        return contexts.get(contexts.size() - 1);
    }

    private void activateEffect() {
        ItemEffectContext context = this.getOldestContext();

        if (context == null || !context.getSource().exists()) {
            return;
        }

        effect.activate(context);
    }

    @Nullable
    private ItemEffectContext getOldestContext() {
        if (contexts.isEmpty()) {
            return null;
        }

        return contexts.get(0);
    }
}
