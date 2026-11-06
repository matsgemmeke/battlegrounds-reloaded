package nl.matsgemmeke.battlegrounds.i18n.configuration;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.i18n.configuration.chat.ChatMessageReader;

public class MessageSpecFactoryProvider implements Provider<MessageSpecFactory> {

    private final ChatMessageReader chatMessageReader;

    @Inject
    public MessageSpecFactoryProvider(ChatMessageReader chatMessageReader) {
        this.chatMessageReader = chatMessageReader;
    }

    @Override
    public MessageSpecFactory get() {
        MessageSpecFactory messageSpecFactory = new MessageSpecFactory();
        messageSpecFactory.addObjectReader(MessageType.CHAT, chatMessageReader);
        return messageSpecFactory;
    }
}
