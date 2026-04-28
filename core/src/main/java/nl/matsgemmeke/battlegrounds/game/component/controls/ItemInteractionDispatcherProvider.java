package nl.matsgemmeke.battlegrounds.game.component.controls;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.GunInteractionHandler;
import nl.matsgemmeke.battlegrounds.item.controls.Action;

import java.util.Arrays;

public class ItemInteractionDispatcherProvider implements Provider<ItemInteractionDispatcher> {

    private final GunInteractionHandler gunInteractionHandler;

    @Inject
    public ItemInteractionDispatcherProvider(GunInteractionHandler gunInteractionHandler) {
        this.gunInteractionHandler = gunInteractionHandler;
    }

    @Override
    public ItemInteractionDispatcher get() {
        ItemInteractionDispatcher dispatcher = new ItemInteractionDispatcher();

        Arrays.stream(Action.values()).forEach(action -> dispatcher.registerActionHandler(action, gunInteractionHandler));

        return dispatcher;
    }
}
