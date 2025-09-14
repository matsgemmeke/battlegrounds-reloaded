package nl.matsgemmeke.battlegrounds.game.component.item;

import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.inject.Named;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.jetbrains.annotations.NotNull;

public class ActionExecutorRegistryProvider implements Provider<ActionExecutorRegistry> {

    private static final String EQUIPMENT_ACTION_EXECUTOR_ID = "equipment";
    private static final String GUN_ACTION_EXECUTOR_ID = "gun";

    @NotNull
    private final ActionExecutor equipmentActionExecutor;
    @NotNull
    private final ActionExecutor gunActionExecutor;
    @NotNull
    private final NamespacedKeyCreator namespacedKeyCreator;

    @Inject
    public ActionExecutorRegistryProvider(
            @NotNull NamespacedKeyCreator namespacedKeyCreator,
            @Named("Equipment") @NotNull ActionExecutor equipmentActionExecutor,
            @Named("Gun") @NotNull ActionExecutor gunActionExecutor
    ) {
        this.equipmentActionExecutor = equipmentActionExecutor;
        this.gunActionExecutor = gunActionExecutor;
        this.namespacedKeyCreator = namespacedKeyCreator;
    }

    public ActionExecutorRegistry get() {
        ActionExecutorRegistry actionExecutorRegistry = new ActionExecutorRegistry(namespacedKeyCreator);
        actionExecutorRegistry.register(EQUIPMENT_ACTION_EXECUTOR_ID, equipmentActionExecutor);
        actionExecutorRegistry.register(GUN_ACTION_EXECUTOR_ID, gunActionExecutor);

        return actionExecutorRegistry;
    }
}
