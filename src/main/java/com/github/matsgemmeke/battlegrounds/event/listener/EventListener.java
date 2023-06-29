package com.github.matsgemmeke.battlegrounds.event.listener;

import com.github.matsgemmeke.battlegrounds.event.EventDispatcher;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class EventListener implements Listener {

    @NotNull
    private EventDispatcher eventDispatcher;

    public EventListener(@NotNull EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
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

    private void dispatchEvent(@NotNull Event event) {
        eventDispatcher.dispatchInternalEvent(event);
    }
}
