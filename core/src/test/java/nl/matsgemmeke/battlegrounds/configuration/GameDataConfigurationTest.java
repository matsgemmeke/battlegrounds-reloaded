package nl.matsgemmeke.battlegrounds.configuration;

import nl.matsgemmeke.battlegrounds.game.session.DefaultSessionConfiguration;
import nl.matsgemmeke.battlegrounds.game.session.SessionConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class GameDataConfigurationTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File dataFolder;

    @Before
    public void setUp() throws IOException {
        this.dataFolder = folder.newFile("game_1.yml");
    }

    @Test
    public void shouldBeAbleToLoadSessionConfiguration() {
        SessionConfiguration sessionConfiguration = DefaultSessionConfiguration.getNewConfiguration();

        SessionDataConfiguration dataConfiguration = new SessionDataConfiguration(dataFolder);
        dataConfiguration.load();
        dataConfiguration.saveConfiguration(sessionConfiguration);

        SessionConfiguration result = dataConfiguration.loadConfiguration();

        assertEquals(result.getMaxPlayers(), sessionConfiguration.getMaxPlayers());
        assertEquals(result.getMinPlayers(), sessionConfiguration.getMinPlayers());
    }

    @Test
    public void shouldBeAbleToSaveSessionConfiguration() {
        SessionConfiguration sessionConfiguration = DefaultSessionConfiguration.getNewConfiguration();

        SessionDataConfiguration dataConfiguration = new SessionDataConfiguration(dataFolder);
        dataConfiguration.load();
        dataConfiguration.saveConfiguration(sessionConfiguration);

        assertEquals(sessionConfiguration.getMaxPlayers(), dataConfiguration.getInteger("config.max-players"));
        assertEquals(sessionConfiguration.getMinPlayers(), dataConfiguration.getInteger("config.min-players"));
    }
}
