package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.game.training.TrainingMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PlayerJoinEventHandlerTest {

    private TrainingMode trainingMode;

    @Before
    public void setUp() {
        this.trainingMode = mock(TrainingMode.class);
    }

    @Test
    public void addsPlayerToTrainingModeUponJoining() {
        Player player = mock(Player.class);
        PlayerJoinEvent event = new PlayerJoinEvent(player, "test");

        PlayerJoinEventHandler eventHandler = new PlayerJoinEventHandler(trainingMode);
        eventHandler.handle(event);

        verify(trainingMode).addPlayer(player);
    }
}
