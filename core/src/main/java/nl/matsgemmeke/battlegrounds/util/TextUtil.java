package nl.matsgemmeke.battlegrounds.util;

public final class TextUtil {

    public static String pluralize(int count, String singular, String plural) {
        return count == 1 ? singular : plural;
    }
}
