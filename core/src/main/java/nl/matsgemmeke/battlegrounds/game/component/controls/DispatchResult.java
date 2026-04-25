package nl.matsgemmeke.battlegrounds.game.component.controls;

public record DispatchResult(boolean handled, boolean cancelEvent) {

    public static DispatchResult unhandled() {
        return new DispatchResult(false, false);
    }
}
