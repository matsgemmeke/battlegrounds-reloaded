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
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitResult;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.UUID;

public class ProjectileHitEventHandler implements EventHandler<ProjectileHitEvent> {

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Provider<ProjectileHitActionRegistry> projectileHitActionRegistryProvider;

    @Inject
    public ProjectileHitEventHandler(GameContextProvider gameContextProvider, GameScope gameScope, Provider<ProjectileHitActionRegistry> projectileHitActionRegistryProvider) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.projectileHitActionRegistryProvider = projectileHitActionRegistryProvider;
    }

    public void handle(ProjectileHitEvent event) {
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

        gameScope.runInScope(gameContext, () -> this.performProjectileAction(projectile, event));
    }

    private void performProjectileAction(Projectile projectile, ProjectileHitEvent event) {
        ProjectileHitActionRegistry projectileHitActionRegistry = projectileHitActionRegistryProvider.get();
        ProjectileHitAction projectileHitAction = projectileHitActionRegistry.getProjectileHitAction(projectile).orElse(null);

        if (projectileHitAction == null) {
            return;
        }

        ProjectileHitResult result = this.getProjectileHitResult(event);
        projectileHitAction.onProjectileHit(result);
    }

    private ProjectileHitResult getProjectileHitResult(ProjectileHitEvent event) {
        Block hitBlock = event.getHitBlock();
        Entity hitEntity = event.getHitEntity();

        return new ProjectileHitResult(hitBlock, hitEntity);
    }
}
