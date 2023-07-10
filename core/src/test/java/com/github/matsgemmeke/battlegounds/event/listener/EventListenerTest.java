package com.github.matsgemmeke.battlegounds.event.listener;

import com.github.matsgemmeke.battlegrounds.event.EventDispatcher;
import com.github.matsgemmeke.battlegrounds.event.listener.EventListener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class EventListenerTest {

    private EventDispatcher eventDispatcher;

    @Before
    public void setUp() {
        this.eventDispatcher = mock(EventDispatcher.class);
    }

    @Test
    public void callsEventDispatcherUponHandlingEntityPickupItemEvent() {
        EntityPickupItemEvent event = mock(EntityPickupItemEvent.class);

        EventListener eventListener = new EventListener(eventDispatcher);
        eventListener.onEntityPickupItem(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    public void callsEventDispatcherUponHandlingPlayerDropItemEvent() {
        PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);

        EventListener eventListener = new EventListener(eventDispatcher);
        eventListener.onPlayerDropItem(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    public void callsEventDispatcherUponHandlingPlayerInteractEvent() {
        PlayerInteractEvent event = mock(PlayerInteractEvent.class);

        EventListener eventListener = new EventListener(eventDispatcher);
        eventListener.onPlayerInteract(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    public void callsEventDispatcherUponHandlingPlayerItemHeldEvent() {
        PlayerItemHeldEvent event = mock(PlayerItemHeldEvent.class);

        EventListener eventListener = new EventListener(eventDispatcher);
        eventListener.onPlayerItemHeld(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    public void callsEventDispatcherUponHandlingPlayerJoinEvent() {
        PlayerJoinEvent event = mock(PlayerJoinEvent.class);

        EventListener eventListener = new EventListener(eventDispatcher);
        eventListener.onPlayerJoin(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }
}
