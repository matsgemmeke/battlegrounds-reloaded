package nl.matsgemmeke.battlegrounds.event.action;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.component.item.ActionExecutorRegistry;
import nl.matsgemmeke.battlegrounds.item.ActionExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActionInvokerTest {

    private Provider<ActionExecutorRegistry> actionExecutorRegistryProvider;

    @BeforeEach
    public void setUp() {
        actionExecutorRegistryProvider = mock();
    }

    @Test
    public void performActionReturnsTrueWhenGivenItemStackIsAir() {
        ItemStack itemStack = new ItemStack(Material.AIR);

        ActionInvoker actionInvoker = new ActionInvoker(actionExecutorRegistryProvider);
        boolean result = actionInvoker.performAction(itemStack, actionExecutor -> false);

        assertThat(result).isTrue();
    }

    @Test
    public void performActionReturnsTrueWhenNoActionExecutorIsFoundForGivenItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        ActionExecutorRegistry actionExecutorRegistry = mock(ActionExecutorRegistry.class);
        when(actionExecutorRegistry.getActionExecutor(itemStack)).thenReturn(Optional.empty());

        when(actionExecutorRegistryProvider.get()).thenReturn(actionExecutorRegistry);

        ActionInvoker actionInvoker = new ActionInvoker(actionExecutorRegistryProvider);
        boolean result = actionInvoker.performAction(itemStack, actionExecutor -> false);

        assertThat(result).isTrue();
    }

    @Test
    public void handleReturnsResultOfGivenFunction() {
        Player player = mock(Player.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        ActionExecutor actionExecutor = mock(ActionExecutor.class);
        when(actionExecutor.handleLeftClickAction(player, itemStack)).thenReturn(false);

        ActionExecutorRegistry actionExecutorRegistry = mock(ActionExecutorRegistry.class);
        when(actionExecutorRegistry.getActionExecutor(itemStack)).thenReturn(Optional.of(actionExecutor));

        when(actionExecutorRegistryProvider.get()).thenReturn(actionExecutorRegistry);

        ActionInvoker actionInvoker = new ActionInvoker(actionExecutorRegistryProvider);
        boolean result = actionInvoker.performAction(itemStack, a -> a.handleLeftClickAction(player, itemStack));

        assertThat(result).isFalse();
    }
}
