package nl.matsgemmeke.battlegrounds.item.controls;

import nl.matsgemmeke.battlegrounds.item.ItemUser;

public record ActionBinding<T extends ItemUser>(
        Function<T> function,
        int priority,
        boolean blocking,
        boolean stopsChain,
        boolean cancelsEvent
) {
}
