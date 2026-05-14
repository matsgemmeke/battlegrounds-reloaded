package nl.matsgemmeke.battlegrounds.text;

import java.util.Map;
import java.util.Map.Entry;

public class TextTemplate {

    private static final char DEFAULT_VAR_START = '%';
    private static final char DEFAULT_VAR_END = '%';

    private final String text;

    public TextTemplate(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String replace(Map<String, Object> values) {
        String text = this.text;
        for (Entry<String, Object> value : values.entrySet()) {
            String placeholder = DEFAULT_VAR_START + value.getKey() + DEFAULT_VAR_END;
            if (text.contains(placeholder)) {
                text = text.replace(placeholder, value.getValue().toString());
            }
        }
        return text;
    }
}
