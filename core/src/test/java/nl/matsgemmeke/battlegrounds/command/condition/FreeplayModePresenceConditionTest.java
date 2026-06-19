package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Test
    @DisplayName("validateCondition throws ConditionFailedException when the condition context has no player")
    void validateCondition_conditionContextWithoutPlayer() {
        when(issuer.getPlayer()).thenReturn(null);
        when(translator.translate(TranslationKey.NOT_IN_FREEPLAY_MODE.getPath())).thenReturn(new TextTemplate("error"));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage("error");
    }

    @Test
    @DisplayName("validateCondition throws ConditionFailedException when freeplay mode context does not exists")
    void validateCondition_freeplayModeNotExists() {
        Player player = mock(Player.class);

        when(issuer.getPlayer()).thenReturn(player);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.empty());
        when(translator.translate(TranslationKey.FREEPLAY_MODE_NOT_EXISTS.getPath())).thenReturn(new TextTemplate("error"));

        assertThatThrownBy(() -> condition.validateCondition(conditionContext))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage("error");
    }

    @Test
    @DisplayName("validateCondition does nothing when player is registered in freeplay mode")
    void validateCondition_passes() {
        GameContext gameContext = new GameContext(GAME_KEY, GameContextType.OPEN_MODE);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.isRegistered(PLAYER_ID)).thenReturn(true);

        when(issuer.getPlayer()).thenReturn(player);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);

        condition.validateCondition(conditionContext);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(gameScope).runInScope(eq(gameContext), runnableCaptor.capture());

        assertThatCode(() -> runnableCaptor.getValue().run()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateCondition throws ConditionFailedException when player is not in freeplay mode")
    void validationCondition_playerNotInFreeplayMode() {
        GameContext gameContext = new GameContext(GAME_KEY, GameContextType.OPEN_MODE);

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(PLAYER_ID);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.isRegistered(PLAYER_ID)).thenReturn(false);

        when(issuer.getPlayer()).thenReturn(player);
        when(gameContextProvider.getGameContext(GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(translator.translate(TranslationKey.NOT_IN_FREEPLAY_MODE.getPath())).thenReturn(new TextTemplate("error"));

        condition.validateCondition(conditionContext);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(gameScope).runInScope(eq(gameContext), runnableCaptor.capture());

        assertThatThrownBy(() -> runnableCaptor.getValue().run())
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage("error");
    }
}
