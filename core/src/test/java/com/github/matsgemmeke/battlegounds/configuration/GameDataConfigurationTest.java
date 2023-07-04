package com.github.matsgemmeke.battlegounds.configuration;

import com.github.matsgemmeke.battlegrounds.api.game.GameConfiguration;
import com.github.matsgemmeke.battlegrounds.configuration.GameDataConfiguration;
import com.github.matsgemmeke.battlegrounds.game.DefaultGameConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class GameDataConfigurationTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File dataFolder;

    @Before
    public void setUp() throws IOException {
        this.dataFolder = folder.newFile("game_1.yml");
    }

    @Test
    public void shouldBeAbleToLoadGameConfiguration() {
        GameConfiguration gameConfiguration = DefaultGameConfiguration.getNewConfiguration();

        GameDataConfiguration dataConfiguration = new GameDataConfiguration(dataFolder);
        dataConfiguration.load();
        dataConfiguration.saveConfiguration(gameConfiguration);

        GameConfiguration result = dataConfiguration.loadConfiguration();

        assertEquals(result.getMaxPlayers(), gameConfiguration.getMaxPlayers());
        assertEquals(result.getMinPlayers(), gameConfiguration.getMinPlayers());
    }

    @Test
    public void shouldBeAbleToSaveGameConfiguration() {
        GameConfiguration gameConfiguration = DefaultGameConfiguration.getNewConfiguration();

        GameDataConfiguration dataConfiguration = new GameDataConfiguration(dataFolder);
        dataConfiguration.load();
        dataConfiguration.saveConfiguration(gameConfiguration);

        assertEquals(gameConfiguration.getMaxPlayers(), dataConfiguration.getInteger("config.max-players"));
        assertEquals(gameConfiguration.getMinPlayers(), dataConfiguration.getInteger("config.min-players"));
    }
}
