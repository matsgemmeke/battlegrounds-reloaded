package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.freeplay.FreeplayGameContext;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FreeplayModePresenceConditionTest {

    private static final GameKey GAME_KEY = GameKey.ofFreeplay();
    private static final FreeplayGameContext GAME_CONTEXT = new FreeplayGameContext();
    private static final UUID PLAYER_ID = UUID.randomUUID();

    @Mock
    private BukkitCommandIssuer issuer;
    @Mock
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private GameScope gameScope;
    @Mock
    private Provider<PlayerRegistry> playerRegistryProvider;
    @Mock
    private Translator translator;
    @InjectMocks
    private FreeplayModePresenceCondition condition;

    @BeforeEach
    void setUp() {
        when(conditionContext.getIssuer()).thenReturn(issuer);
    }

    @ParameterizedTest
    @ValueSource(strings = { "error" })
    @NullSource
    @DisplayName("validateCondition throws ConditionFailedException when the condition context has no player")
    void validateCondition_conditionContextWithoutPlayer(String errorMessage) {
        when(issuer.getPlayer()).thenReturn(null);
        when(translator.translateLegacyText(TranslationKey.NOT_IN_FREEPLAY_MODE.getPath())).thenReturn(Optional.ofNullable(errorMessage));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(errorMessage);
    }

    @ParameterizedTest
    @ValueSource(strings = { "error" })
    @NullSource
    @DisplayName("validateCondition throws ConditionFailedException when freeplay mode context does not exists")
    void validateCondition_freeplayModeNotExists(String errorMessage) {
        Player player = mock(Player.class);

        when(issuer.getPlayer()).thenReturn(player);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());
        when(translator.translateLegacyText(TranslationKey.FREEPLAY_MODE_NOT_EXISTS.getPath())).thenReturn(Optional.ofNullable(errorMessage));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(errorMessage);
    }

    @Test
    @DisplayName("validateCondition does nothing when player is registered in freeplay mode")
    void validateCondition_passes() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.isRegistered(PLAYER_ID)).thenReturn(true);

        when(issuer.getPlayer()).thenReturn(player);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);

        condition.validateCondition(conditionContext);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(gameScope).runInScope(eq(GAME_CONTEXT), runnableCaptor.capture());

        assertThatCode(() -> runnableCaptor.getValue().run()).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = { "error" })
    @NullSource
    @DisplayName("validateCondition throws ConditionFailedException when player is not in freeplay mode")
    void validationCondition_playerNotInFreeplayMode(String errorMessage) {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.isRegistered(PLAYER_ID)).thenReturn(false);

        when(issuer.getPlayer()).thenReturn(player);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(GAME_CONTEXT));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(translator.translateLegacyText(TranslationKey.NOT_IN_FREEPLAY_MODE.getPath())).thenReturn(Optional.ofNullable(errorMessage));
        doAnswer(MockUtils.answerRunGameScopeRunnable()).when(gameScope).runInScope(eq(GAME_CONTEXT), any(Runnable.class));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(errorMessage);
    }
}
