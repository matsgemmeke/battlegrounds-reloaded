package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandler;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitAction;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class ProjectileHitEventHandler implements EventHandler<ProjectileHitEvent> {

    @NotNull
    private final GameContextProvider gameContextProvider;
    @NotNull
    private final GameScope gameScope;
    @NotNull
    private final Provider<ProjectileHitActionRegistry> projectileHitActionRegistryProvider;

    @Inject
    public ProjectileHitEventHandler(@NotNull GameContextProvider gameContextProvider, @NotNull GameScope gameScope, @NotNull Provider<ProjectileHitActionRegistry> projectileHitActionRegistryProvider) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.projectileHitActionRegistryProvider = projectileHitActionRegistryProvider;
    }

    public void handle(@NotNull ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        ProjectileSource projectileSource = projectile.getShooter();

        if (!(projectileSource instanceof Player player)) {
            return;
        }

        UUID playerId = player.getUniqueId();
        GameKey gameKey = gameContextProvider.getGameKeyByEntityId(playerId).orElse(null);

        if (gameKey == null) {
            return;
        }

        GameContext gameContext = gameContextProvider.getGameContext(gameKey)
                .orElseThrow(() -> new EventHandlingException("Unable to process ProjectileHitEvent for game key %s, no corresponding game context was found".formatted(gameKey)));

        gameScope.runInScope(gameContext, () -> this.performProjectileAction(projectile));
    }

    private void performProjectileAction(Projectile projectile) {
        ProjectileHitActionRegistry projectileHitActionRegistry = projectileHitActionRegistryProvider.get();

        Optional<ProjectileHitAction> projectileHitAction = projectileHitActionRegistry.getProjectileHitAction(projectile);
        projectileHitAction.ifPresent(ProjectileHitAction::onProjectileHit);
    }
}
