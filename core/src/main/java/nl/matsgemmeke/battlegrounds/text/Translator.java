package nl.matsgemmeke.battlegrounds.text;

import nl.matsgemmeke.battlegrounds.configuration.LanguageConfiguration;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * Class used for obtaining translated messages values from the language configuration files.
 */
public class Translator {

    @NotNull
    private LanguageConfiguration languageConfiguration;

    public Translator(@NotNull LanguageConfiguration languageConfiguration) {
        this.languageConfiguration = languageConfiguration;
    }

    /**
     * Gets the current language configuration of the translator.
     *
     * @return the translator's language configuration
     */
    @NotNull
    public LanguageConfiguration getLanguageConfiguration() {
        return languageConfiguration;
    }

    /**
     * Sets the current language configuration of the translator.
     *
     * @param languageConfiguration the translator's language configuration
     */
    public void setLanguageConfiguration(@NotNull LanguageConfiguration languageConfiguration) {
        this.languageConfiguration = languageConfiguration;
    }

    /**
     * Gets the translation of a certain message by its translation key path.
     *
     * @param key the translation key
     * @return the translation message from the message path as a text template
     */
    @NotNull
    public TextTemplate translate(@NotNull String key) {
        String translation = languageConfiguration.getString(key);

        if (translation == null) {
            throw new InvalidTranslationKeyException("Translation for key \"" + key + "\" in language configuration '"
                    + languageConfiguration.getLocale().getCountry() + "' not found");
        }

        return new TextTemplate(ChatColor.translateAlternateColorCodes('&', translation));
    }
}