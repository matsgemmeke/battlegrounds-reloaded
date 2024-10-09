package nl.matsgemmeke.battlegrounds.text;

import nl.matsgemmeke.battlegrounds.configuration.LanguageConfiguration;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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
     * @return the translation message from the message path
     */
    @NotNull
    public String translate(@NotNull String key) {
        String translation = languageConfiguration.getString(key);

        if (translation == null) {
            throw new InvalidTranslationKeyException("Translation for key \"" + key + "\" in language configuration '"
                    + languageConfiguration.getLocale().getCountry() + "' not found");
        }

        return ChatColor.translateAlternateColorCodes('&', translation);
    }

    /**
     * Gets the translation of a certain message by its translation key path and also replaces a single placeholder
     * value with a given argument.
     *
     * @param key the translation key
     * @param placeholder the placeholder entry
     * @return the translation message from the message path
     */
    @NotNull
    public String translate(@NotNull String key, @NotNull PlaceholderEntry placeholder) {
        String translation = this.translate(key);
        String placeholderId = "%" + placeholder.getKey() + "%";

        if (translation.contains(placeholderId)) {
            translation = translation.replaceAll(placeholderId, placeholder.getValue());
        }

        return translation;
    }

    /**
     * Gets the translation of a certain message by its translation key path and also replaces placeholder values with
     * given arguments. The placeholders should be given as a {@link Map}, where the key should indicate the placeholder
     * name, and the value should indicate the text that should replace the placeholder.
     *
     * @param key the translation key
     * @param placeholderValues the placeholder values
     * @return the translation message from the message path
     */
    @NotNull
    public String translate(@NotNull String key, @NotNull Iterable<PlaceholderEntry> placeholderValues) {
        String translation = this.translate(key);

        for (PlaceholderEntry placeholder : placeholderValues) {
            String placeholderId = "%" + placeholder.getKey() + "%";

            if (translation.contains(placeholderId)) {
                translation = translation.replaceAll(placeholderId, placeholder.getValue());
            }
        }

        return translation;
    }
}
