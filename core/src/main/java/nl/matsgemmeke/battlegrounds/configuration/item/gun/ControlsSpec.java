package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class ControlsSpec {

    @Required
    @EnumValue(ActionValue.class)
    public String reload;
    @Required
    @EnumValue(ActionValue.class)
    public String shoot;
    @EnumValue(ActionValue.class)
    public String scopeUse;
    @EnumValue(ActionValue.class)
    public String scopeStop;
    @EnumValue(ActionValue.class)
    public String changeScopeMagnification;

    private enum ActionValue {
        CHANGE_FROM, CHANGE_TO, DROP_ITEM, LEFT_CLICK, PICKUP_ITEM, RIGHT_CLICK, SWAP_FROM, SWAP_TO
    }
}
