package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.action.ActionExecutor;
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

    private ActionExecutorRegistry actionExecutorRegistry;

    @BeforeEach
    public void setUp() {
        actionExecutorRegistry = mock(ActionExecutorRegistry.class);
    }

    @Test
    public void performActionReturnsTrueWhenGivenItemStackIsAir() {
        ItemStack itemStack = new ItemStack(Material.AIR);

        ActionInvoker actionInvoker = new ActionInvoker(actionExecutorRegistry);
        boolean result = actionInvoker.performAction(itemStack, actionExecutor -> false);

        assertThat(result).isTrue();
    }

    @Test
    public void performActionReturnsTrueWhenNoActionExecutorIsFoundForGivenItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        when(actionExecutorRegistry.getActionExecutor(itemStack)).thenReturn(Optional.empty());

        ActionInvoker actionInvoker = new ActionInvoker(actionExecutorRegistry);
        boolean result = actionInvoker.performAction(itemStack, actionExecutor -> false);

        assertThat(result).isTrue();
    }

    @Test
    public void handleReturnsResultOfGivenFunction() {
        Player player = mock(Player.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        ActionExecutor actionExecutor = mock(ActionExecutor.class);
        when(actionExecutor.handleLeftClickAction(player, itemStack)).thenReturn(false);

        when(actionExecutorRegistry.getActionExecutor(itemStack)).thenReturn(Optional.of(actionExecutor));

        ActionInvoker actionInvoker = new ActionInvoker(actionExecutorRegistry);
        boolean result = actionInvoker.performAction(itemStack, a -> a.handleLeftClickAction(player, itemStack));

        assertThat(result).isFalse();
    }
}
