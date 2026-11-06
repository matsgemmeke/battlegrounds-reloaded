package nl.matsgemmeke.battlegrounds.i18n.message.title;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TitleMessageTest {

    @Test
    @DisplayName("send sends title to given audience")
    void send() {
        Component component = Component.text("test");
        Title title = Title.title(component, component);
        Audience audience = mock(Audience.class);

        TitleMessage message = new TitleMessage(title);
        message.send(audience);

        verify(audience).showTitle(title);
    }
}
