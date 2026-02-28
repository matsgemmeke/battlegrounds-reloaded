package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.action.ActionExecutor;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActionExecutorRegistryTest {

    private static final String ACTION_EXECUTOR_ID = "gun";
    private static final String ACTION_EXECUTOR_ID_KEY = "action-executor-id";

    private NamespacedKeyCreator namespacedKeyCreator;
    private Plugin plugin;

    @BeforeEach
    public void setUp() {
        namespacedKeyCreator = mock(NamespacedKeyCreator.class);

        plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("Battlegrounds");
    }

    @Test
    public void getActionExecutorReturnsEmptyOptionalWhenGivenItemStackHasNoItemMeta() {
        ItemStack itemStack = mock(ItemStack.class);
        when(itemStack.getItemMeta()).thenReturn(null);

        ActionExecutorRegistry actionExecutorRegistry = new ActionExecutorRegistry(namespacedKeyCreator);
        Optional<ActionExecutor> actionExecutorOptional = actionExecutorRegistry.getActionExecutor(itemStack);

        assertThat(actionExecutorOptional).isEmpty();
    }

    @Test
    public void getActionExecutorReturnsEmptyOptionalWhenGivenItemStackHasNoDataValueForActionExecutorId() {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, ACTION_EXECUTOR_ID_KEY);
        when(namespacedKeyCreator.create(ACTION_EXECUTOR_ID_KEY)).thenReturn(namespacedKey);

        ItemStack itemStack = mock(ItemStack.class, Mockito.RETURNS_DEEP_STUBS);
        when(itemStack.getItemMeta().getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING)).thenReturn(null);

        ActionExecutorRegistry actionExecutorRegistry = new ActionExecutorRegistry(namespacedKeyCreator);
        Optional<ActionExecutor> actionExecutorOptional = actionExecutorRegistry.getActionExecutor(itemStack);

        assertThat(actionExecutorOptional).isEmpty();
    }

    @Test
    public void getActionExecutorReturnsOptionalContainingCorrespondingActionExecutorToGivenItemStackDataValue() {
        ActionExecutor actionExecutor = mock(ActionExecutor.class);

        NamespacedKey namespacedKey = new NamespacedKey(plugin, ACTION_EXECUTOR_ID_KEY);
        when(namespacedKeyCreator.create(ACTION_EXECUTOR_ID_KEY)).thenReturn(namespacedKey);

        ItemStack itemStack = mock(ItemStack.class, Mockito.RETURNS_DEEP_STUBS);
        when(itemStack.getItemMeta().getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING)).thenReturn(ACTION_EXECUTOR_ID);

        ActionExecutorRegistry actionExecutorRegistry = new ActionExecutorRegistry(namespacedKeyCreator);
        actionExecutorRegistry.register(ACTION_EXECUTOR_ID, actionExecutor);
        Optional<ActionExecutor> actionExecutorOptional = actionExecutorRegistry.getActionExecutor(itemStack);

        assertThat(actionExecutorOptional).hasValue(actionExecutor);
    }
}
