package nl.matsgemmeke.battlegrounds.i18n.message.actionbar;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.configuration.actionbar.ActionBarMessageSpec;
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
class ActionBarMessageFactoryTest {

    private static final String TEXT = "<gold>good <time>";

    @Spy
    private MiniMessage miniMessage = MiniMessage.miniMessage();
    @InjectMocks
    private ActionBarMessageFactory actionBarMessageFactory;

    @Test
    void create() {
        Replacement replacement = Replacement.of("time", "evening");
        Audience audience = mock(Audience.class);

        ActionBarMessageSpec spec = new ActionBarMessageSpec();
        spec.setText(TEXT);

        ActionBarMessage actionBarMessage = actionBarMessageFactory.create(spec, replacement);
        actionBarMessage.send(audience);

        ArgumentCaptor<TextComponent> componentCaptor = ArgumentCaptor.forClass(TextComponent.class);
        verify(audience).sendActionBar(componentCaptor.capture());

        assertThat(miniMessage.serialize(componentCaptor.getValue())).isEqualTo("<gold>good evening");
    }
}
