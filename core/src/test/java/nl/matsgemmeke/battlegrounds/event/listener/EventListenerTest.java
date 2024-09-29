package nl.matsgemmeke.battlegrounds.event.listener;

import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EventListenerTest {

    private EventDispatcher eventDispatcher;

    @Before
    public void setUp() {
        this.eventDispatcher = mock(EventDispatcher.class);
    }

    @Test
    public void callsEventDispatcherUponHandlingBlockBurnEvent() {
        BlockBurnEvent event = mock(BlockBurnEvent.class);

        EventListener eventListener = new EventListener(eventDispatcher);
        eventListener.onBlockBurn(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    public void callsEventDispatcherUponHandlingBlockSpreadEvent() {
        BlockSpreadEvent event = mock(BlockSpreadEvent.class);

        EventListener eventListener = new EventListener(eventDispatcher);
        eventListener.onBlockSpread(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    public void callsEventDispatcherUponHandlingEntityDamageByEntityEvent() {
        EntityDamageByEntityEvent event = mock(EntityDamageByEntityEvent.class);

        EventListener eventListener = new EventListener(eventDispatcher);
        eventListener.onEntityDamageByEntity(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
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

    @Test
    public void callsEventDispatcherUponHandlingPlayerSwapHandItemsEvent() {
        PlayerSwapHandItemsEvent event = mock(PlayerSwapHandItemsEvent.class);

        EventListener eventListener = new EventListener(eventDispatcher);
        eventListener.onPlayerSwapHandItems(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }
}
