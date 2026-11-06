package nl.matsgemmeke.battlegrounds.i18n.configuration.chat;

import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.i18n.configuration.MessageSpec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatMessageReaderTest {

    private static final String TEXT = "<gold>hello";

    private final ChatMessageReader chatMessageReader = new ChatMessageReader();

    @Test
    @DisplayName("read returns ChatMessageSpec from given section")
    void read() {
        Section section = mock(Section.class);
        when(section.getString("text")).thenReturn(Optional.of(TEXT));

        MessageSpec result = chatMessageReader.read(section);

        assertThat(result).isInstanceOfSatisfying(ChatMessageSpec.class, spec -> {
            assertThat(spec.getText()).isEqualTo(TEXT);
        });
    }
}
