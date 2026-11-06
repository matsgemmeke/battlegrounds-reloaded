package nl.matsgemmeke.battlegrounds.i18n.configuration;

import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.configuration.polymorphic.ObjectReader;
import nl.matsgemmeke.battlegrounds.configuration.polymorphic.ObjectReaderRegistry;

public class MessageSpecFactory {

    private final ObjectReaderRegistry<MessageType, MessageSpec> objectReaderRegistry;

    public MessageSpecFactory() {
        this.objectReaderRegistry = new ObjectReaderRegistry<>();
    }

    public void addObjectReader(MessageType messageType, ObjectReader<MessageSpec> objectReader) {
        objectReaderRegistry.register(messageType, objectReader);
    }

    public MessageSpec create(MessageType messageType, Section section) {
        ObjectReader<MessageSpec> objectReader = objectReaderRegistry.getSectionReader(messageType).orElse(null);

        if (objectReader == null) {
            throw new MessageSpecCreationException("Unable to create message with type %s because no object reader is present".formatted(messageType));
        }

        return objectReader.read(section);
    }
}
