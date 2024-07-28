package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.EntityRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PlayerJoinEventHandlerTest {

    private EntityRegistry<GamePlayer, Player> playerRegistry;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        playerRegistry = (EntityRegistry<GamePlayer, Player>) mock(EntityRegistry.class);
    }

    @Test
    public void addsPlayerToTrainingModeUponJoining() {
        Player player = mock(Player.class);
        PlayerJoinEvent event = new PlayerJoinEvent(player, "test");

        PlayerJoinEventHandler eventHandler = new PlayerJoinEventHandler(playerRegistry);
        eventHandler.handle(event);

        verify(playerRegistry).registerEntity(player);
    }
}
