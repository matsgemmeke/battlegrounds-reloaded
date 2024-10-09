package nl.matsgemmeke.battlegrounds.text;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Map.Entry;

public class TextTemplate {

    private static final char DEFAULT_VAR_START = '%';
    private static final char DEFAULT_VAR_END = '%';

    @NotNull
    private String text;

    public TextTemplate(@NotNull String text) {
        this.text = text;
    }

    @NotNull
    public String getText() {
        return text;
    }

    @NotNull
    public String replace(@NotNull Map<String, Object> values) {
        for (Entry<String, Object> value : values.entrySet()) {
            String placeholder = DEFAULT_VAR_START + value.getKey() + DEFAULT_VAR_END;
            if (text.contains(placeholder)) {
                text = text.replace(placeholder, value.getValue().toString());
            }
        }
        return text;
    }
}
