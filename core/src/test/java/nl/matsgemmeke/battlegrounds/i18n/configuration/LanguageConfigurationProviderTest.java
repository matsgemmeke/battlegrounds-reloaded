package nl.matsgemmeke.battlegrounds.i18n.configuration;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFileFactory;
import nl.matsgemmeke.battlegrounds.util.ResourceProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LanguageConfigurationProviderTest {

    @Mock
    private BattlegroundsConfiguration configuration;
    @Spy
    @TempDir
    private File langFolder;
    @Mock
    private ResourceProvider resourceProvider;
    @Mock
    private YamlConfigurationFileFactory yamlConfigurationFileFactory;
    @InjectMocks
    private LanguageConfigurationProvider provider;

    @Test
    @DisplayName("get creates new configuration instance with corresponding configuration file")
    void get() throws FileNotFoundException {
        File langFile = new File("src/main/resources/lang/lang_en.yml");
        FileInputStream resource = new FileInputStream(langFile);
        YamlConfigurationFile configurationFile = mock(YamlConfigurationFile.class);

        when(configuration.getLanguage()).thenReturn("en");
        when(resourceProvider.getResource("lang/lang_en.yml")).thenReturn(resource);
        when(yamlConfigurationFileFactory.create(new File(langFolder, "lang_en.yml"), resource)).thenReturn(configurationFile);

        LanguageConfiguration configuration = provider.get();

        assertThat(configuration).isNotNull();
    }
}
