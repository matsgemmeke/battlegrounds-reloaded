package nl.matsgemmeke.battlegrounds.locale;

import org.jetbrains.annotations.NotNull;

public class PlaceholderEntry {

    @NotNull
    private String key;
    @NotNull
    private String value;

    public PlaceholderEntry(@NotNull String key, @NotNull String value) {
        this.key = key;
        this.value = value;
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @NotNull
    public String getValue() {
        return value;
    }
}
