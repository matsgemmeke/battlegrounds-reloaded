package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface ProjectileEffect {

    void onLaunch(@NotNull Entity deployerEntity, @NotNull Projectile projectile);
}
