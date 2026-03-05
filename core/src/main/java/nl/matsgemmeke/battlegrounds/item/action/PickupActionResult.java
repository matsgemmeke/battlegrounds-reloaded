package nl.matsgemmeke.battlegrounds.item.action;

import org.bukkit.entity.Item;

import java.util.function.Consumer;

public record PickupActionResult(boolean performAction, Consumer<Item> itemAction) {

    private static final Consumer<Item> NO_OP = item -> {};

    public PickupActionResult(boolean performAction) {
        this(performAction, NO_OP);
    }
}
