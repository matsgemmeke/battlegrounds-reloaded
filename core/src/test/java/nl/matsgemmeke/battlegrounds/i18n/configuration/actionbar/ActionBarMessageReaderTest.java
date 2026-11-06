package nl.matsgemmeke.battlegrounds.i18n.configuration.actionbar;

import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.i18n.configuration.MessageSpec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ActionBarMessageReaderTest {

    private static final String TEXT = "<gold>hello";

    private final ActionBarMessageReader actionBarMessageReader = new ActionBarMessageReader();

    @Test
    @DisplayName("read returns ActionBarMessageSpec from given section")
    void read() {
        Section section = mock(Section.class);
        when(section.getString("text")).thenReturn(Optional.of(TEXT));

        MessageSpec result = actionBarMessageReader.read(section);

        assertThat(result).isInstanceOfSatisfying(ActionBarMessageSpec.class, spec -> {
            assertThat(spec.getText()).isEqualTo(TEXT);
        });
    }
}
