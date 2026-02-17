package nl.matsgemmeke.battlegrounds.item.representation;

public enum Placeholder {

    ITEM_NAME("name"),
    LOADED_AMOUNT("loaded_amount"),
    MAGAZINE_AMMO("magazine_ammo"),
    RESERVE_AMMO("reserve_ammo"),
    RESERVE_AMOUNT("reserve_amount");

    private final String key;

    Placeholder(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
