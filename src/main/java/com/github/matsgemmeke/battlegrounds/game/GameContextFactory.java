package com.github.matsgemmeke.battlegrounds.game;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.game.GameConfiguration;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.configuration.GameDataConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Factory class for creating {@link GameContext} instances.
 */
public class GameContextFactory {

    @NotNull
    private File dataDirectory;
    @NotNull
    private TaskRunner taskRunner;

    public GameContextFactory(@NotNull TaskRunner taskRunner, @NotNull File dataDirectory) {
        this.taskRunner = taskRunner;
        this.dataDirectory = dataDirectory;
    }

    /**
     * Makes a new {@link GameContext} instance.
     *
     * @param id the game id
     * @param configuration the game configuration
     * @return a new {@link GameContext} instance
     */
    public GameContext make(int id, GameConfiguration configuration) {
        File dataConfigFile = new File(dataDirectory.getPath() + "/game_" + id + "/config.yml");

        GameDataConfiguration dataConfig = new GameDataConfiguration(dataConfigFile);
        dataConfig.load();
        dataConfig.saveConfiguration(configuration);

        GameContext context = new DefaultGameContext(taskRunner, id, configuration);

        return context;
    }
}
