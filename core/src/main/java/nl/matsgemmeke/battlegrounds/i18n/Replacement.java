package nl.matsgemmeke.battlegrounds.i18n;

import net.kyori.adventure.text.minimessage.tag.TagPattern;
import org.intellij.lang.annotations.Subst;

public record Replacement(@Subst("placeholder") @TagPattern String key, String value) {

    public static Replacement of(@Subst("placeholder") @TagPattern String key, Object value) {
        return new Replacement(key, String.valueOf(value));
    }
}
