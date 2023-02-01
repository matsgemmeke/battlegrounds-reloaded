package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleEntity;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.GameConfiguration;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class DefaultGameContext extends AbstractBattleContext implements GameContext {

    private final int id;
    @NotNull
    private GameConfiguration configuration;

    public DefaultGameContext(
            @NotNull TaskRunner taskRunner,
            int id,
            @NotNull GameConfiguration configuration
    ) {
        super(taskRunner);
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
    public Iterable<BattleEntity> getTargets(@NotNull BattleEntity battleEntity, @NotNull Location location, double range) {
        return Collections.emptyList();
    }

    public boolean onInteract(@NotNull BattlePlayer battlePlayer, @NotNull PlayerInteractEvent event) {
        return false;
    }
}
