package nl.matsgemmeke.battlegrounds.i18n.configuration;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.yaml.YamlConfigurationFileFactory;
import nl.matsgemmeke.battlegrounds.util.ResourceProvider;
import nl.matsgemmeke.battlegrounds.validation.ObjectValidator;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public class LanguageConfigurationProvider implements Provider<LanguageConfiguration> {

    private static final String DEFAULT_FILE_NAME = "lang_%s.yml";

    private final BattlegroundsConfiguration configuration;
    private final File langFolder;
    private final Logger logger;
    private final MessageSpecFactory messageSpecFactory;
    private final ObjectValidator objectValidator;
    private final ResourceProvider resourceProvider;
    private final YamlConfigurationFileFactory yamlConfigurationFileFactory;

    @Inject
    public LanguageConfigurationProvider(
            BattlegroundsConfiguration configuration,
            @Named("LangFolder") File langFolder,
            @Named("Battlegrounds") Logger logger,
            MessageSpecFactory messageSpecFactory,
            ObjectValidator objectValidator,
            ResourceProvider resourceProvider,
            YamlConfigurationFileFactory yamlConfigurationFileFactory
    ) {
        this.configuration = configuration;
        this.langFolder = langFolder;
        this.logger = logger;
        this.messageSpecFactory = messageSpecFactory;
        this.objectValidator = objectValidator;
        this.resourceProvider = resourceProvider;
        this.yamlConfigurationFileFactory = yamlConfigurationFileFactory;
    }

    @Override
    public LanguageConfiguration get() {
        String language = configuration.getLanguage();
        String fileName = DEFAULT_FILE_NAME.formatted(language);

        File langFile = new File(langFolder, fileName);
        InputStream resource = resourceProvider.getResource("lang/" + fileName);

        YamlConfigurationFile configurationFile = yamlConfigurationFileFactory.create(langFile, resource);

        return new LanguageConfiguration(configurationFile, logger, messageSpecFactory, objectValidator);
    }
}
