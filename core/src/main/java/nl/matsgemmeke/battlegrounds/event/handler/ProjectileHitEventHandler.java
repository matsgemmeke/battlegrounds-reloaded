package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitAction;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ProjectileHitEventHandler implements EventHandler<ProjectileHitEvent> {

    @NotNull
    private final GameContextProvider contextProvider;

    @Inject
    public ProjectileHitEventHandler(@NotNull GameContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void handle(@NotNull ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        for (GameKey gameKey : contextProvider.getGameKeys()) {
            ProjectileHitActionRegistry projectileHitActionRegistry = contextProvider.getComponent(gameKey, ProjectileHitActionRegistry.class);

            Optional<ProjectileHitAction> projectileHitAction = projectileHitActionRegistry.getProjectileHitAction(projectile);
            projectileHitAction.ifPresent(ProjectileHitAction::onProjectileHit);
        }
    }
}
