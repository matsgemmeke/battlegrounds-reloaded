package nl.matsgemmeke.battlegrounds.i18n.message;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.configuration.actionbar.ActionBarMessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.configuration.chat.ChatMessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.configuration.MessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.message.actionbar.ActionBarMessageFactory;
import nl.matsgemmeke.battlegrounds.i18n.message.chat.ChatMessageFactory;

public class CompositeMessageFactory {

    private final ActionBarMessageFactory actionBarMessageFactory;
    private final ChatMessageFactory chatMessageFactory;

    @Inject
    public CompositeMessageFactory(ActionBarMessageFactory actionBarMessageFactory, ChatMessageFactory chatMessageFactory) {
        this.actionBarMessageFactory = actionBarMessageFactory;
        this.chatMessageFactory = chatMessageFactory;
    }

    public Message create(MessageSpec messageSpec, Replacement... replacements) {
        return switch (messageSpec) {
            case ActionBarMessageSpec actionBarMessageSpec -> actionBarMessageFactory.create(actionBarMessageSpec, replacements);
            case ChatMessageSpec chatMessageSpec -> chatMessageFactory.create(chatMessageSpec, replacements);
            default -> throw new IllegalStateException("Unexpected value: " + messageSpec);
        };
    }
}
