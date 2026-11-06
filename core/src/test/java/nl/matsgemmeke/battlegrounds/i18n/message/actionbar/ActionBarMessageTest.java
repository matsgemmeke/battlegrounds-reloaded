package nl.matsgemmeke.battlegrounds.i18n.message.actionbar;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ActionBarMessageTest {

    @Test
    void send() {
        Component component = Component.text("test");
        Audience audience = mock(Audience.class);

        ActionBarMessage message = new ActionBarMessage(component);
        message.send(audience);

        verify(audience).sendActionBar(component);
    }
}
