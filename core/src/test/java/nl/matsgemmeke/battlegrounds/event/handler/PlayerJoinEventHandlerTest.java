package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.EntityRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class PlayerJoinEventHandlerTest {

    private BattlegroundsConfiguration config;
    private EntityRegistry<GamePlayer, Player> playerRegistry;
    private GameContext trainingModeContext;

    @BeforeEach
    public void setUp() {
        config = mock(BattlegroundsConfiguration.class);
        playerRegistry = mock();

        trainingModeContext = mock(GameContext.class);
        when(trainingModeContext.getPlayerRegistry()).thenReturn(playerRegistry);
    }

    @Test
    public void addsPlayerToTrainingModeUponJoiningAndSetsPassiveBasedOnConfiguration() {
        when(config.isEnabledRegisterPlayersAsPassive()).thenReturn(true);

        Player player = mock(Player.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        EntityRegistry<GamePlayer, Player> playerRegistry = mock();
        when(playerRegistry.registerEntity(player)).thenReturn(gamePlayer);

        PlayerJoinEvent event = new PlayerJoinEvent(player, "test");

        PlayerJoinEventHandler eventHandler = new PlayerJoinEventHandler(config, trainingModeContext);
        eventHandler.handle(event);

        verify(gamePlayer).setPassive(true);
        verify(playerRegistry).registerEntity(player);
    }
}
