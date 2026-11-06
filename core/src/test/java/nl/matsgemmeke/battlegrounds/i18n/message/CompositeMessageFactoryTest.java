package nl.matsgemmeke.battlegrounds.i18n.message;

import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.configuration.chat.ChatMessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.message.chat.ChatMessage;
import nl.matsgemmeke.battlegrounds.i18n.message.chat.ChatMessageFactory;
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
    private ChatMessageFactory chatMessageFactory;
    @InjectMocks
    private CompositeMessageFactory compositeMessageFactory;

    @Test
    @DisplayName("create returns ChatMessage when given spec is for a chat message")
    void create_chatMessage() {
        ChatMessageSpec spec = new ChatMessageSpec();
        Replacement[] replacements = { Replacement.of("test", "test") };
        ChatMessage chatMessage = mock(ChatMessage.class);

        when(chatMessageFactory.create(spec, replacements)).thenReturn(chatMessage);

        Message message = compositeMessageFactory.create(spec, replacements);

        assertThat(message).isEqualTo(chatMessage);
    }
}
