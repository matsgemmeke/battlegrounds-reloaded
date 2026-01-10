package nl.matsgemmeke.battlegrounds.game.component.item;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ActionExecutorRegistry {

    private static final String ACTION_EXECUTOR_ID_KEY = "action-executor-id";

    private final Map<String, ActionExecutor> actionExecutors;
    private final NamespacedKeyCreator namespacedKeyCreator;

    @Inject
    public ActionExecutorRegistry(NamespacedKeyCreator namespacedKeyCreator) {
        this.namespacedKeyCreator = namespacedKeyCreator;
        this.actionExecutors = new HashMap<>();
    }

    public void register(String actionExecutorId, ActionExecutor executor) {
        actionExecutors.put(actionExecutorId, executor);
    }

    public Optional<ActionExecutor> getActionExecutor(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return Optional.empty();
        }

        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        NamespacedKey key = namespacedKeyCreator.create(ACTION_EXECUTOR_ID_KEY);
        String actionExecutorId = data.get(key, PersistentDataType.STRING);

        if (actionExecutorId == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(actionExecutors.get(actionExecutorId));
    }
}
