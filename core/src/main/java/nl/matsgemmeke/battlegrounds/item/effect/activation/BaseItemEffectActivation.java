package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseItemEffectActivation implements ItemEffectActivation {

    @NotNull
    protected ItemEffect effect;
    @NotNull
    protected List<BukkitTask> tasks;
    @NotNull
    protected List<EffectSource> sources;

    public BaseItemEffectActivation(@NotNull ItemEffect effect) {
        this.effect = effect;
        this.sources = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    public void activateInstantly(@NotNull ItemHolder holder) {
        for (EffectSource source : sources) {
            effect.activate(holder, source);
        }

        for (BukkitTask task : tasks) {
            task.cancel();
        }

        sources.clear();
        tasks.clear();
    }

    public boolean isPrimed() {
        return !sources.isEmpty() && !sources.get(sources.size() - 1).isDeployed();
    }
}
