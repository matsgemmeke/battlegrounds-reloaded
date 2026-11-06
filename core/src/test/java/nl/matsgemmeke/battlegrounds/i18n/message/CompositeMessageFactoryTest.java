package nl.matsgemmeke.battlegrounds.i18n.message;

import nl.matsgemmeke.battlegrounds.i18n.configuration.actionbar.ActionBarMessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.configuration.chat.ChatMessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.configuration.title.TitleMessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.message.actionbar.ActionBarMessageFactory;
import nl.matsgemmeke.battlegrounds.i18n.message.chat.ChatMessageFactory;
import nl.matsgemmeke.battlegrounds.i18n.message.title.TitleMessageFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompositeMessageFactoryTest {

    @Mock
    private ActionBarMessageFactory actionBarMessageFactory;
    @Mock
    private ChatMessageFactory chatMessageFactory;
    @Mock
    private TitleMessageFactory titleMessageFactory;
    @InjectMocks
    private CompositeMessageFactory compositeMessageFactory;

    @Test
    @DisplayName("create returns ActionBarMessage when given spec is for an action bar message")
    void create_actionBarMessage() {
        ActionBarMessageSpec spec = new ActionBarMessageSpec();
        Message actionBarMessage = mock(Message.class);

        when(actionBarMessageFactory.create(spec)).thenReturn(actionBarMessage);

        Message message = compositeMessageFactory.create(spec);

        assertThat(message).isEqualTo(actionBarMessage);
    }

    @Test
    @DisplayName("create returns ChatMessage when given spec is for a chat message")
    void create_chatMessage() {
        ChatMessageSpec spec = new ChatMessageSpec();
        Message chatMessage = mock(Message.class);

        when(chatMessageFactory.create(spec)).thenReturn(chatMessage);

        Message message = compositeMessageFactory.create(spec);

        assertThat(message).isEqualTo(chatMessage);
    }

    @Test
    @DisplayName("create returns TitleMessage when given spec is for a title message")
    void create_titleMessage() {
        TitleMessageSpec spec = new TitleMessageSpec();
        Message titleMessage = mock(Message.class);

        when(titleMessageFactory.create(spec)).thenReturn(titleMessage);

        Message message = compositeMessageFactory.create(spec);

        assertThat(message).isEqualTo(titleMessage);
    }
}
