package nl.matsgemmeke.battlegrounds.item.controls;

import nl.matsgemmeke.battlegrounds.configuration.item.controls.ControlSpec;
import nl.matsgemmeke.battlegrounds.item.ItemUser;

public class ActionBindingMapper {

    public <T extends ItemUser> ActionBinding<T> toBinding(ControlSpec spec, Function<T> function) {
        int priority = spec.priority;
        boolean blocking = spec.blocking;
        boolean stopsChain = spec.stopsChain;
        boolean cancelsEvent = spec.cancelsEvent;

        return new ActionBinding<>(function, priority, blocking, stopsChain, cancelsEvent);
    }
}
