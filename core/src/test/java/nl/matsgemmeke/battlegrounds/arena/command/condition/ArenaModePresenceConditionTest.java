package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArenaModePresenceConditionTest {

    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String NOT_IN_ARENA_MODE_TEXT = "not in arena mode";

    @Mock
    private BukkitCommandIssuer issuer;
    @Mock
    private ConditionContext<BukkitCommandIssuer> conditionContext;
    @Mock
    private GameContextProvider gameContextProvider;
    @Mock
    private Player player;
    @Mock
    private Translator translator;
    @InjectMocks
    private ArenaModePresenceCondition condition;

    @BeforeEach
    void setUp() {
        when(conditionContext.getIssuer()).thenReturn(issuer);
    }

    @ParameterizedTest
    @ValueSource(strings = { NOT_IN_ARENA_MODE_TEXT })
    @NullSource
    @DisplayName("validateCondition throws ConditionFailedException when issuer is not a player")
    void validateCondition_issuerIsNoPlayer(String message) {
        when(issuer.getPlayer()).thenReturn(null);
        when(translator.translateLegacyText(TranslationKey.NOT_IN_ARENA_MODE.getPath())).thenReturn(Optional.ofNullable(message));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(message);
    }

    @Test
    @DisplayName("validationCondition does nothing when the issuer is in an arena game context")
    void validationCondition_issuerIsInArenaMode() {
        GameContext gameContext = mock(GameContext.class);
        when(gameContext.getType()).thenReturn(GameContextType.ARENA_MODE);

        when(issuer.getPlayer()).thenReturn(player);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.getGameContext(PLAYER_ID)).thenReturn(Optional.of(gameContext));

        assertThatNoException().isThrownBy(() -> condition.validateCondition(conditionContext));
    }

    @ParameterizedTest
    @ValueSource(strings = { NOT_IN_ARENA_MODE_TEXT })
    @NullSource
    @DisplayName("validationCondition throws ConditionFailedException when issuer is not in a game context")
    void validationCondition_issuerIsInNoGame(String message) {
        when(issuer.getPlayer()).thenReturn(player);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.getGameContext(PLAYER_ID)).thenReturn(Optional.empty());
        when(translator.translateLegacyText(TranslationKey.NOT_IN_ARENA_MODE.getPath())).thenReturn(Optional.ofNullable(message));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(message);
    }

    @ParameterizedTest
    @ValueSource(strings = { NOT_IN_ARENA_MODE_TEXT })
    @NullSource
    @DisplayName("validationCondition throws ConditionFailedException when issuer is a game context other than arena mode")
    void validationCondition_issuerIsNonArenaGameContext(String message) {
        GameContext gameContext = mock(GameContext.class);
        when(gameContext.getType()).thenReturn(GameContextType.FREEPLAY_MODE);

        when(issuer.getPlayer()).thenReturn(player);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.getGameContext(PLAYER_ID)).thenReturn(Optional.of(gameContext));
        when(translator.translateLegacyText(TranslationKey.NOT_IN_ARENA_MODE.getPath())).thenReturn(Optional.ofNullable(message));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(message);
    }
}
