package nl.matsgemmeke.battlegrounds.i18n.message;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.configuration.chat.ChatMessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.configuration.MessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.message.chat.ChatMessageFactory;

public class CompositeMessageFactory {

    private final ChatMessageFactory chatMessageFactory;

    @Inject
    public CompositeMessageFactory(ChatMessageFactory chatMessageFactory) {
        this.chatMessageFactory = chatMessageFactory;
    }

    public Message create(MessageSpec messageSpec, Replacement... replacements) {
        return switch (messageSpec) {
            case ChatMessageSpec chatMessageSpec -> chatMessageFactory.create(chatMessageSpec, replacements);
            default -> throw new IllegalStateException("Unexpected value: " + messageSpec);
        };
    }
}
