package com.github.matsgemmeke.battlegounds.event.handler;

import com.github.matsgemmeke.battlegrounds.api.game.FreemodeContext;
import com.github.matsgemmeke.battlegrounds.event.handler.PlayerJoinEventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class PlayerJoinEventHandlerTest {

    private FreemodeContext freemodeContext;

    @Before
    public void setUp() {
        this.freemodeContext = mock(FreemodeContext.class);
    }

    @Test
    public void addsPlayerToFreemodeUponJoining() {
        Player player = mock(Player.class);
        PlayerJoinEvent event = new PlayerJoinEvent(player, "test");

        PlayerJoinEventHandler eventHandler = new PlayerJoinEventHandler(freemodeContext);
        eventHandler.handle(event);

        verify(freemodeContext).addPlayer(player);
    }
}
