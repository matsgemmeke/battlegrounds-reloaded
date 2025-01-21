package nl.matsgemmeke.battlegrounds.configuration.lang;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.Locale;

public class LanguageConfigurationProvider implements Provider<LanguageConfiguration> {

    @NotNull
    private BattlegroundsConfiguration configuration;
    @NotNull
    private File langFolder;
    @NotNull
    private Plugin plugin;

    @Inject
    public LanguageConfigurationProvider(
            @NotNull BattlegroundsConfiguration configuration,
            @Named("LangFolder") @NotNull File langFolder,
            @NotNull Plugin plugin
    ) {
        this.configuration = configuration;
        this.langFolder = langFolder;
        this.plugin = plugin;
    }

    public LanguageConfiguration get() {
        String language = configuration.getLanguage();
        String fileName = "lang_" + language + ".yml";

        File langFile = langFolder.toPath().resolve(fileName).toFile();
        InputStream resource = plugin.getResource(fileName);
        Locale locale = Locale.forLanguageTag(language);

        LanguageConfiguration languageConfiguration = new LanguageConfiguration(langFile, resource, locale);
        languageConfiguration.load();
        return languageConfiguration;
    }
}
