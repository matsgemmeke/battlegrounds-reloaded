package nl.matsgemmeke.battlegrounds.event.listener;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

public class EventListener implements Listener {

    @NotNull
    private final EventDispatcher eventDispatcher;

    @Inject
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
    public void onEntityCombust(@NotNull EntityCombustEvent event) {
        this.dispatchEvent(event);
    }

    @EventHandler
    public void onEntityDamage(@NotNull EntityDamageEvent event) {
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
    public void onPlayerKick(@NotNull PlayerKickEvent event) {
        this.dispatchEvent(event);
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
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
