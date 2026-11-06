package nl.matsgemmeke.battlegrounds.i18n.message.chat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.configuration.chat.ChatMessageSpec;
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
class ChatMessageFactoryTest {

    private static final String TEXT = "<gold>good <time>";

    @Spy
    private MiniMessage miniMessage = MiniMessage.miniMessage();
    @InjectMocks
    private ChatMessageFactory chatMessageFactory;

    @Test
    void create() {
        Replacement replacement = Replacement.of("time", "evening");
        Audience audience = mock(Audience.class);

        ChatMessageSpec spec = new ChatMessageSpec();
        spec.setText(TEXT);

        ChatMessage chatMessage = chatMessageFactory.create(spec, replacement);
        chatMessage.send(audience);

        ArgumentCaptor<TextComponent> componentCaptor = ArgumentCaptor.forClass(TextComponent.class);
        verify(audience).sendMessage(componentCaptor.capture());

        assertThat(miniMessage.serialize(componentCaptor.getValue())).isEqualTo("<gold>good evening");
    }
}
