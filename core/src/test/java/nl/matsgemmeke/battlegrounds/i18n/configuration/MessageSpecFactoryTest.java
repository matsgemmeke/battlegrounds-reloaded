package nl.matsgemmeke.battlegrounds.i18n.configuration;

import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.configuration.polymorphic.ObjectReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MessageSpecFactoryTest {

    private MessageSpecFactory messageSpecFactory;

    @BeforeEach
    void setUp() {
        messageSpecFactory = new MessageSpecFactory();
    }

    @Test
    @DisplayName("create throws MessageSpecCreationException when no object reader for given message type is present")
    void create_missingObjectReader() {
        Section section = mock(Section.class);

        assertThatThrownBy(() -> messageSpecFactory.create(MessageType.CHAT, section))
                .isInstanceOf(MessageSpecCreationException.class)
                .hasMessage("Unable to create message with type CHAT because no object reader is present");
    }

    @Test
    @DisplayName("create returns MessageSpec created from corresponding object reader")
    void create_successful() {
        Section section = mock(Section.class);
        MessageSpec messageSpec = mock(MessageSpec.class);

        ObjectReader<MessageSpec> objectReader = mock();
        when(objectReader.read(section)).thenReturn(messageSpec);

        messageSpecFactory.addObjectReader(MessageType.CHAT, objectReader);
        MessageSpec result = messageSpecFactory.create(MessageType.CHAT, section);

        assertThat(result).isEqualTo(messageSpec);
    }
}
