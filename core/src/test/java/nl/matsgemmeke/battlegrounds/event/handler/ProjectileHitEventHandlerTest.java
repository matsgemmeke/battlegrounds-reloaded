package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitAction;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

public class ProjectileHitEventHandlerTest {

    private GameContextProvider contextProvider;

    @BeforeEach
    public void setUp() {
        contextProvider = mock(GameContextProvider.class);
    }

    @Test
    public void handleChecksAllRegisteredGamesAndCallsProjectileHitActionOnMatchingProjectileEntity() {
        GameKey gameKey = GameKey.ofOpenMode();
        Projectile projectile = mock(Projectile.class);
        ProjectileHitAction projectileHitAction = mock(ProjectileHitAction.class);
        ProjectileHitEvent event = new ProjectileHitEvent(projectile);

        ProjectileHitActionRegistry projectileHitActionRegistry = mock(ProjectileHitActionRegistry.class);
        when(projectileHitActionRegistry.getProjectileHitAction(projectile)).thenReturn(Optional.of(projectileHitAction));

        when(contextProvider.getGameKeys()).thenReturn(Set.of(gameKey));
        when(contextProvider.getComponent(gameKey, ProjectileHitActionRegistry.class)).thenReturn(projectileHitActionRegistry);

        ProjectileHitEventHandler eventHandler = new ProjectileHitEventHandler(contextProvider);
        eventHandler.handle(event);

        verify(projectileHitAction).onProjectileHit();
    }
}
