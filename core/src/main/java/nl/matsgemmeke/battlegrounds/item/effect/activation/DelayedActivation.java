package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
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
        this.addEffectSource(source);

        if (source.isDeployed()) {
            holder.setHeldItem(null);
        }

        BukkitTask task = taskRunner.runTaskLater(() -> this.activateEffect(holder), delayUntilActivation);
        tasks.add(task);
    }

    private void addEffectSource(@NotNull EffectSource source) {
        EffectSource mostRecentSource = sources.isEmpty() ? null : sources.get(sources.size() - 1);

        if (mostRecentSource == null || mostRecentSource.isDeployed()) {
            sources.add(source);
        } else {
            sources.set(sources.indexOf(mostRecentSource), source);
        }
    }

    private void activateEffect(@NotNull ItemHolder holder) {
        EffectSource source = this.getOldestSource();

        if (source == null || !source.exists()) {
            return;
        }

        effect.activate(holder, source);
        sources.remove(source);
    }

    @Nullable
    private EffectSource getOldestSource() {
        if (sources.isEmpty()) {
            return null;
        }

        return sources.get(0);
    }
}
