package com.github.matsgemmeke.battlegounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.game.TrainingMode;
import com.github.matsgemmeke.battlegrounds.event.handler.PlayerJoinEventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

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
