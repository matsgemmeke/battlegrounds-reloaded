package nl.matsgemmeke.battlegrounds.i18n;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import nl.matsgemmeke.battlegrounds.i18n.Replacement.ReplacementType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReplacementMapperTest {

    private static final String CLICKABLE_TEXT = "<click:suggest_command:<bg-command-suggestion>>Click to use command";
    private static final String UNCLICKABLE_TEXT = "Hello <bg-name>";

    private final ReplacementMapper replacementMapper = new ReplacementMapper();
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Test
    @DisplayName("map returns TagResolver for a parsed replacement")
    void map_parsed() {
        Replacement replacement = new Replacement("bg-command-suggestion", "/bg example", ReplacementType.PARSED);
        Component expected = miniMessage.deserialize("<click:suggest_command:/bg example>Click to use command");

        TagResolver tagResolver = replacementMapper.map(replacement);
        Component actual = miniMessage.deserialize(CLICKABLE_TEXT, tagResolver);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("map returns TagResolver for an unparsed replacement")
    void map_unparsed() {
        Replacement replacement = new Replacement("bg-name", "Steve", ReplacementType.UNPARSED);
        Component expected = miniMessage.deserialize("Hello Steve");

        Component actual = miniMessage.deserialize(UNCLICKABLE_TEXT, replacementMapper.map(replacement));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("map does not parse MiniMessage tags in the value for an unparsed replacement")
    void map_unparsedDoesNotInterpretTags() {
        Replacement replacement = new Replacement("bg-name", "<red>Steve</red>", ReplacementType.UNPARSED);
        Component expected = Component.text("Hello <red>Steve</red>");

        Component actual = miniMessage.deserialize(UNCLICKABLE_TEXT, replacementMapper.map(replacement));

        assertThat(actual).isEqualTo(expected);
    }
}
