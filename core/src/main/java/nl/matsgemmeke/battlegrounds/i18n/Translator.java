package nl.matsgemmeke.battlegrounds.i18n;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.i18n.configuration.LanguageConfiguration;
import org.bukkit.ChatColor;

import java.util.Arrays;
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
     * @param replacements   the placeholder replacements
     * @return               an optional with the deserialized component, or empty when the given translation key has
     *                       no value
     */
    public Optional<Message> translateNew(String translationKey, Replacement... replacements) {
        String text = languageConfiguration.getTextValue(translationKey).orElse(null);

        if (text == null) {
            return Optional.empty();
        }

        TagResolver[] tagResolvers = Arrays.stream(replacements)
                .map(replacement -> Placeholder.unparsed(replacement.key(), replacement.value()))
                .toArray(TagResolver[]::new);
        Component component = miniMessage.deserialize(text, tagResolvers);

        return Optional.of(new Message(component));
    }
}
