package nl.matsgemmeke.battlegrounds.i18n;

import net.kyori.adventure.text.minimessage.tag.TagPattern;
import org.intellij.lang.annotations.Subst;

public record Replacement(@Subst("placeholder") @TagPattern String key, Object value, ReplacementType type) {

    public static Replacement parsed(@Subst("placeholder") @TagPattern String key, Object value) {
        return new Replacement(key, value, ReplacementType.PARSED);
    }

    public static Replacement unparsed(@Subst("placeholder") @TagPattern String key, Object value) {
        return new Replacement(key, value, ReplacementType.UNPARSED);
    }

    enum ReplacementType {

        PARSED, UNPARSED
    }
}
