package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import org.jetbrains.annotations.NotNull;

public interface ProjectileEffect {

    void onLaunch(@NotNull Projectile projectile);
}
