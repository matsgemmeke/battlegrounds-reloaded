package nl.matsgemmeke.battlegrounds.i18n;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageTest {

    @Spy
    private Component component = Component.text("test");
    @InjectMocks
    private Message message;

    @Test
    @DisplayName("send sends component to given audience")
    void send() {
        Audience audience = mock(Audience.class);

        message.send(audience);

        verify(audience).sendMessage(component);
    }
}
