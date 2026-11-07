package nl.matsgemmeke.battlegrounds.i18n.configuration.chat;

import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.i18n.configuration.MessageSpec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatMessageReaderTest {

    private static final String TEXT_ONE = "<gold>good morning";
    private static final String TEXT_TWO = "<gold>good evening";

    private final ChatMessageReader chatMessageReader = new ChatMessageReader();

    @Test
    @DisplayName("read returns ChatMessageSpec as a single line from given section")
    void read_singleLine() {
        Section section = mock(Section.class);
        when(section.isList("lines")).thenReturn(false);
        when(section.getString("lines")).thenReturn(Optional.of(TEXT_ONE));

        MessageSpec result = chatMessageReader.read(section);

        assertThat(result).isInstanceOfSatisfying(ChatMessageSpec.class, spec -> {
            assertThat(spec.getLines()).containsExactly(TEXT_ONE);
        });
    }

    @Test
    @DisplayName("read returns ChatMessageSpec as multiple lines from given section")
    void read_multipleLines() {
        Section section = mock(Section.class);
        when(section.isList("lines")).thenReturn(true);
        when(section.getStringList("lines")).thenReturn(List.of(TEXT_ONE, TEXT_TWO));

        MessageSpec result = chatMessageReader.read(section);

        assertThat(result).isInstanceOfSatisfying(ChatMessageSpec.class, spec -> {
            assertThat(spec.getLines()).containsExactly(TEXT_ONE, TEXT_TWO);
        });
    }
}
