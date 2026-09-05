package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import nl.matsgemmeke.battlegrounds.game.*;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArenaModeAbsenceConditionTest {

    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String ERROR_MESSAGE = "error";

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
    private ArenaModeAbsenceCondition condition;

    @BeforeEach
    void setUp() {
        when(conditionContext.getIssuer()).thenReturn(issuer);
    }

    @Test
    @DisplayName("validateCondition does nothing when given context has no player")
    void validateCondition_nullPlayer() {
        when(issuer.getPlayer()).thenReturn(null);

        assertThatNoException().isThrownBy(() -> condition.validateCondition(conditionContext));
    }

    @Test
    @DisplayName("validateCondition does nothing when the player issuer is not in a game context")
    void validateCondition_nonExistentGameContext() {
        when(issuer.getPlayer()).thenReturn(player);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.getGameContext(PLAYER_ID)).thenReturn(Optional.empty());

        assertThatNoException().isThrownBy(() -> condition.validateCondition(conditionContext));
    }

    @Test
    @DisplayName("validateCondition does nothing when the player issuer is not a freeplay game context")
    void validateCondition_playerInFreeplayGameContext() {
        GameContext gameContext = new GameContext(GameKey.ofFreeplay(), GameContextType.FREEPLAY_MODE);

        when(issuer.getPlayer()).thenReturn(player);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.getGameContext(PLAYER_ID)).thenReturn(Optional.of(gameContext));

        assertThatNoException().isThrownBy(() -> condition.validateCondition(conditionContext));
    }

    @Test
    @DisplayName("validateCondition throws ConditionFailedException when player issuer is registered in arena game context")
    void validateCondition_issuerPresentInArena() {
        GameContext gameContext = new GameContext(GameKey.ofArena(1), GameContextType.ARENA_MODE);

        when(issuer.getPlayer()).thenReturn(player);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
        when(gameContextProvider.getGameContext(PLAYER_ID)).thenReturn(Optional.of(gameContext));
        when(translator.translate(TranslationKey.ALREADY_IN_ARENA_MODE.getPath())).thenReturn(new TextTemplate(ERROR_MESSAGE));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage(ERROR_MESSAGE);
    }
}
