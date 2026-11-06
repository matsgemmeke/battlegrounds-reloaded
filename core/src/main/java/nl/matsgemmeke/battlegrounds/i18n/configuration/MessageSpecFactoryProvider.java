package nl.matsgemmeke.battlegrounds.i18n.configuration;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.i18n.configuration.actionbar.ActionBarMessageReader;
import nl.matsgemmeke.battlegrounds.i18n.configuration.chat.ChatMessageReader;
import nl.matsgemmeke.battlegrounds.i18n.configuration.title.TitleMessageReader;

public class MessageSpecFactoryProvider implements Provider<MessageSpecFactory> {

    private final ActionBarMessageReader actionBarMessageReader;
    private final ChatMessageReader chatMessageReader;
    private final TitleMessageReader titleMessageReader;

    @Inject
    public MessageSpecFactoryProvider(ActionBarMessageReader actionBarMessageReader, ChatMessageReader chatMessageReader, TitleMessageReader titleMessageReader) {
        this.actionBarMessageReader = actionBarMessageReader;
        this.chatMessageReader = chatMessageReader;
        this.titleMessageReader = titleMessageReader;
    }

    @Override
    public MessageSpecFactory get() {
        MessageSpecFactory messageSpecFactory = new MessageSpecFactory();
        messageSpecFactory.addObjectReader(MessageType.ACTION_BAR, actionBarMessageReader);
        messageSpecFactory.addObjectReader(MessageType.CHAT, chatMessageReader);
        messageSpecFactory.addObjectReader(MessageType.TITLE, titleMessageReader);
        return messageSpecFactory;
    }
}
