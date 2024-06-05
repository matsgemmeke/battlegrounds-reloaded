package nl.matsgemmeke.battlegrounds.event;

import nl.matsgemmeke.battlegrounds.event.handler.PlayerInteractEventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class EventBusTest {

    @Test
    public void canAddEventHandler() {
        PlayerInteractEventHandler eventHandler = mock(PlayerInteractEventHandler.class);
        EventBus<PlayerInteractEvent> eventBus = new EventBus<>();

        assertTrue(eventBus.addEventHandler(eventHandler));
        assertFalse(eventBus.addEventHandler(eventHandler));
    }

    @Test
    public void canRemoveEventHandler() {
        PlayerInteractEventHandler eventHandler = mock(PlayerInteractEventHandler.class);
        EventBus<PlayerInteractEvent> eventBus = new EventBus<>();

        assertFalse(eventBus.removeEventHandler(eventHandler));
        assertTrue(eventBus.addEventHandler(eventHandler));
        assertTrue(eventBus.removeEventHandler(eventHandler));
    }

    @Test
    public void canAddEventBus() {
        PlayerInteractEventHandler eventHandler = mock(PlayerInteractEventHandler.class);
        EventBus<PlayerInteractEvent> eventBus = new EventBus<>();

        EventBus<PlayerInteractEvent> other = new EventBus<>();
        other.addEventHandler(eventHandler);

        assertTrue(eventBus.addEventBus(other));
    }

    @Test
    public void canPassEventToAllEventHandlers() {
        PlayerInteractEvent event = mock(PlayerInteractEvent.class);
        PlayerInteractEventHandler eventHandler = mock(PlayerInteractEventHandler.class);

        EventBus<PlayerInteractEvent> eventBus = new EventBus<>(eventHandler);
        eventBus.passEvent(event);
    }
}
