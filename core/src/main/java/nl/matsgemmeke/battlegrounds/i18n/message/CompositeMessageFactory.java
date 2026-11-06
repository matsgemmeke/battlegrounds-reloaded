package nl.matsgemmeke.battlegrounds.i18n.message;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.configuration.actionbar.ActionBarMessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.configuration.chat.ChatMessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.configuration.MessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.configuration.title.TitleMessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.message.actionbar.ActionBarMessageFactory;
import nl.matsgemmeke.battlegrounds.i18n.message.chat.ChatMessageFactory;
import nl.matsgemmeke.battlegrounds.i18n.message.title.TitleMessageFactory;

public class CompositeMessageFactory {

    private final ActionBarMessageFactory actionBarMessageFactory;
    private final ChatMessageFactory chatMessageFactory;
    private final TitleMessageFactory titleMessageFactory;

    @Inject
    public CompositeMessageFactory(ActionBarMessageFactory actionBarMessageFactory, ChatMessageFactory chatMessageFactory, TitleMessageFactory titleMessageFactory) {
        this.actionBarMessageFactory = actionBarMessageFactory;
        this.chatMessageFactory = chatMessageFactory;
        this.titleMessageFactory = titleMessageFactory;
    }

    public Message create(MessageSpec messageSpec, Replacement... replacements) {
        return switch (messageSpec) {
            case ActionBarMessageSpec actionBarMessageSpec -> actionBarMessageFactory.create(actionBarMessageSpec, replacements);
            case ChatMessageSpec chatMessageSpec -> chatMessageFactory.create(chatMessageSpec, replacements);
            case TitleMessageSpec titleMessageSpec -> titleMessageFactory.create(titleMessageSpec, replacements);
            default -> throw new IllegalStateException("Unexpected value: " + messageSpec);
        };
    }
}
