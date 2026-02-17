package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;

public class UnregisteredTriggerResult implements TriggerResult {

    @Override
    public boolean activates() {
        return false;
    }
}
