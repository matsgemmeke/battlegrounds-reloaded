package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseProjectileEffect implements ProjectileEffect {

    @NotNull
    private Set<Trigger> triggers;

    public BaseProjectileEffect() {
        this.triggers = new HashSet<>();
    }

    public void addTrigger(@NotNull Trigger trigger) {
        triggers.add(trigger);
    }

    protected void deactivateTriggers() {
        triggers.forEach(Trigger::deactivate);
    }

    public void onLaunch(@NotNull Entity deployerEntity, @NotNull Projectile projectile) {
        TriggerContext context = new TriggerContext(deployerEntity, projectile);

        for (Trigger trigger : triggers) {
            trigger.addObserver(() -> this.performEffect(projectile));
            trigger.activate(context);
        }
    }

    public abstract void performEffect(@NotNull Projectile projectile);
}
