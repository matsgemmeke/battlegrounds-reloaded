package nl.matsgemmeke.battlegrounds.event.listener;

import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

public class EventListener implements Listener {

    @NotNull
    private EventDispatcher eventDispatcher;

    public EventListener(@NotNull EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    @EventHandler
    public void onBlockBurn(@NotNull BlockBurnEvent event) {
        this.dispatchEvent(event);
    }

    @EventHandler
    public void onBlockSpread(@NotNull BlockSpreadEvent event) {
        this.dispatchEvent(event);
    }

    @EventHandler
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        this.dispatchEvent(event);
    }

    @EventHandler
    public void onEntityPickupItem(@NotNull EntityPickupItemEvent event) {
        this.dispatchEvent(event);
    }

    @EventHandler
    public void onPlayerDropItem(@NotNull PlayerDropItemEvent event) {
        this.dispatchEvent(event);
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        this.dispatchEvent(event);
    }

    @EventHandler
    public void onPlayerItemHeld(@NotNull PlayerItemHeldEvent event) {
        this.dispatchEvent(event);
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        this.dispatchEvent(event);
    }

    @EventHandler
    public void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        this.dispatchEvent(event);
    }

    @EventHandler
    public void onPlayerSwapHandItems(@NotNull PlayerSwapHandItemsEvent event) {
        this.dispatchEvent(event);
    }

    private void dispatchEvent(@NotNull Event event) {
        eventDispatcher.dispatchInternalEvent(event);
    }
}
