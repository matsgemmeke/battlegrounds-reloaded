package nl.matsgemmeke.battlegrounds.i18n;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import nl.matsgemmeke.battlegrounds.i18n.configuration.LanguageConfiguration;

import java.util.Arrays;
import java.util.Optional;

/**
 * Class used for obtaining translated messages values from the language configuration files.
 */
public class Translator {

    private final LanguageConfiguration languageConfiguration;
    private final LegacyComponentSerializer legacyComponentSerializer;
    private final MiniMessage miniMessage;
    private final ReplacementMapper replacementMapper;

    @Inject
    public Translator(
            LanguageConfiguration languageConfiguration,
            LegacyComponentSerializer legacyComponentSerializer,
            MiniMessage miniMessage,
            ReplacementMapper replacementMapper
    ) {
        this.languageConfiguration = languageConfiguration;
        this.legacyComponentSerializer = legacyComponentSerializer;
        this.miniMessage = miniMessage;
        this.replacementMapper = replacementMapper;
    }

    /**
     * Gets the translation of an individual text value by its translation key, with MiniMessage tags converted to
     * legacy codes.
     *
     * @param translationKey the translation key
     * @param replacements   the placeholder replacements
     * @return               an optional with the text value converted to legacy, or empty when the given translation
     *                       key has no value
     */
    public Optional<String> translateLegacyText(String translationKey, Replacement... replacements) {
        String text = languageConfiguration.getTextValue(translationKey).orElse(null);

        if (text == null) {
            return Optional.empty();
        }

        TagResolver[] tagResolvers = Arrays.stream(replacements).map(replacementMapper::map).toArray(TagResolver[]::new);
        Component component = miniMessage.deserialize(text, tagResolvers);

        return Optional.of(legacyComponentSerializer.serialize(component));
    }

    /**
     * Gets the translation of a certain message by its translation key.
     *
     * @param translationKey the translation key
     * @param replacements   the placeholder replacements
     * @return               an optional with the message, or empty when the given translation key has no value
     */
    public Optional<Message> translateMessage(String translationKey, Replacement... replacements) {
        String text = languageConfiguration.getTextValue(translationKey).orElse(null);

        if (text == null) {
            return Optional.empty();
        }

        TagResolver[] tagResolvers = Arrays.stream(replacements).map(replacementMapper::map).toArray(TagResolver[]::new);
        Component component = miniMessage.deserialize(text, tagResolvers);

        return Optional.of(new Message(component));
    }
}
