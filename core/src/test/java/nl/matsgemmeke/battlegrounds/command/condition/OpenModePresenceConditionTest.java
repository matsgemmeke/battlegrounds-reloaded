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
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class OpenModePresenceConditionTest {

    private static final GameKey OPEN_MODE_GAME_KEY = GameKey.ofOpenMode();

    private ConditionContext<BukkitCommandIssuer> conditionContext;
    private GameContextProvider gameContextProvider;
    private GameScope gameScope;
    private Player player;
    private Provider<PlayerRegistry> playerRegistryProvider;
    private Translator translator;
    private UUID playerId;

    @BeforeEach
    public void setUp() {
        gameContextProvider = mock(GameContextProvider.class);
        gameScope = mock(GameScope.class);
        playerId = UUID.randomUUID();
        playerRegistryProvider = mock();
        translator = mock(Translator.class);

        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerId);

        BukkitCommandIssuer issuer = mock(BukkitCommandIssuer.class);
        when(issuer.getPlayer()).thenReturn(player);

        conditionContext = mock();
        when(conditionContext.getIssuer()).thenReturn(issuer);
    }

    @Test
    public void validateConditionThrowsConditionFailedExceptionWhenOpenModeContextDoesNotExist() {
        when(gameContextProvider.getGameContext(OPEN_MODE_GAME_KEY)).thenReturn(Optional.empty());
        when(translator.translate(TranslationKey.OPEN_MODE_NOT_EXISTS.getPath())).thenReturn(new TextTemplate("error"));

        OpenModePresenceCondition condition = new OpenModePresenceCondition(gameContextProvider, gameScope, playerRegistryProvider, translator);

        assertThatThrownBy(() -> condition.validateCondition(conditionContext))
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage("error");
    }

    @Test
    public void validateConditionDoesNothingWhenPlayerIsRegisteredInOpenMode() {
        GameContext gameContext = new GameContext(OPEN_MODE_GAME_KEY, GameContextType.OPEN_MODE);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.isRegistered(playerId)).thenReturn(true);

        when(gameContextProvider.getGameContext(OPEN_MODE_GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);

        OpenModePresenceCondition condition = new OpenModePresenceCondition(gameContextProvider, gameScope, playerRegistryProvider, translator);
        condition.validateCondition(conditionContext);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(gameScope).runInScope(eq(gameContext), runnableCaptor.capture());

        assertThatCode(() -> runnableCaptor.getValue().run()).doesNotThrowAnyException();
    }

    @Test
    public void validateConditionThrowsConditionFailedExceptionWhenPlayerIsNotInOpenMode() {
        GameContext gameContext = new GameContext(OPEN_MODE_GAME_KEY, GameContextType.OPEN_MODE);

        PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
        when(playerRegistry.isRegistered(playerId)).thenReturn(false);

        when(gameContextProvider.getGameContext(OPEN_MODE_GAME_KEY)).thenReturn(Optional.of(gameContext));
        when(playerRegistryProvider.get()).thenReturn(playerRegistry);
        when(translator.translate(TranslationKey.NOT_IN_OPEN_MODE.getPath())).thenReturn(new TextTemplate("error"));

        OpenModePresenceCondition condition = new OpenModePresenceCondition(gameContextProvider, gameScope, playerRegistryProvider, translator);
        condition.validateCondition(conditionContext);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(gameScope).runInScope(eq(gameContext), runnableCaptor.capture());

        assertThatThrownBy(() -> runnableCaptor.getValue().run())
                .isInstanceOf(ConditionFailedException.class)
                .hasMessage("error");
    }
}
