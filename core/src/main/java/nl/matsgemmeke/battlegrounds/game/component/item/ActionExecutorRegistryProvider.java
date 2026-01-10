package nl.matsgemmeke.battlegrounds.game.component.item;

import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.inject.Named;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;

public class ActionExecutorRegistryProvider implements Provider<ActionExecutorRegistry> {

    private static final String EQUIPMENT_ACTION_EXECUTOR_ID = "equipment";
    private static final String GUN_ACTION_EXECUTOR_ID = "gun";
    private static final String MELEE_WEAPON_ACTION_EXECUTOR_ID = "melee-weapon";

    private final ActionExecutor equipmentActionExecutor;
    private final ActionExecutor gunActionExecutor;
    private final ActionExecutor meleeWeaponActionExecutor;
    private final NamespacedKeyCreator namespacedKeyCreator;

    @Inject
    public ActionExecutorRegistryProvider(
            NamespacedKeyCreator namespacedKeyCreator,
            @Named("Equipment") ActionExecutor equipmentActionExecutor,
            @Named("Gun") ActionExecutor gunActionExecutor,
            @Named("MeleeWeapon") ActionExecutor meleeWeaponActionExecutor
    ) {
        this.equipmentActionExecutor = equipmentActionExecutor;
        this.gunActionExecutor = gunActionExecutor;
        this.meleeWeaponActionExecutor = meleeWeaponActionExecutor;
        this.namespacedKeyCreator = namespacedKeyCreator;
    }

    public ActionExecutorRegistry get() {
        ActionExecutorRegistry actionExecutorRegistry = new ActionExecutorRegistry(namespacedKeyCreator);
        actionExecutorRegistry.register(EQUIPMENT_ACTION_EXECUTOR_ID, equipmentActionExecutor);
        actionExecutorRegistry.register(GUN_ACTION_EXECUTOR_ID, gunActionExecutor);
        actionExecutorRegistry.register(MELEE_WEAPON_ACTION_EXECUTOR_ID, meleeWeaponActionExecutor);

        return actionExecutorRegistry;
    }
}
