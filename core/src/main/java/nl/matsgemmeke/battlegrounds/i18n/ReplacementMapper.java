package nl.matsgemmeke.battlegrounds.i18n;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import nl.matsgemmeke.battlegrounds.i18n.Replacement.ReplacementType;

public class ReplacementMapper {

    public TagResolver map(Replacement replacement) {
        String key = replacement.key();
        String value = replacement.value();

        if (replacement.type() == ReplacementType.PARSED) {
            return Placeholder.parsed(key, value);
        } else {
            return Placeholder.unparsed(key, value);
        }
    }
}
