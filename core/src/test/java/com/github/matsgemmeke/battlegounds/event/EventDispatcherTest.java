package com.github.matsgemmeke.battlegounds.event;

import com.github.matsgemmeke.battlegrounds.event.EventBus;
import com.github.matsgemmeke.battlegrounds.event.EventDispatcher;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.server.ServerLoadEvent.LoadType;
import org.bukkit.plugin.PluginManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EventDispatcherTest {

    private PluginManager pluginManager;

    @Before
    public void setUp() {
        this.pluginManager = mock(PluginManager.class);
    }

    @Test
    public void canGetWhetherEventClassIsRegistered() {
        EventBus<PlayerInteractEvent> eventBus = new EventBus<>();

        EventDispatcher eventDispatcher = new EventDispatcher(pluginManager);
        eventDispatcher.registerEventBus(PlayerInteractEvent.class, eventBus);

        assertTrue(eventDispatcher.containsEventBusForClass(PlayerInteractEvent.class));
        assertFalse(eventDispatcher.containsEventBusForClass(PlayerMoveEvent.class));
    }

    @Test
    public void canRegisterEventBusForUnregisteredEventClass() {
        EventBus<PlayerInteractEvent> eventBus = new EventBus<>();

        EventDispatcher eventDispatcher = new EventDispatcher(pluginManager);
        eventDispatcher.registerEventBus(PlayerInteractEvent.class, eventBus);

        assertTrue(eventDispatcher.containsEventBusForClass(PlayerInteractEvent.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void canRegisterEventBusForRegisteredEventClass() {
        EventBus<PlayerInteractEvent> eventBus = (EventBus<PlayerInteractEvent>) mock(EventBus.class);
        EventBus<PlayerInteractEvent> other = new EventBus<>();

        EventDispatcher eventDispatcher = new EventDispatcher(pluginManager);
        eventDispatcher.registerEventBus(PlayerInteractEvent.class, eventBus);
        eventDispatcher.registerEventBus(PlayerInteractEvent.class, other);

        verify(eventBus).addEventBus(other);

        assertTrue(eventDispatcher.containsEventBusForClass(PlayerInteractEvent.class));
    }

    @Test
    public void canRemoveRegisteredEventBus() {
        EventBus<PlayerInteractEvent> eventBus = new EventBus<>();

        EventDispatcher eventDispatcher = new EventDispatcher(pluginManager);
        eventDispatcher.registerEventBus(PlayerInteractEvent.class, eventBus);
        eventDispatcher.unregisterEventBus(PlayerInteractEvent.class);

        assertFalse(eventDispatcher.containsEventBusForClass(PlayerInteractEvent.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void canDispatchInternalEvent() {
        ServerLoadEvent event = new ServerLoadEvent(LoadType.RELOAD);
        EventBus<ServerLoadEvent> eventBus = (EventBus<ServerLoadEvent>) mock(EventBus.class);

        EventDispatcher eventDispatcher = new EventDispatcher(pluginManager);
        eventDispatcher.registerEventBus(ServerLoadEvent.class, eventBus);
        eventDispatcher.dispatchInternalEvent(event);

        verify(eventBus).passEvent(event);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void canDispatchExternalEvent() {
        ServerLoadEvent event = new ServerLoadEvent(LoadType.RELOAD);
        EventBus<ServerLoadEvent> eventBus = (EventBus<ServerLoadEvent>) mock(EventBus.class);

        EventDispatcher eventDispatcher = new EventDispatcher(pluginManager);
        eventDispatcher.registerEventBus(ServerLoadEvent.class, eventBus);
        eventDispatcher.dispatchExternalEvent(event);

        verify(eventBus).passEvent(event);
        verify(pluginManager).callEvent(event);
    }
}
