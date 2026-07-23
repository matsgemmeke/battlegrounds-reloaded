package nl.matsgemmeke.battlegrounds.text;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.lang.LanguageConfiguration;
import org.bukkit.ChatColor;

/**
 * Class used for obtaining translated messages values from the language configuration files.
 */
public class Translator {

    private final BattlegroundsConfiguration battlegroundsConfiguration;
    private final LanguageConfiguration languageConfiguration;

    @Inject
    public Translator(BattlegroundsConfiguration battlegroundsConfiguration, LanguageConfiguration languageConfiguration) {
        this.battlegroundsConfiguration = battlegroundsConfiguration;
        this.languageConfiguration = languageConfiguration;
    }

    /**
     * Gets the translation of a certain message by its translation key path.
     *
     * @param key the translation key
     * @return the translation message from the message path as a text template
     */
    public TextTemplate translate(String key) {
        String textValue = languageConfiguration.getTextValue(key).orElse(null);

        if (textValue == null) {
            throw new InvalidTranslationKeyException("Translation for key \"" + key + "\" in language configuration '" + battlegroundsConfiguration.getLanguage() + "' not found");
        }

        return new TextTemplate(ChatColor.translateAlternateColorCodes('&', textValue));
    }
}
