package nl.matsgemmeke.battlegrounds.i18n.configuration;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFileFactory;
import nl.matsgemmeke.battlegrounds.resource.ResourceProvider;
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
        File mainLangFile = new File("src/main/resources/lang/lang_fr.yml");
        File fallbackLangFile = new File("src/main/resources/lang/lang_en.yml");
        FileInputStream mainResource = new FileInputStream(mainLangFile);
        FileInputStream fallbackResource = new FileInputStream(fallbackLangFile);
        YamlConfigurationFile mainConfigurationFile = mock(YamlConfigurationFile.class);
        YamlConfigurationFile fallbackConfigurationFile = mock(YamlConfigurationFile.class);

        when(configuration.getLanguage()).thenReturn("fr");
        when(resourceProvider.getResource("lang/lang_fr.yml")).thenReturn(mainResource);
        when(resourceProvider.getResource("lang/lang_en.yml")).thenReturn(fallbackResource);
        when(yamlConfigurationFileFactory.create(new File(langFolder, "lang_fr.yml"), mainResource)).thenReturn(mainConfigurationFile);
        when(yamlConfigurationFileFactory.create(new File(langFolder, "lang_en.yml"), fallbackResource)).thenReturn(fallbackConfigurationFile);

        LanguageConfiguration configuration = provider.get();

        assertThat(configuration).isNotNull();
    }
}
