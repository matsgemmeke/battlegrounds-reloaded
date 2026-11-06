package nl.matsgemmeke.battlegrounds.i18n.message.title;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.configuration.title.TitleMessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.message.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TitleMessageFactoryTest {

    private static final String TITLE_TEXT = "<gold>good <time>";
    private static final String SUBTITLE_TEXT = "<gold>the current time is <current-time>";
    private static final Integer FADE_IN_TICKS = 5;
    private static final Integer STAY_TICKS = 60;
    private static final Integer FADE_OUT_TICKS = 10;

    @Spy
    private MiniMessage miniMessage = MiniMessage.miniMessage();
    @InjectMocks
    private TitleMessageFactory titleMessageFactory;

    @Test
    void create() {
        Replacement timeReplacement = Replacement.of("time", "evening");
        Replacement currentTimeReplacement = Replacement.of("current-time", "10:30AM");
        Audience audience = mock(Audience.class);

        TitleMessageSpec spec = new TitleMessageSpec();
        spec.setTitleText(TITLE_TEXT);
        spec.setSubtitleText(SUBTITLE_TEXT);
        spec.setFadeInTicks(FADE_IN_TICKS);
        spec.setStayTicks(STAY_TICKS);
        spec.setFadeOutTicks(FADE_OUT_TICKS);

        Message message = titleMessageFactory.create(spec, timeReplacement, currentTimeReplacement);
        message.send(audience);

        ArgumentCaptor<Title> titleCaptor = ArgumentCaptor.forClass(Title.class);
        verify(audience).showTitle(titleCaptor.capture());

        assertThat(titleCaptor.getValue()).satisfies(title -> {
            assertThat(miniMessage.serialize(title.title())).isEqualTo("<gold>good evening");
            assertThat(miniMessage.serialize(title.subtitle())).isEqualTo("<gold>the current time is 10:30AM");
            assertThat(title.times()).satisfies(times -> {
                assertThat(times).isNotNull();
                assertThat(times.fadeIn()).hasSeconds(0).hasMillis(250);
                assertThat(times.stay()).hasSeconds(3);
                assertThat(times.fadeOut()).hasSeconds(0).hasMillis(500);
            });
        });
    }
}
