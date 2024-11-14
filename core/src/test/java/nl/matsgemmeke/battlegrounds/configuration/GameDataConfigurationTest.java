package nl.matsgemmeke.battlegrounds.configuration;

import nl.matsgemmeke.battlegrounds.game.session.DefaultSessionConfiguration;
import nl.matsgemmeke.battlegrounds.game.session.SessionConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameDataConfigurationTest {

    private File dataFile;

    @BeforeEach
    public void setUp(@TempDir File tempDir) throws IOException {
        this.dataFile = new File(tempDir, "game_1.yml");
    }

    @Test
    public void shouldBeAbleToLoadSessionConfiguration() {
        SessionConfiguration sessionConfiguration = DefaultSessionConfiguration.getNewConfiguration();

        SessionDataConfiguration dataConfiguration = new SessionDataConfiguration(dataFile);
        dataConfiguration.load();
        dataConfiguration.saveConfiguration(sessionConfiguration);

        SessionConfiguration result = dataConfiguration.loadConfiguration();

        assertEquals(result.getMaxPlayers(), sessionConfiguration.getMaxPlayers());
        assertEquals(result.getMinPlayers(), sessionConfiguration.getMinPlayers());
    }

    @Test
    public void shouldBeAbleToSaveSessionConfiguration() {
        SessionConfiguration sessionConfiguration = DefaultSessionConfiguration.getNewConfiguration();

        SessionDataConfiguration dataConfiguration = new SessionDataConfiguration(dataFile);
        dataConfiguration.load();
        dataConfiguration.saveConfiguration(sessionConfiguration);

        assertEquals(sessionConfiguration.getMaxPlayers(), dataConfiguration.getInteger("config.max-players"));
        assertEquals(sessionConfiguration.getMinPlayers(), dataConfiguration.getInteger("config.min-players"));
    }
}
