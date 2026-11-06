package nl.matsgemmeke.battlegrounds.i18n.message.actionbar;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.configuration.actionbar.ActionBarMessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.message.MessageFactory;

import java.util.Arrays;

public class ActionBarMessageFactory implements MessageFactory<ActionBarMessageSpec> {

    private final MiniMessage miniMessage;

    @Inject
    public ActionBarMessageFactory(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    @Override
    public ActionBarMessage create(ActionBarMessageSpec spec, Replacement... replacements) {
        TagResolver[] tagResolvers = Arrays.stream(replacements)
                .map(replacement -> Placeholder.unparsed(replacement.key(), replacement.value()))
                .toArray(TagResolver[]::new);

        String text = spec.getText();
        Component component = miniMessage.deserialize(text, tagResolvers);

        return new ActionBarMessage(component);
    }
}
