package nl.matsgemmeke.battlegrounds.event.handler;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.event.EventHandlingException;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitAction;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectileHitEventHandlerTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final GameContext GAME_CONTEXT = new GameContext(GAME_KEY, GameContextType.OPEN_MODE);
    private static final UUID PLAYER_ID = UUID.randomUUID();

    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;
    @Mock
    private Provider<ProjectileHitActionRegistry> projectileHitActionRegistryProvider;
    @InjectMocks
    private ProjectileHitEventHandler eventHandler;

    @Test
    void handleDoesNothingWhenProjectileSourceIsNoInstanceOfPlayer() {
        ProjectileSource projectileSource = mock(BlockProjectileSource.class);

        Projectile projectile = mock(Projectile.class);
        when(projectile.getShooter()).thenReturn(projectileSource);

        ProjectileHitEvent event = new ProjectileHitEvent(projectile);

        eventHandler.handle(event);

        verifyNoInteractions(gameContextProvider);
        verifyNoInteractions(gameScope);
    }

    @Test
    void handleDoesNothingWhenNoGameKeyCanBeFoundForPlayer() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        Projectile projectile = mock(Projectile.class);
        when(projectile.getShooter()).thenReturn(player);

        ProjectileHitEvent event = new ProjectileHitEvent(projectile);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.empty());

        eventHandler.handle(event);

        verifyNoInteractions(gameScope);
    }

    @Test
    void handleThrowsEventHandlingExceptionWhenGameContextCannotBeFoundForGameKey() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        Projectile projectile = mock(Projectile.class);
        when(projectile.getShooter()).thenReturn(player);

        ProjectileHitEvent event = new ProjectileHitEvent(projectile);

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventHandler.handle(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Unable to process ProjectileHitEvent for game key OPEN-MODE, no corresponding game context was found");

        verifyNoInteractions(gameScope);
    }

    @Test
    void handleDoesNothingWhenNoProjectileHitActionWasFound() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        Projectile projectile = mock(Projectile.class);
        when(projectile.getShooter()).thenReturn(player);

        ProjectileHitEvent event = new ProjectileHitEvent(projectile);

        ProjectileHitActionRegistry projectileHitActionRegistry = mock(ProjectileHitActionRegistry.class);
        when(projectileHitActionRegistry.getProjectileHitAction(projectile)).thenReturn(Optional.empty());

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(projectileHitActionRegistryProvider.get()).thenReturn(projectileHitActionRegistry);
        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        assertThatCode(() -> eventHandler.handle(event)).doesNotThrowAnyException();
    }

    @Test
    void handleCallsProjectileHitActionWithHitEntityLocation() {
        ProjectileHitAction projectileHitAction = mock(ProjectileHitAction.class);
        Location entityLocation = new Location(null, 1, 1, 1);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        Projectile projectile = mock(Projectile.class);
        when(projectile.getShooter()).thenReturn(player);

        Entity entity = mock(Entity.class);
        when(entity.getLocation()).thenReturn(entityLocation);

        ProjectileHitEvent event = new ProjectileHitEvent(projectile, entity);

        ProjectileHitActionRegistry projectileHitActionRegistry = mock(ProjectileHitActionRegistry.class);
        when(projectileHitActionRegistry.getProjectileHitAction(projectile)).thenReturn(Optional.of(projectileHitAction));

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(projectileHitActionRegistryProvider.get()).thenReturn(projectileHitActionRegistry);
        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        eventHandler.handle(event);

        verify(projectileHitAction).onProjectileHit(entityLocation);
    }

    @Test
    void handleCallsProjectileHitActionWithHitBlockLocation() {
        ProjectileHitAction projectileHitAction = mock(ProjectileHitAction.class);
        Location blockLocation = new Location(null, 1, 1, 1);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        Projectile projectile = mock(Projectile.class);
        when(projectile.getShooter()).thenReturn(player);

        Block block = mock(Block.class);
        when(block.getLocation()).thenReturn(blockLocation);

        ProjectileHitEvent event = new ProjectileHitEvent(projectile, block);

        ProjectileHitActionRegistry projectileHitActionRegistry = mock(ProjectileHitActionRegistry.class);
        when(projectileHitActionRegistry.getProjectileHitAction(projectile)).thenReturn(Optional.of(projectileHitAction));

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(projectileHitActionRegistryProvider.get()).thenReturn(projectileHitActionRegistry);
        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        eventHandler.handle(event);

        verify(projectileHitAction).onProjectileHit(blockLocation);
    }

    @Test
    void handleCallsProjectileHitActionWithProjectileLocation() {
        ProjectileHitAction projectileHitAction = mock(ProjectileHitAction.class);
        Location projectileLocation = new Location(null, 1, 1, 1);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        Projectile projectile = mock(Projectile.class);
        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getShooter()).thenReturn(player);

        ProjectileHitEvent event = new ProjectileHitEvent(projectile);

        ProjectileHitActionRegistry projectileHitActionRegistry = mock(ProjectileHitActionRegistry.class);
        when(projectileHitActionRegistry.getProjectileHitAction(projectile)).thenReturn(Optional.of(projectileHitAction));

        when(gameContextProvider.getGameKeyByEntityId(PLAYER_ID)).thenReturn(Optional.of(GAME_KEY));
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(projectileHitActionRegistryProvider.get()).thenReturn(projectileHitActionRegistry);
        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        eventHandler.handle(event);

        verify(projectileHitAction).onProjectileHit(projectileLocation);
    }
}
