package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.fixture.LanguageFixture;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArenaModePresenceConditionTest {

    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String NOT_IN_ARENA_MODE_TEXT = LanguageFixture.getTranslation(TranslationKey.NOT_IN_ARENA_MODE.getPath());

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

    @Test
    @DisplayName("validateCondition throws ConditionFailedException when issuer is not a player")
    void validateCondition_issuerIsNoPlayer() {
        when(issuer.getPlayer()).thenReturn(null);
        when(translator.translate(TranslationKey.NOT_IN_ARENA_MODE.getPath())).thenReturn(new TextTemplate(NOT_IN_ARENA_MODE_TEXT));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage("&cUnable to perform action; you are not participating in an arena.");
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

    @Test
    @DisplayName("validationCondition throws ConditionFailedException when issuer is not in a game context")
    void validationCondition_issuerIsInNoGame() {
        when(issuer.getPlayer()).thenReturn(player);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.getGameContext(PLAYER_ID)).thenReturn(Optional.empty());
        when(translator.translate(TranslationKey.NOT_IN_ARENA_MODE.getPath())).thenReturn(new TextTemplate(NOT_IN_ARENA_MODE_TEXT));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage("&cUnable to perform action; you are not participating in an arena.");
    }

    @Test
    @DisplayName("validationCondition throws ConditionFailedException when issuer is a game context other than arena mode")
    void validationCondition_issuerIsNonArenaGameContext() {
        GameContext gameContext = mock(GameContext.class);
        when(gameContext.getType()).thenReturn(GameContextType.FREEPLAY_MODE);

        when(issuer.getPlayer()).thenReturn(player);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.getGameContext(PLAYER_ID)).thenReturn(Optional.of(gameContext));
        when(translator.translate(TranslationKey.NOT_IN_ARENA_MODE.getPath())).thenReturn(new TextTemplate(NOT_IN_ARENA_MODE_TEXT));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage("&cUnable to perform action; you are not participating in an arena.");
    }
}
