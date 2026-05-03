package nl.matsgemmeke.battlegrounds.game.component.controls;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.EquipmentInteractionHandler;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.GunInteractionHandler;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.MeleeWeaponInteractionHandler;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.MeleeWeaponPickupHandler;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;

import java.util.Arrays;

public class ItemInteractionDispatcherProvider implements Provider<ItemInteractionDispatcher> {

    private final EquipmentInteractionHandler equipmentInteractionHandler;
    private final GunInteractionHandler gunInteractionHandler;
    private final MeleeWeaponInteractionHandler meleeWeaponInteractionHandler;
    private final MeleeWeaponPickupHandler meleeWeaponPickupHandler;
    private final NamespacedKeyCreator namespacedKeyCreator;

    @Inject
    public ItemInteractionDispatcherProvider(
            EquipmentInteractionHandler equipmentInteractionHandler,
            GunInteractionHandler gunInteractionHandler,
            MeleeWeaponInteractionHandler meleeWeaponInteractionHandler,
            MeleeWeaponPickupHandler meleeWeaponPickupHandler,
            NamespacedKeyCreator namespacedKeyCreator
    ) {
        this.equipmentInteractionHandler = equipmentInteractionHandler;
        this.gunInteractionHandler = gunInteractionHandler;
        this.meleeWeaponInteractionHandler = meleeWeaponInteractionHandler;
        this.meleeWeaponPickupHandler = meleeWeaponPickupHandler;
        this.namespacedKeyCreator = namespacedKeyCreator;
    }

    @Override
    public ItemInteractionDispatcher get() {
        ItemInteractionDispatcher dispatcher = new ItemInteractionDispatcher(namespacedKeyCreator);

        Arrays.stream(Action.values()).forEach(action -> dispatcher.registerInteractionHandler(action, WeaponType.EQUIPMENT, equipmentInteractionHandler));
        Arrays.stream(Action.values()).forEach(action -> dispatcher.registerInteractionHandler(action, WeaponType.GUN, gunInteractionHandler));

        dispatcher.registerInteractionHandler(Action.CHANGE_FROM, WeaponType.MELEE_WEAPON, meleeWeaponInteractionHandler);
        dispatcher.registerInteractionHandler(Action.CHANGE_TO, WeaponType.MELEE_WEAPON, meleeWeaponInteractionHandler);
        dispatcher.registerInteractionHandler(Action.DROP_ITEM, WeaponType.MELEE_WEAPON, meleeWeaponInteractionHandler);
        dispatcher.registerInteractionHandler(Action.LEFT_CLICK, WeaponType.MELEE_WEAPON, meleeWeaponInteractionHandler);
        dispatcher.registerInteractionHandler(Action.PICKUP_ITEM, WeaponType.MELEE_WEAPON, meleeWeaponPickupHandler);
        dispatcher.registerInteractionHandler(Action.RIGHT_CLICK, WeaponType.MELEE_WEAPON, meleeWeaponInteractionHandler);
        dispatcher.registerInteractionHandler(Action.SWAP_FROM, WeaponType.MELEE_WEAPON, meleeWeaponInteractionHandler);
        dispatcher.registerInteractionHandler(Action.SWAP_TO, WeaponType.MELEE_WEAPON, meleeWeaponInteractionHandler);

        return dispatcher;
    }
}
