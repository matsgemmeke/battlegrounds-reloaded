package nl.matsgemmeke.battlegrounds.event;

import org.bukkit.event.Event;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.server.ServerLoadEvent.LoadType;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.mockito.Mockito.*;

public class EventDispatcherTest {

    private Logger logger;
    private PluginManager pluginManager;

    @BeforeEach
    public void setUp() {
        logger = mock(Logger.class);
        pluginManager = mock(PluginManager.class);
    }

    @Test
    public void dispatchInternalEventInvokesCorrespondingEventHandlers() {
        ServerLoadEvent event = new ServerLoadEvent(LoadType.RELOAD);
        ServerLoadEventHandlerMock eventHandler = spy(new ServerLoadEventHandlerMock(() -> {}));

        EventDispatcher eventDispatcher = new EventDispatcher(pluginManager, logger);
        eventDispatcher.registerEventHandler(ServerLoadEvent.class, eventHandler);
        eventDispatcher.dispatchInternalEvent(event);

        verify(eventHandler).handle(event);
        verify(pluginManager, never()).callEvent(any(Event.class));
    }

    @Test
    public void dispatchInternalEventLogsErrorMessageIfEventHandlerMethodFailedToExecute() {
        ServerLoadEvent event = new ServerLoadEvent(LoadType.RELOAD);
        ServerLoadEventHandlerMock eventHandler = spy(new ServerLoadEventHandlerMock(() -> {
            throw new Error();
        }));

        EventDispatcher eventDispatcher = new EventDispatcher(pluginManager, logger);
        eventDispatcher.registerEventHandler(ServerLoadEvent.class, eventHandler);
        eventDispatcher.dispatchInternalEvent(event);

        verify(eventHandler).handle(event);
        verify(logger).severe("Error occurred while invoking handle method for event ServerLoadEvent");
    }

    @Test
    public void dispatchExternalEventInvokesCorrespondingEventHandlersAsWellAsPluginManager() {
        ServerLoadEvent event = new ServerLoadEvent(LoadType.RELOAD);
        ServerLoadEventHandlerMock eventHandler = spy(new ServerLoadEventHandlerMock(() -> {}));

        EventDispatcher eventDispatcher = new EventDispatcher(pluginManager, logger);
        eventDispatcher.registerEventHandler(ServerLoadEvent.class, eventHandler);
        eventDispatcher.dispatchExternalEvent(event);

        verify(eventHandler).handle(event);
        verify(pluginManager).callEvent(event);
    }

    @Test
    public void registerEventHandlerLogsErrorMessageIfEventHandlerDoesNotContainCorrectHandleMethod() {
        // Use a mock so the method signature uses org.bukkit.event.Event instead
        EventHandler<ServerLoadEvent> eventHandler = mock();

        EventDispatcher eventDispatcher = new EventDispatcher(pluginManager, logger);
        eventDispatcher.registerEventHandler(ServerLoadEvent.class, eventHandler);

        verify(logger).severe("Cannot register event handler for event ServerLoadEvent without handle method");
    }
}
