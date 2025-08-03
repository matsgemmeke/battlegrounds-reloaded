package nl.matsgemmeke.battlegrounds.game.component.projectile;

import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProjectileHitActionRegistry {

    @NotNull
    private final Map<Projectile, ProjectileHitAction> projectileHitActions;

    public ProjectileHitActionRegistry() {
        this.projectileHitActions = new HashMap<>();
    }

    public Optional<ProjectileHitAction> getProjectileHitAction(Projectile projectile) {
        return Optional.ofNullable(projectileHitActions.get(projectile));
    }

    public void registerProjectileHitAction(Projectile projectile, ProjectileHitAction projectileHitAction) {
        projectileHitActions.put(projectile, projectileHitAction);
    }

    public void removeProjectileHitAction(Projectile projectile) {
        projectileHitActions.remove(projectile);
    }
}
