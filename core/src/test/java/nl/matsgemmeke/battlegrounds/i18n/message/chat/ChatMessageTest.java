package nl.matsgemmeke.battlegrounds.i18n.message.chat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ChatMessageTest {

    @Test
    @DisplayName("send sends component to given audience as a message")
    void send() {
        Component component = Component.text("test");
        Audience audience = mock(Audience.class);

        ChatMessage message = new ChatMessage(component);
        message.send(audience);

        verify(audience).sendMessage(component);
    }
}
