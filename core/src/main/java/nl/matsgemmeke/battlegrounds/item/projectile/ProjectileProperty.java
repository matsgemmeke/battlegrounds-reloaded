package nl.matsgemmeke.battlegrounds.item.projectile;

import org.jetbrains.annotations.NotNull;

public interface ProjectileProperty {

    void onLaunch(@NotNull Projectile projectile);
}
