package nl.matsgemmeke.battlegrounds.event.action;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.component.item.ActionExecutorRegistry;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ActionInvoker {

    @NotNull
    private final Provider<ActionExecutorRegistry> actionExecutorRegistryProvider;

    @Inject
    public ActionInvoker(@NotNull Provider<ActionExecutorRegistry> actionExecutorRegistryProvider) {
        this.actionExecutorRegistryProvider = actionExecutorRegistryProvider;
    }

    public boolean performAction(ItemStack itemStack, Function<ActionExecutor, Boolean> actionExecutorFunction) {
        if (itemStack.getType().isAir()) {
            return true;
        }

        ActionExecutorRegistry actionExecutorRegistry = actionExecutorRegistryProvider.get();
        ActionExecutor actionExecutor = actionExecutorRegistry.getActionExecutor(itemStack).orElse(null);

        if (actionExecutor == null) {
            return true;
        }

        return actionExecutorFunction.apply(actionExecutor);
    }
}
