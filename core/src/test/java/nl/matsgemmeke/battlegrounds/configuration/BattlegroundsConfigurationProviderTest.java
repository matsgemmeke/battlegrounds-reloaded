package nl.matsgemmeke.battlegrounds.configuration;

import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BattlegroundsConfigurationProviderTest {

    private Plugin plugin;

    @BeforeEach
    public void setUp() {
        plugin = mock(Plugin.class);
    }

    @Test
    public void getCreatesNewConfigurationWithConfigFileContent() throws FileNotFoundException {
        File dataFolder = new File("src/main/resources");
        File configFile = new File("src/main/resources/config.yml");
        FileInputStream inputStream = new FileInputStream(configFile);

        when(plugin.getResource("config.yml")).thenReturn(inputStream);

        BattlegroundsConfigurationProvider provider = new BattlegroundsConfigurationProvider(dataFolder, plugin);
        BattlegroundsConfiguration configuration = provider.get();

        assertEquals("en", configuration.getLanguage());
    }
}
