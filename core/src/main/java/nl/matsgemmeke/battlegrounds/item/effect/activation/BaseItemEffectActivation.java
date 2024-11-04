package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
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
    protected List<ItemEffectContext> contexts;

    public BaseItemEffectActivation(@NotNull ItemEffect effect) {
        this.effect = effect;
        this.tasks = new ArrayList<>();
        this.contexts = new ArrayList<>();
    }

    public void activateInstantly(@NotNull ItemHolder holder) {
        for (ItemEffectContext context : contexts) {
            effect.activate(context);
        }

        for (BukkitTask task : tasks) {
            task.cancel();
        }

        contexts.clear();
        tasks.clear();
    }

    public boolean isAwaitingDeployment() {
        return !contexts.isEmpty() && !contexts.get(contexts.size() - 1).getSource().isDeployed();
    }
}
