package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArenaModeAbsenceConditionTest {

    private static final int ARENA_ID = 1;
    private static final GameKey GAME_KEY = GameKey.ofArena(ARENA_ID);
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String ERROR_MESSAGE = "error";

    @Mock
    private BukkitCommandExecutionContext execContext;
    @Mock
    private BukkitCommandIssuer issuer;
    @Mock
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;
    @Mock
    private Player player;
    @Mock
    private PlayerRegistry playerRegistry;
    @Mock
    private Provider<PlayerRegistry> playerRegistryProvider;
    @Mock
    private Translator translator;
    @InjectMocks
    private ArenaModeAbsenceCondition condition;

    @BeforeEach
    void setUp() {
        when(conditionContext.getIssuer()).thenReturn(issuer);
    }

    @Test
    @DisplayName("validateCondition throws ConditionFailedException when given context has no player")
    void validateCondition_nullPlayer() {
        when(issuer.getPlayer()).thenReturn(null);
        when(translator.translate(TranslationKey.NOT_IN_ARENA_MODE.getPath())).thenReturn(new TextTemplate(ERROR_MESSAGE));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext, execContext, ARENA_ID))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(ERROR_MESSAGE);
    }

    @Test
    @DisplayName("validateCondition does nothing when a game context with the given arena id does not exist")
    void validateCondition_nonExistentGameContext() {
        when(issuer.getPlayer()).thenReturn(player);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());

        assertThatNoException().isThrownBy(() -> condition.validateCondition(conditionContext, execContext, ARENA_ID));
    }

    @Test
    @DisplayName("validateCondition throws ConditionFailedException when issuer is registered in the given arena")
    void validateCondition_issuerPresentInArena() {
        GameContext gameContext = new GameContext(GAME_KEY, GameContextType.ARENA_MODE);

        when(issuer.getPlayer()).thenReturn(player);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(playerRegistry.isRegistered(PLAYER_ID)).thenReturn(true);
        when(translator.translate(TranslationKey.NOT_IN_ARENA_MODE.getPath())).thenReturn(new TextTemplate(ERROR_MESSAGE));

        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(gameContext), any(Runnable.class));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext, execContext, ARENA_ID))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(ERROR_MESSAGE);
    }

    @Test
    @DisplayName("validateCondition does nothing when issuer is not registered in the given arena")
    void validateCondition_issuerNotPresentInArena() {
        GameContext gameContext = new GameContext(GAME_KEY, GameContextType.ARENA_MODE);

        when(issuer.getPlayer()).thenReturn(player);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(playerRegistry.isRegistered(PLAYER_ID)).thenReturn(false);

        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(gameContext), any(Runnable.class));

        assertThatNoException().isThrownBy(() -> condition.validateCondition(conditionContext, execContext, ARENA_ID));
    }
}
