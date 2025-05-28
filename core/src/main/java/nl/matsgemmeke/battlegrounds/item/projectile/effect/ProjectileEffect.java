package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface ProjectileEffect {

    void addTrigger(@NotNull Trigger trigger);

    void onLaunch(@NotNull Entity deployerEntity, @NotNull Projectile projectile);
}
