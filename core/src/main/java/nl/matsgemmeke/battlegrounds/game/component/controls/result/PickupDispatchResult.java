package nl.matsgemmeke.battlegrounds.game.component.controls.result;

public record PickupDispatchResult(boolean dispatched, boolean cancelEvent, boolean removeItem) {

    public static PickupDispatchResult handled() {
        return new PickupDispatchResult(true, false, false);
    }

    public static PickupDispatchResult unhandled() {
        return new PickupDispatchResult(false, false, false);
    }

    public static PickupDispatchResult cancelPickup() {
        return new PickupDispatchResult(true, true, true);
    }
}
