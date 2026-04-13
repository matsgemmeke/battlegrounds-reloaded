package nl.matsgemmeke.battlegrounds.item.controls;

public enum FunctionResult {

    /**
     * Function could not perform.
     */
    DENIED,
    /**
     * Performed, no side effects needed.
     */
    SUCCESS,
    /**
     * Performed, but suppress the originating trigger
     */
    CANCELLED
}
