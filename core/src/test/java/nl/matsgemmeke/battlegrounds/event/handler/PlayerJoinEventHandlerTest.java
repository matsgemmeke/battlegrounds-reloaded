package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.registry.PlayerRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class PlayerJoinEventHandlerTest {

    private BattlegroundsConfiguration config;
    private GameContextProvider contextProvider;
    private GameKey trainingModeGameKey;
    private PlayerRegistry playerRegistry;

    @BeforeEach
    public void setUp() {
        config = mock(BattlegroundsConfiguration.class);
        trainingModeGameKey = GameKey.ofTrainingMode();
        playerRegistry = mock(PlayerRegistry.class);

        contextProvider = mock(GameContextProvider.class);
        when(contextProvider.getComponent(trainingModeGameKey, PlayerRegistry.class)).thenReturn(playerRegistry);
    }

    @Test
    public void addsPlayerToTrainingModeUponJoiningAndSetsPassiveBasedOnConfiguration() {
        Player player = mock(Player.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(config.isEnabledRegisterPlayersAsPassive()).thenReturn(true);
        when(playerRegistry.registerEntity(player)).thenReturn(gamePlayer);

        PlayerJoinEvent event = new PlayerJoinEvent(player, "test");

        PlayerJoinEventHandler eventHandler = new PlayerJoinEventHandler(config, contextProvider, trainingModeGameKey);
        eventHandler.handle(event);

        verify(gamePlayer).setPassive(true);
        verify(playerRegistry).registerEntity(player);
    }
}
