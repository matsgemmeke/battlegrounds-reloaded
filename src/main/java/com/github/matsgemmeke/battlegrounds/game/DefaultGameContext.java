package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleEntity;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.GameConfiguration;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.api.game.Team;
import com.github.matsgemmeke.battlegrounds.entity.DefaultBattlePlayer;
import com.github.matsgemmeke.battlegrounds.item.BlockCollisionChecker;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class DefaultGameContext extends AbstractBattleContext implements GameContext {

    private final int id;
    @NotNull
    private GameConfiguration configuration;

    public DefaultGameContext(
            @NotNull BlockCollisionChecker collisionChecker,
            @NotNull TaskRunner taskRunner,
            int id,
            @NotNull GameConfiguration configuration
    ) {
        super(collisionChecker, taskRunner);
        this.id = id;
        this.configuration = configuration;
    }

    @NotNull
    public GameConfiguration getConfiguration() {
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
    public Iterable<BattleEntity> getTargets(@NotNull BattleEntity battleEntity, @NotNull Location location, double range) {
        return Collections.emptyList();
    }

    public boolean onInteract(@NotNull BattlePlayer battlePlayer, @NotNull PlayerInteractEvent event) {
        return false;
    }
}
