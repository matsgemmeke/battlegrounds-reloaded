package nl.matsgemmeke.battlegrounds.item.representation;

import org.jetbrains.annotations.NotNull;

public enum Placeholder {

    ITEM_NAME("name"),
    MAGAZINE_AMMO("magazine_ammo"),
    RESERVE_AMMO("reserve_ammo");

    @NotNull
    private final String key;

    Placeholder(@NotNull String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return key;
    }
}
