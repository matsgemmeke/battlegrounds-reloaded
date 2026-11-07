package nl.matsgemmeke.battlegrounds.i18n;

import com.google.inject.Inject;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.i18n.configuration.LanguageConfiguration;
import nl.matsgemmeke.battlegrounds.i18n.message.CompositeMessageFactory;
import nl.matsgemmeke.battlegrounds.i18n.message.Message;
import org.bukkit.ChatColor;

import java.util.Optional;

/**
 * Class used for obtaining translated messages values from the language configuration files.
 */
public class Translator {

    private final BattlegroundsConfiguration battlegroundsConfiguration;
    private final CompositeMessageFactory compositeMessageFactory;
    private final LanguageConfiguration languageConfiguration;

    @Inject
    public Translator(BattlegroundsConfiguration battlegroundsConfiguration, CompositeMessageFactory compositeMessageFactory, LanguageConfiguration languageConfiguration, MiniMessage miniMessage) {
        this.battlegroundsConfiguration = battlegroundsConfiguration;
        this.compositeMessageFactory = compositeMessageFactory;
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

    /**
     * Gets the translation of a certain message by its translation key path.
     *
     * @param translationKey the translation key
     * @param replacements   the placeholder replacements
     * @return               an optional with the deserialized component, or empty when the given translation key has
     *                       no value
     */
    public Optional<Message> translateNew(String translationKey, Replacement... replacements) {
        return languageConfiguration.getMessageSpec(translationKey)
                .map(messageSpec -> compositeMessageFactory.create(messageSpec, replacements));
    }
}
