package nl.matsgemmeke.battlegrounds.game.component.controls;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.EquipmentInteractionHandler;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.GunInteractionHandler;
import nl.matsgemmeke.battlegrounds.item.controls.Action;

import java.util.Arrays;

public class ItemInteractionDispatcherProvider implements Provider<ItemInteractionDispatcher> {

    private final EquipmentInteractionHandler equipmentInteractionHandler;
    private final GunInteractionHandler gunInteractionHandler;

    @Inject
    public ItemInteractionDispatcherProvider(EquipmentInteractionHandler equipmentInteractionHandler, GunInteractionHandler gunInteractionHandler) {
        this.equipmentInteractionHandler = equipmentInteractionHandler;
        this.gunInteractionHandler = gunInteractionHandler;
    }

    @Override
    public ItemInteractionDispatcher get() {
        ItemInteractionDispatcher dispatcher = new ItemInteractionDispatcher();

        Arrays.stream(Action.values()).forEach(action -> dispatcher.registerActionHandler(action, equipmentInteractionHandler));
        Arrays.stream(Action.values()).forEach(action -> dispatcher.registerActionHandler(action, gunInteractionHandler));

        return dispatcher;
    }
}
