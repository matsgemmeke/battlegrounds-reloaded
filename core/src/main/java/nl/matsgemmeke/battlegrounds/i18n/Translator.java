package nl.matsgemmeke.battlegrounds.i18n;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.i18n.configuration.LanguageConfiguration;
import org.bukkit.ChatColor;

import java.util.Optional;

/**
 * Class used for obtaining translated messages values from the language configuration files.
 */
public class Translator {

    private final BattlegroundsConfiguration battlegroundsConfiguration;
    private final LanguageConfiguration languageConfiguration;
    private final MiniMessage miniMessage;

    @Inject
    public Translator(BattlegroundsConfiguration battlegroundsConfiguration, LanguageConfiguration languageConfiguration, MiniMessage miniMessage) {
        this.battlegroundsConfiguration = battlegroundsConfiguration;
        this.languageConfiguration = languageConfiguration;
        this.miniMessage = miniMessage;
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

    /**
     * Gets the translation of a certain message by its translation key path.
     *
     * @param translationKey the translation key
     * @param tagResolvers   the tag resolvers filling placeholders
     * @return               an optional with the deserialized component, or empty when the given translation key has
     *                       no value
     */
    public Optional<Component> translateNew(String translationKey, TagResolver... tagResolvers) {
        String textValue = languageConfiguration.getTextValue(translationKey).orElse(null);

        if (textValue == null) {
            return Optional.empty();
        }

        return Optional.of(miniMessage.deserialize(textValue, tagResolvers));
    }
}
