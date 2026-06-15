package nl.matsgemmeke.battlegrounds.configuration;

import nl.matsgemmeke.battlegrounds.game.arena.ArenaConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class GameDataConfigurationTest {

    private File dataFile;

    @BeforeEach
    public void setUp(@TempDir File tempDir) throws IOException {
        this.dataFile = new File(tempDir, "game_1.yml");
    }

    @Test
    public void shouldBeAbleToLoadSessionConfiguration() {
        ArenaConfiguration arenaConfiguration = ArenaConfiguration.getNewConfiguration();

        SessionDataConfiguration dataConfiguration = new SessionDataConfiguration(dataFile);
        dataConfiguration.load();
        dataConfiguration.saveConfiguration(arenaConfiguration);

        ArenaConfiguration result = dataConfiguration.loadConfiguration();

        assertThat(result.getMaxPlayers()).isEqualTo(arenaConfiguration.getMaxPlayers());
        assertThat(result.getMinPlayers()).isEqualTo(arenaConfiguration.getMinPlayers());
    }

    @Test
    public void shouldBeAbleToSaveSessionConfiguration() {
        ArenaConfiguration arenaConfiguration = ArenaConfiguration.getNewConfiguration();

        SessionDataConfiguration dataConfiguration = new SessionDataConfiguration(dataFile);
        dataConfiguration.load();
        dataConfiguration.saveConfiguration(arenaConfiguration);

        assertThat(dataConfiguration.getInteger("config.max-players")).isEqualTo(arenaConfiguration.getMaxPlayers());
        assertThat(dataConfiguration.getInteger("config.min-players")).isEqualTo(arenaConfiguration.getMinPlayers());
    }
}
