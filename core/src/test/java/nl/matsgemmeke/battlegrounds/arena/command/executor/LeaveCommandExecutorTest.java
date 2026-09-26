package nl.matsgemmeke.battlegrounds.arena.command.executor;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.fixture.LanguageFixture;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
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
import java.util.UUID;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveCommandExecutorTest {

    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String PLAYER_NAME = "TestPlayer";
    private static final TextTemplate NOT_IN_ARENA_MODE_TEXT_TEMPLATE = LanguageFixture.getTextTemplate(TranslationKey.NOT_IN_ARENA_MODE.getPath());

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
    private LeaveCommandExecutor commandExecutor;

    @Test
    @DisplayName("execute logs warning message when the given player is not in a game context")
    void execute_notInAnyGameContext() {
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(player.getName()).thenReturn(PLAYER_NAME);
        when(gameContextProvider.getGameContext(PLAYER_ID)).thenReturn(Optional.empty());
        when(translator.translate(TranslationKey.NOT_IN_ARENA_MODE.getPath())).thenReturn(NOT_IN_ARENA_MODE_TEXT_TEMPLATE);

        commandExecutor.execute(player);

        verify(logger).warning("Player TestPlayer attempts to leave its current arena, however they are not registered in a game context despite prior validation");
        verify(player).sendMessage("&cUnable to perform action; you are not participating in an arena.");
    }

    @Test
    @DisplayName("execute logs warning message when the given player is in a game context other than arena mode")
    void execute_notInArenaMode() {
        GameContext gameContext = mock(GameContext.class);
        when(gameContext.getType()).thenReturn(GameContextType.FREEPLAY_MODE);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(player.getName()).thenReturn(PLAYER_NAME);
        when(gameContextProvider.getGameContext(PLAYER_ID)).thenReturn(Optional.of(gameContext));
        when(translator.translate(TranslationKey.NOT_IN_ARENA_MODE.getPath())).thenReturn(NOT_IN_ARENA_MODE_TEXT_TEMPLATE);

        commandExecutor.execute(player);

        verify(logger).warning("Player TestPlayer attempts to leave its current arena, however they are not registered in a game context despite prior validation");
        verify(player).sendMessage("&cUnable to perform action; you are not participating in an arena.");
    }

    @Test
    @DisplayName("execute makes player leave arena mode using MembershipService")
    void execute_successful() {
        GameContext gameContext = mock(GameContext.class);
        when(gameContext.getType()).thenReturn(GameContextType.ARENA_MODE);

        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.getGameContext(PLAYER_ID)).thenReturn(Optional.of(gameContext));
        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(gameContext), any(Runnable.class));
        when(membershipServiceProvider.get()).thenReturn(membershipService);

        commandExecutor.execute(player);

        verify(membershipService).leave(player);
    }
}
