package nl.matsgemmeke.battlegrounds.i18n.configuration;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFileFactory;
import nl.matsgemmeke.battlegrounds.resource.ResourceProvider;

import java.io.File;
import java.io.InputStream;

public class LanguageConfigurationProvider implements Provider<LanguageConfiguration> {

    private static final String DEFAULT_FILE_NAME = "lang_%s.yml";
    private static final String FALLBACK_FILE_NAME = "lang_en.yml";

    private final BattlegroundsConfiguration configuration;
    private final File langFolder;
    private final ResourceProvider resourceProvider;
    private final YamlConfigurationFileFactory yamlConfigurationFileFactory;

    @Inject
    public LanguageConfigurationProvider(
            BattlegroundsConfiguration configuration,
            @Named("LangFolder") File langFolder,
            ResourceProvider resourceProvider,
            YamlConfigurationFileFactory yamlConfigurationFileFactory
    ) {
        this.configuration = configuration;
        this.langFolder = langFolder;
        this.resourceProvider = resourceProvider;
        this.yamlConfigurationFileFactory = yamlConfigurationFileFactory;
    }

    @Override
    public LanguageConfiguration get() {
        String language = configuration.getLanguage();
        String mainFileName = DEFAULT_FILE_NAME.formatted(language);

        File mainFile = new File(langFolder, mainFileName);
        File fallbackFile = new File(langFolder, FALLBACK_FILE_NAME);

        InputStream mainResource = resourceProvider.getResource("lang/" + mainFileName);
        InputStream fallbackResource = resourceProvider.getResource("lang/" + FALLBACK_FILE_NAME);

        YamlConfigurationFile mainConfigurationFile = yamlConfigurationFileFactory.create(mainFile, mainResource);
        YamlConfigurationFile fallbackConfigurationFile = yamlConfigurationFileFactory.create(fallbackFile, fallbackResource);

        return new LanguageConfiguration(mainConfigurationFile, fallbackConfigurationFile);
    }
}
