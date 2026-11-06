package nl.matsgemmeke.battlegrounds.i18n.configuration.title;

import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.i18n.configuration.MessageSpec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TitleMessageReaderTest {

    private static final String TITLE_TEXT = "<gold>a cool title";
    private static final String SUBTITLE_TEXT = "<gold>a cool subtitle";
    private static final int FADE_IN_TICKS = 5;
    private static final int STAY_TICKS = 60;
    private static final int FADE_OUT_TICKS = 10;

    private final TitleMessageReader titleMessageReader = new TitleMessageReader();

    @Test
    @DisplayName("read returns TitleMessageSpec with all values present")
    void read_completeSpec() {
        Section section = mock(Section.class);
        when(section.getString("title-text")).thenReturn(Optional.of(TITLE_TEXT));
        when(section.getString("subtitle-text")).thenReturn(Optional.of(SUBTITLE_TEXT));
        when(section.getInt("fade-in-ticks")).thenReturn(Optional.of(FADE_IN_TICKS));
        when(section.getInt("stay-ticks")).thenReturn(Optional.of(STAY_TICKS));
        when(section.getInt("fade-out-ticks")).thenReturn(Optional.of(FADE_OUT_TICKS));

        MessageSpec result = titleMessageReader.read(section);

        assertThat(result).isInstanceOfSatisfying(TitleMessageSpec.class, spec -> {
            assertThat(spec.getTitleText()).isEqualTo(TITLE_TEXT);
            assertThat(spec.getSubtitleText()).isEqualTo(SUBTITLE_TEXT);
            assertThat(spec.getFadeInTicks()).isEqualTo(FADE_IN_TICKS);
            assertThat(spec.getStayTicks()).isEqualTo(STAY_TICKS);
            assertThat(spec.getFadeOutTicks()).isEqualTo(FADE_OUT_TICKS);
        });
    }

    @Test
    @DisplayName("read returns TitleMessageSpec with defaults for values that are not present")
    void read_missingValues() {
        Section section = mock(Section.class);
        when(section.getString("title-text")).thenReturn(Optional.empty());
        when(section.getString("subtitle-text")).thenReturn(Optional.empty());
        when(section.getInt("fade-in-ticks")).thenReturn(Optional.empty());
        when(section.getInt("stay-ticks")).thenReturn(Optional.empty());
        when(section.getInt("fade-out-ticks")).thenReturn(Optional.empty());

        MessageSpec result = titleMessageReader.read(section);

        assertThat(result).isInstanceOfSatisfying(TitleMessageSpec.class, spec -> {
            assertThat(spec.getTitleText()).isNull();
            assertThat(spec.getSubtitleText()).isNull();
            assertThat(spec.getFadeInTicks()).isZero();
            assertThat(spec.getStayTicks()).isZero();
            assertThat(spec.getFadeOutTicks()).isZero();
        });
    }
}
