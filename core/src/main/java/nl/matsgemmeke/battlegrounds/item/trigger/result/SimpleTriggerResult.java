package nl.matsgemmeke.battlegrounds.item.trigger.result;

/**
 * A trigger result that does not carry any extra data.
 */
public class SimpleTriggerResult implements TriggerResult {

    public static final SimpleTriggerResult ACTIVATES = new SimpleTriggerResult(true);
    public static final SimpleTriggerResult NOT_ACTIVATES = new SimpleTriggerResult(false);

    private final boolean activates;

    private SimpleTriggerResult(boolean activates) {
        this.activates = activates;
    }

    @Override
    public boolean activates() {
        return activates;
    }
}
