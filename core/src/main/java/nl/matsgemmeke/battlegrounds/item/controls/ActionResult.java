package nl.matsgemmeke.battlegrounds.item.controls;

public record ActionResult(boolean performed, boolean cancelEvent) {

    public static ActionResult ignore() {
        return new ActionResult(false, false);
    }
}