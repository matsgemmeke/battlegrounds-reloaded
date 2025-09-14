package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitAction;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ProjectileHitEventHandlerTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final GameContext GAME_CONTEXT = new GameContext(GAME_KEY, GameContextType.OPEN_MODE);
    private static final UUID PLAYER_ID = UUID.randomUUID();

    private GameContextProvider gameContextProvider;
    private GameScope gameScope;
    private Provider<ProjectileHitActionRegistry> projectileHitActionRegistryProvider;

    @BeforeEach
    public void setUp() {
        gameContextProvider = mock(GameContextProvider.class);
        gameScope = mock(GameScope.class);
        projectileHitActionRegistryProvider = mock();
    }

    @Test
    public void handleDoesNothingWhenProjectileSourceIsNoInstanceOfPlayer() {
        ProjectileSource projectileSource = mock(BlockProjectileSource.class);

        Projectile projectile = mock(Projectile.class);
        when(projectile.getShooter()).thenReturn(projectileSource);

        ProjectileHitEvent event = new ProjectileHitEvent(projectile);

        ProjectileHitEventHandler eventHandler = new ProjectileHitEventHandler(gameContextProvider, gameScope, projectileHitActionRegistryProvider);
        eventHandler.handle(event);

        verifyNoInteractions(gameContextProvider);
        verifyNoInteractions(gameScope);
    }

    @Test
    public void handleDoesNothingWhenNoGameKeyCanBeFoundForPlayer() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        Projectile projectile = mock(Projectile.class);
        when(projectile.getShooter()).thenReturn(player);

        ProjectileHitEvent event = new ProjectileHitEvent(projectile);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.empty());

        ProjectileHitEventHandler eventHandler = new ProjectileHitEventHandler(gameContextProvider, gameScope, projectileHitActionRegistryProvider);
        eventHandler.handle(event);

        verifyNoInteractions(gameScope);
    }

    @Test
    public void handleThrowsEventHandlingExceptionWhenGameContextCannotBeFoundForGameKey() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        Projectile projectile = mock(Projectile.class);
        when(projectile.getShooter()).thenReturn(player);

        ProjectileHitEvent event = new ProjectileHitEvent(projectile);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        ProjectileHitEventHandler eventHandler = new ProjectileHitEventHandler(gameContextProvider, gameScope, projectileHitActionRegistryProvider);

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process ProjectileHitEvent for game key OPEN-MODE, no corresponding game context was found");

        verifyNoInteractions(gameScope);
    }

    @Test
    public void handleCallsProjectileHitActionOnMatchingProjectileEntity() {
        ProjectileHitAction projectileHitAction = mock(ProjectileHitAction.class);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        Projectile projectile = mock(Projectile.class);
        when(projectile.getShooter()).thenReturn(player);

        ProjectileHitEvent event = new ProjectileHitEvent(projectile);

        ProjectileHitActionRegistry projectileHitActionRegistry = mock(ProjectileHitActionRegistry.class);
        when(projectileHitActionRegistry.getProjectileHitAction(projectile)).thenReturn(Optional.of(projectileHitAction));

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(projectileHitActionRegistryProvider.get()).thenReturn(projectileHitActionRegistry);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        ProjectileHitEventHandler eventHandler = new ProjectileHitEventHandler(gameContextProvider, gameScope, projectileHitActionRegistryProvider);
        eventHandler.handle(event);

        verify(projectileHitAction).onProjectileHit();
    }
}
