package nl.matsgemmeke.battlegrounds.configuration.lang;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LanguageConfigurationProviderTest {

    private BattlegroundsConfiguration configuration;
    private File langFolder;
    private Plugin plugin;

    @BeforeEach
    public void setUp() {
        configuration = mock(BattlegroundsConfiguration.class);
        langFolder = new File("src/main/resources/lang");
        plugin = mock(Plugin.class);
    }

    @Test
    public void getCreatesNewConfigurationWithConfigFileContent() throws FileNotFoundException {
        File langFile = new File("src/main/resources/lang/lang_en.yml");
        FileInputStream inputStream = new FileInputStream(langFile);

        when(configuration.getLanguage()).thenReturn("en");
        when(plugin.getResource("lang_en.yml")).thenReturn(inputStream);

        LanguageConfigurationProvider provider = new LanguageConfigurationProvider(configuration, langFolder, plugin);
        LanguageConfiguration configuration = provider.get();

        assertEquals("&6&l Battlegrounds Help Menu", configuration.getString("commands.help-menu-title"));
    }
}
