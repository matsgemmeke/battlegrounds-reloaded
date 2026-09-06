package nl.matsgemmeke.battlegrounds.arena.command.executor;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoinCommandExecutorTest {

    private static final int ARENA_ID = 1;
    private static final GameKey GAME_KEY = GameKey.ofArena(ARENA_ID);

    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String PLAYER_NAME = "TestPlayer";

    private static final String ARENA_NOT_AVAILABLE_MESSAGE = "arena not available";
    private static final String ALREADY_IN_ARENA_MODE_MESSAGE = "already in arena mode";

    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;
    @Mock
    private Logger logger;
    @Mock
    private Player player;
    @Mock
    private PlayerRegistry playerRegistry;
    @Mock
    private Provider<PlayerRegistry> playerRegistryProvider;
    @Mock
    private Translator translator;
    @InjectMocks
    private JoinCommandExecutor commandExecutor;

    @Test
    @DisplayName("execute logs warning message when the given arena id is somehow not a valid game key after validation")
    void execute_unknownGameContext() {
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());
        when(translator.translate(TranslationKey.ARENA_NOT_AVAILABLE.getPath())).thenReturn(new TextTemplate(ARENA_NOT_AVAILABLE_MESSAGE));
        when(player.getName()).thenReturn(PLAYER_NAME);

        commandExecutor.execute(player, ARENA_ID);

        verify(logger).warning("Player TestPlayer attempted to join arena 1, however no game context was found for validated game key ARENA-1");
        verify(player).sendMessage(ARENA_NOT_AVAILABLE_MESSAGE);
    }

    @Test
    @DisplayName("execute logs warning message when the player is somehow already registered in the arena after validation")
    void execute_playerAlreadyRegistered() {
        GameContext gameContext = new GameContext(GAME_KEY, GameContextType.ARENA_MODE);

        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(gameContext), any(Runnable.class));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(playerRegistry.isRegistered(PLAYER_ID)).thenReturn(true);
        when(player.getName()).thenReturn(PLAYER_NAME);
        when(translator.translate(TranslationKey.ALREADY_IN_ARENA_MODE.getPath())).thenReturn(new TextTemplate(ALREADY_IN_ARENA_MODE_MESSAGE));

        commandExecutor.execute(player, ARENA_ID);

        verify(logger).warning("Player TestPlayer passed arena join validation, but was already registered in its player registry");
        verify(player).sendMessage(ALREADY_IN_ARENA_MODE_MESSAGE);
        verify(playerRegistry, never()).register(any(Player.class));
    }

    @Test
    @DisplayName("execute registers player to player registry")
    void execute_successful() {
        GameContext gameContext = new GameContext(GAME_KEY, GameContextType.ARENA_MODE);

        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(gameContext), any(Runnable.class));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(playerRegistry.isRegistered(PLAYER_ID)).thenReturn(false);

        commandExecutor.execute(player, ARENA_ID);

        verify(playerRegistry).register(player);
    }
}
