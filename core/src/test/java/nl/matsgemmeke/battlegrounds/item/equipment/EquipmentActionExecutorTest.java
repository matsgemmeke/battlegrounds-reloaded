package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.ItemContainer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class EquipmentActionExecutorTest {

    private Equipment equipment;
    private GamePlayer gamePlayer;
    private ItemContainer<Equipment, EquipmentHolder> equipmentContainer;
    private ItemStack itemStack;

    @BeforeEach
    public void setUp() {
        gamePlayer = mock(GamePlayer.class);
        equipmentContainer = new ItemContainer<>();
        itemStack = new ItemStack(Material.SHEARS);

        equipment = mock(Equipment.class);
        when(equipment.isMatching(itemStack)).thenReturn(true);
    }

    @Test
    public void shouldCallFunctionOnEquipmentWhenLeftClicked() {
        when(equipment.getHolder()).thenReturn(gamePlayer);

        equipmentContainer.addAssignedItem(equipment, gamePlayer);

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentContainer);
        boolean performAction = actionExecutor.handleLeftClickAction(gamePlayer, itemStack);

        assertFalse(performAction);

        verify(equipment).onLeftClick();
    }

    @Test
    public void shouldDoNothingWhenLeftClickedButEquipmentIsNotRegistered() {
        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentContainer);
        boolean performAction = actionExecutor.handleLeftClickAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(equipment, never()).onLeftClick();
    }

    @Test
    public void shouldDoNothingWhenLeftClickedButHolderDoesNotMatch() {
        equipmentContainer.addAssignedItem(equipment, gamePlayer);

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentContainer);
        boolean performAction = actionExecutor.handleLeftClickAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(equipment, never()).onLeftClick();
    }

    @Test
    public void shouldCallFunctionOnEquipmentWhenRightClicked() {
        when(equipment.getHolder()).thenReturn(gamePlayer);

        equipmentContainer.addAssignedItem(equipment, gamePlayer);

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentContainer);
        boolean performAction = actionExecutor.handleRightClickAction(gamePlayer, itemStack);

        assertFalse(performAction);

        verify(equipment).onRightClick();
    }

    @Test
    public void shouldDoNothingWhenRightClickedButEquipmentIsNotRegistered() {
        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentContainer);
        boolean performAction = actionExecutor.handleRightClickAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(equipment, never()).onRightClick();
    }

    @Test
    public void shouldDoNothingWhenRightClickedButHolderDoesNotMatch() {
        equipmentContainer.addAssignedItem(equipment, gamePlayer);

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentContainer);
        boolean performAction = actionExecutor.handleRightClickAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(equipment, never()).onRightClick();
    }
}
