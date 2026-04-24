package nl.matsgemmeke.battlegrounds.game.component.controls;

public enum DispatchResult {

    /**
     * An item handler claimed the item/action combination. No further action needed.
     */
    HANDLED,
    /**
     * An item handler claimed the item/action combination and explicitly calls to cancel the action cause.
     */
    CANCELLED,
    /**
     * No handler claimed the item/action combination.
     */
    UNHANDLED
}
