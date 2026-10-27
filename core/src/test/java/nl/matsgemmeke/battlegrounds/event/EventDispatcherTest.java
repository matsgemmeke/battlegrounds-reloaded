package nl.matsgemmeke.battlegrounds.event;

import org.bukkit.event.Event;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventDispatcherTest {

    @Mock
    private Logger logger;
    @Mock
    private PluginManager pluginManager;
    @InjectMocks
    private EventDispatcher eventDispatcher;

    @Test
    @DisplayName("dispatchInternalEvent invokes corresponding event handlers")
    void dispatchInternalEvent_successful() {
        WorldLoadEvent event = mock(WorldLoadEvent.class);
        WorldLoadEventHandlerMock eventHandler = spy(new WorldLoadEventHandlerMock(() -> {}));

        eventDispatcher.registerEventHandler(WorldLoadEvent.class, eventHandler);
        eventDispatcher.dispatchInternalEvent(event);

        verify(eventHandler).handle(event);
        verify(pluginManager, never()).callEvent(any(Event.class));
    }

    @Test
    @DisplayName("dispatchInternalEvent throws EventHandlingException when event handler method fails to execute")
    void dispatchInternalEvent_eventHandlerException() {
        Error error = new Error();
        WorldLoadEvent event = mock(WorldLoadEvent.class);
        WorldLoadEventHandlerMock eventHandler = spy(new WorldLoadEventHandlerMock(() -> {
            throw error;
        }));

        eventDispatcher.registerEventHandler(WorldLoadEvent.class, eventHandler);

        assertThatThrownBy(() -> eventDispatcher.dispatchInternalEvent(event))
                .isInstanceOf(EventHandlingException.class)
                .hasMessage("Error occurred while invoking handle method for event WorldLoadEvent")
                .cause()
                .isInstanceOf(InvocationTargetException.class)
                .cause()
                .isEqualTo(error);

        verify(eventHandler).handle(event);
    }

    @Test
    @DisplayName("dispatchExternalEvent invokes corresponding event handler and plugin manager")
    void dispatchExternalEvent_successful() {
        WorldLoadEvent event = mock(WorldLoadEvent.class);
        WorldLoadEventHandlerMock eventHandler = spy(new WorldLoadEventHandlerMock(() -> {}));

        eventDispatcher.registerEventHandler(WorldLoadEvent.class, eventHandler);
        eventDispatcher.dispatchExternalEvent(event);

        verify(eventHandler).handle(event);
        verify(pluginManager).callEvent(event);
    }

    @Test
    @DisplayName("registerEventHandler logs error message when given event handler somehow has no handle method")
    void registerEventHandler_invalidEventHandler() {
        // Use a mock so the method signature uses org.bukkit.event.Event instead
        EventHandler<WorldLoadEvent> eventHandler = mock();

        eventDispatcher.registerEventHandler(WorldLoadEvent.class, eventHandler);

        verify(logger).severe("Cannot register event handler for event WorldLoadEvent without handle method");
    }
}
