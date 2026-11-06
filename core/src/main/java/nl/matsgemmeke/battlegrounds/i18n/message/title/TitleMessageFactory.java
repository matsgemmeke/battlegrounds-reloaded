package nl.matsgemmeke.battlegrounds.i18n.message.title;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.configuration.title.TitleMessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.message.Message;
import nl.matsgemmeke.battlegrounds.i18n.message.MessageFactory;

import java.util.Arrays;

public class TitleMessageFactory implements MessageFactory<TitleMessageSpec> {

    private final MiniMessage miniMessage;

    @Inject
    public TitleMessageFactory(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    @Override
    public Message create(TitleMessageSpec spec, Replacement... replacements) {
        TagResolver[] tagResolvers = Arrays.stream(replacements)
                .map(replacement -> Placeholder.unparsed(replacement.key(), replacement.value()))
                .toArray(TagResolver[]::new);

        String titleText = spec.getTitleText();
        Component titleComponent = miniMessage.deserialize(titleText, tagResolvers);

        String subtitleText = spec.getSubtitleText();
        Component subtitleComponent = miniMessage.deserialize(subtitleText, tagResolvers);

        int fadeInTicks = spec.getFadeInTicks();
        int stayTicks = spec.getStayTicks();
        int fadeOutTicks = spec.getFadeOutTicks();

        Title title = Title.title(titleComponent, subtitleComponent, fadeInTicks, stayTicks, fadeOutTicks);

        return new TitleMessage(title);
    }
}
