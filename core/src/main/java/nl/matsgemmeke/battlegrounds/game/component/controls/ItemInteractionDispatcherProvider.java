package nl.matsgemmeke.battlegrounds.game.component.controls;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.EquipmentInteractionHandler;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.GunInteractionHandler;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.MeleeWeaponInteractionHandler;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;

public class ItemInteractionDispatcherProvider implements Provider<ItemInteractionDispatcher> {

    private final EquipmentInteractionHandler equipmentInteractionHandler;
    private final GunInteractionHandler gunInteractionHandler;
    private final MeleeWeaponInteractionHandler meleeWeaponInteractionHandler;
    private final NamespacedKeyCreator namespacedKeyCreator;

    @Inject
    public ItemInteractionDispatcherProvider(
            EquipmentInteractionHandler equipmentInteractionHandler,
            GunInteractionHandler gunInteractionHandler,
            MeleeWeaponInteractionHandler meleeWeaponInteractionHandler,
            NamespacedKeyCreator namespacedKeyCreator
    ) {
        this.equipmentInteractionHandler = equipmentInteractionHandler;
        this.gunInteractionHandler = gunInteractionHandler;
        this.meleeWeaponInteractionHandler = meleeWeaponInteractionHandler;
        this.namespacedKeyCreator = namespacedKeyCreator;
    }

    @Override
    public ItemInteractionDispatcher get() {
        ItemInteractionDispatcher dispatcher = new ItemInteractionDispatcher(namespacedKeyCreator);
        dispatcher.registerInteractionHandler(WeaponType.EQUIPMENT, equipmentInteractionHandler);
        dispatcher.registerInteractionHandler(WeaponType.GUN, gunInteractionHandler);
        dispatcher.registerInteractionHandler(WeaponType.MELEE_WEAPON, meleeWeaponInteractionHandler);
        return dispatcher;
    }
}
