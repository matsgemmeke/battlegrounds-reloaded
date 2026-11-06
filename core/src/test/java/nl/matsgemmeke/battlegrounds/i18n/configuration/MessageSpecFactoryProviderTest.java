package nl.matsgemmeke.battlegrounds.i18n.configuration;

import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.i18n.configuration.actionbar.ActionBarMessageReader;
import nl.matsgemmeke.battlegrounds.i18n.configuration.actionbar.ActionBarMessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.configuration.chat.ChatMessageReader;
import nl.matsgemmeke.battlegrounds.i18n.configuration.chat.ChatMessageSpec;
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
class MessageSpecFactoryProviderTest {

    @Mock
    private ActionBarMessageReader actionBarMessageReader;
    @Mock
    private ChatMessageReader chatMessageReader;
    @InjectMocks
    private MessageSpecFactoryProvider provider;

    @Test
    @DisplayName("get returns instance with object readers")
    void get() {
        ActionBarMessageSpec actionBarMessageSpec = new ActionBarMessageSpec();
        ChatMessageSpec chatMessageSpec = new ChatMessageSpec();
        Section section = mock(Section.class);

        when(actionBarMessageReader.read(section)).thenReturn(actionBarMessageSpec);
        when(chatMessageReader.read(section)).thenReturn(chatMessageSpec);

        MessageSpecFactory messageSpecFactory = provider.get();
        MessageSpec actionBarResult = messageSpecFactory.create(MessageType.ACTION_BAR, section);
        MessageSpec chatResult = messageSpecFactory.create(MessageType.CHAT, section);

        assertThat(actionBarResult).isEqualTo(actionBarMessageSpec);
        assertThat(chatResult).isEqualTo(chatMessageSpec);
    }
}
