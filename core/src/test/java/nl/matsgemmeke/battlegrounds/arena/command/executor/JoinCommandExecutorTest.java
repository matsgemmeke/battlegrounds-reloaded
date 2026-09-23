package nl.matsgemmeke.battlegrounds.arena.command.executor;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.arena.ArenaGameContext;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.membership.JoinResult;
import nl.matsgemmeke.battlegrounds.game.component.membership.MembershipService;
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
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoinCommandExecutorTest {

    private static final int ARENA_ID = 1;
    private static final GameKey GAME_KEY = GameKey.ofArena(ARENA_ID);
    private static final ArenaGameContext GAME_CONTEXT = new ArenaGameContext(GAME_KEY, null);
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
    private MembershipService membershipService;
    @Mock
    private Player player;
    @Mock
    private Provider<MembershipService> membershipServiceProvider;
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
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));
        when(membershipServiceProvider.get()).thenReturn(membershipService);
        when(membershipService.join(player)).thenReturn(JoinResult.ALREADY_IN_GAME);
        when(player.getName()).thenReturn(PLAYER_NAME);
        when(translator.translate(TranslationKey.ALREADY_IN_ARENA_MODE.getPath())).thenReturn(new TextTemplate(ALREADY_IN_ARENA_MODE_MESSAGE));

        commandExecutor.execute(player, ARENA_ID);

        verify(logger).warning("Player TestPlayer passed arena join validation, but was already in game");
        verify(player).sendMessage(ALREADY_IN_ARENA_MODE_MESSAGE);
    }
}
