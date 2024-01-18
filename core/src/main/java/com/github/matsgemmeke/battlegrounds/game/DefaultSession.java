package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleEntity;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Session;
import com.github.matsgemmeke.battlegrounds.api.game.SessionConfiguration;
import com.github.matsgemmeke.battlegrounds.entity.DefaultBattlePlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class DefaultSession extends AbstractBattleContext implements Session {

    private final int id;
    @NotNull
    private SessionConfiguration configuration;

    public DefaultSession(
            @NotNull BlockCollisionChecker collisionChecker,
            int id,
            @NotNull SessionConfiguration configuration
    ) {
        super(collisionChecker);
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
    public BattlePlayer addPlayer(@NotNull Player player) {
        return new DefaultBattlePlayer(player);
    }

    @NotNull
    public Iterable<BattlePlayer> getPlayers() {
        return Collections.emptyList();
    }

    @NotNull
    public Collection<BattleEntity> getTargets(@NotNull BattleEntity battleEntity, @NotNull Location location, double range) {
        return Collections.emptyList();
    }

    public void handleRecoil(@NotNull BattleEntity battleEntity, float recoilYaw, float recoilPitch) {

    }

    public boolean onInteract(@NotNull BattlePlayer battlePlayer, @NotNull PlayerInteractEvent event) {
        return false;
    }

    public boolean onItemDrop(@NotNull BattlePlayer battlePlayer, @NotNull PlayerDropItemEvent event) {
        return false;
    }

    public boolean onItemHeld(@NotNull BattlePlayer battlePlayer, @NotNull PlayerItemHeldEvent event) {
        return false;
    }

    public boolean onPickupItem(@NotNull BattlePlayer battlePlayer, @NotNull EntityPickupItemEvent event) {
        return false;
    }
}
