package nl.matsgemmeke.battlegrounds.game.component.item;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ActionInvoker {

    @NotNull
    private final ActionExecutorRegistry actionExecutorRegistry;

    @Inject
    public ActionInvoker(@NotNull ActionExecutorRegistry actionExecutorRegistry) {
        this.actionExecutorRegistry = actionExecutorRegistry;
    }

    public boolean performAction(ItemStack itemStack, Function<ActionExecutor, Boolean> actionExecutorFunction) {
        if (itemStack.getType().isAir()) {
            return true;
        }

        ActionExecutor actionExecutor = actionExecutorRegistry.getActionExecutor(itemStack).orElse(null);

        if (actionExecutor == null) {
            return true;
        }

        return actionExecutorFunction.apply(actionExecutor);
    }
}
