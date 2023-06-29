package com.github.matsgemmeke.battlegounds.game;

import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.game.DefaultGameConfiguration;
import com.github.matsgemmeke.battlegrounds.game.GameContextFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class GameContextFactoryTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File dataFolder;

    @Before
    public void setUp() throws IOException {
        this.dataFolder = folder.newFolder("data");
    }

    @Test
    public void shouldBeAbleToMakeGameContext() {
        int gameId = 1;

        DefaultGameConfiguration configuration = DefaultGameConfiguration.getNewConfiguration();
        GameContextFactory contextFactory = new GameContextFactory(dataFolder);

        GameContext gameContext = contextFactory.make(gameId, configuration);

        assertEquals(gameId, gameContext.getId());
    }
}
