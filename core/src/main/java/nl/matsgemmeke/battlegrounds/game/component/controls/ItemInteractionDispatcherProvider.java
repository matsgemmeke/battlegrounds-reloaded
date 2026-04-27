package nl.matsgemmeke.battlegrounds.game.component.controls;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.GunActionHandler;
import nl.matsgemmeke.battlegrounds.item.controls.Action;

import java.util.Arrays;

public class ItemInteractionDispatcherProvider implements Provider<ItemInteractionDispatcher> {

    private final GunActionHandler gunActionHandler;

    @Inject
    public ItemInteractionDispatcherProvider(GunActionHandler gunActionHandler) {
        this.gunActionHandler = gunActionHandler;
    }

    @Override
    public ItemInteractionDispatcher get() {
        ItemInteractionDispatcher dispatcher = new ItemInteractionDispatcher();

        Arrays.stream(Action.values()).forEach(action -> dispatcher.registerActionHandler(action, gunActionHandler));

        return dispatcher;
    }
}
