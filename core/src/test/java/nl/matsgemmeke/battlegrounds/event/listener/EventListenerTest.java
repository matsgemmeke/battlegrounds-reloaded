package nl.matsgemmeke.battlegrounds.event.listener;

import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventListenerTest {

    @Mock
    private EventDispatcher eventDispatcher;
    @InjectMocks
    private EventListener eventListener;

    @Test
    void callsEventDispatcherUponHandlingBlockBurnEvent() {
        BlockBurnEvent event = mock(BlockBurnEvent.class);

        eventListener.onBlockBurn(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    void callsEventDispatcherUponHandlingBlockSpreadEvent() {
        BlockSpreadEvent event = mock(BlockSpreadEvent.class);

        eventListener.onBlockSpread(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    void onEntityCombustDispatchesEventToEventDispatcher() {
        EntityCombustEvent event = mock(EntityCombustEvent.class);

        eventListener.onEntityCombust(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    void onEntityDamageDispatchesEventToEventDispatcher() {
        EntityDamageEvent event = mock(EntityDamageEvent.class);

        eventListener.onEntityDamage(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    void callsEventDispatcherUponHandlingEntityPickupItemEvent() {
        EntityPickupItemEvent event = mock(EntityPickupItemEvent.class);

        eventListener.onEntityPickupItem(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    void callsEventDispatcherUponHandlingPlayerDropItemEvent() {
        PlayerDropItemEvent event = mock(PlayerDropItemEvent.class);

        eventListener.onPlayerDropItem(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    void callsEventDispatcherUponHandlingPlayerInteractEvent() {
        PlayerInteractEvent event = mock(PlayerInteractEvent.class);

        eventListener.onPlayerInteract(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    void callsEventDispatcherUponHandlingPlayerItemHeldEvent() {
        PlayerItemHeldEvent event = mock(PlayerItemHeldEvent.class);

        eventListener.onPlayerItemHeld(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    void callsEventDispatcherUponHandlingPlayerJoinEvent() {
        PlayerJoinEvent event = mock(PlayerJoinEvent.class);

        eventListener.onPlayerJoin(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    void onPlayerKickDispatchesEventToEventDispatcher() {
        PlayerKickEvent event = mock(PlayerKickEvent.class);

        eventListener.onPlayerKick(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    void onPlayerQuitDispatchesEventToEventDispatcher() {
        PlayerQuitEvent event = mock(PlayerQuitEvent.class);

        eventListener.onPlayerQuit(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    void onPlayerRespawnDispatchesEventToEventDispatcher() {
        PlayerRespawnEvent event = mock(PlayerRespawnEvent.class);

        eventListener.onPlayerRespawn(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    void onProjectileHitDispatchesEventToEventDispatcher() {
        ProjectileHitEvent event = mock(ProjectileHitEvent.class);

        eventListener.onProjectileHit(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }

    @Test
    void callsEventDispatcherUponHandlingPlayerSwapHandItemsEvent() {
        PlayerSwapHandItemsEvent event = mock(PlayerSwapHandItemsEvent.class);

        eventListener.onPlayerSwapHandItems(event);

        verify(eventDispatcher).dispatchInternalEvent(event);
    }
}
