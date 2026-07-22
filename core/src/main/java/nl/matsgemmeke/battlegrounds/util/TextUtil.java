package nl.matsgemmeke.battlegrounds.util;

public final class TextUtil {

    public static String pluralize(int count, String singular, String plural) {
        return count == 1 ? singular : plural;
    }

    public static String toKebabCase(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input must be text with at least one character");
        }

        return input
                // Insert a hyphen between a lowercase/digit and an uppercase letter (camelCase -> camel-Case)
                .replaceAll("([a-z0-9])([A-Z])", "$1-$2")
                // Handle sequences like "HTTPRequest" -> "HTTP-Request"
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1-$2")
                // Replace spaces, underscores, and repeated hyphens with a single hyphen
                .replaceAll("[\\s_]+", "-")
                .replaceAll("-+", "-")
                .toLowerCase();
    }
}
