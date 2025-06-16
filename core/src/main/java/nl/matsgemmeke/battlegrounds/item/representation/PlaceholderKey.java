package nl.matsgemmeke.battlegrounds.item.representation;

import org.jetbrains.annotations.NotNull;

public enum PlaceholderKey {

    ITEM_NAME("item_name");

    @NotNull
    private final String key;

    PlaceholderKey(@NotNull String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return key;
    }
}
