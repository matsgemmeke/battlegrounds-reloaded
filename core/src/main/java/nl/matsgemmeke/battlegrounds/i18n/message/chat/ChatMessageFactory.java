package nl.matsgemmeke.battlegrounds.i18n.message.chat;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.configuration.chat.ChatMessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.message.Message;
import nl.matsgemmeke.battlegrounds.i18n.message.MessageFactory;

import java.util.Arrays;
import java.util.List;

public class ChatMessageFactory implements MessageFactory<ChatMessageSpec> {

    private final MiniMessage miniMessage;

    @Inject
    public ChatMessageFactory(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    @Override
    public Message create(ChatMessageSpec spec, Replacement... replacements) {
        TagResolver[] tagResolvers = Arrays.stream(replacements)
                .map(replacement -> Placeholder.unparsed(replacement.key(), replacement.value()))
                .toArray(TagResolver[]::new);

        List<Component> components = spec.getLines().stream()
                .map(line -> miniMessage.deserialize(line, tagResolvers))
                .toList();

        return new ChatMessage(components);
    }
}
