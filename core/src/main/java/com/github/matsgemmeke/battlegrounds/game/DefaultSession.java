package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Session;
import com.github.matsgemmeke.battlegrounds.api.game.SessionConfiguration;
import com.github.matsgemmeke.battlegrounds.entity.DefaultGamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class DefaultSession extends AbstractGame implements Session {

    private final int id;
    @NotNull
    private SessionConfiguration configuration;

    public DefaultSession(
            int id,
            @NotNull SessionConfiguration configuration
    ) {
        this.id = id;
        this.configuration = configuration;
    }

    @NotNull
    public SessionConfiguration getConfiguration() {
        return configuration;
    }

    public int getId() {
        return id;
    }

    @NotNull
    public GamePlayer addPlayer(@NotNull Player player) {
        return new DefaultGamePlayer(player, null);
    }

    @NotNull
    public Iterable<GamePlayer> getPlayers() {
        return Collections.emptyList();
    }

    public boolean onInteract(@NotNull GamePlayer gamePlayer, @NotNull PlayerInteractEvent event) {
        return false;
    }

    public boolean onItemDrop(@NotNull GamePlayer gamePlayer, @NotNull PlayerDropItemEvent event) {
        return false;
    }

    public boolean onItemHeld(@NotNull GamePlayer gamePlayer, @NotNull PlayerItemHeldEvent event) {
        return false;
    }

    public boolean onPickupItem(@NotNull GamePlayer gamePlayer, @NotNull EntityPickupItemEvent event) {
        return false;
    }
}
