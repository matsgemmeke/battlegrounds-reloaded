package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.ItemRegister;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class EquipmentBehaviorTest {

    private Equipment equipment;
    private GamePlayer gamePlayer;
    private ItemRegister<Equipment, EquipmentHolder> register;
    private ItemStack itemStack;

    @Before
    public void setUp() {
        gamePlayer = mock(GamePlayer.class);
        register = new ItemRegister<>();
        itemStack = new ItemStack(Material.FLINT_AND_STEEL);

        equipment = mock(Equipment.class);
        when(equipment.isMatching(itemStack)).thenReturn(true);
    }

    @Test
    public void shouldCallFunctionOnEquipmentWhenLeftClicked() {
        when(equipment.getHolder()).thenReturn(gamePlayer);

        register.addAssignedItem(equipment, gamePlayer);

        EquipmentBehavior behavior = new EquipmentBehavior(register);
        boolean performAction = behavior.handleLeftClickAction(gamePlayer, itemStack);

        assertFalse(performAction);

        verify(equipment).onLeftClick();
    }

    @Test
    public void shouldDoNothingWhenLeftClickedButEquipmentIsNotRegistered() {
        EquipmentBehavior behavior = new EquipmentBehavior(register);
        boolean performAction = behavior.handleLeftClickAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(equipment, never()).onLeftClick();
    }

    @Test
    public void shouldDoNothingWhenLeftClickedButHolderDoesNotMatch() {
        register.addAssignedItem(equipment, gamePlayer);

        EquipmentBehavior behavior = new EquipmentBehavior(register);
        boolean performAction = behavior.handleLeftClickAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(equipment, never()).onLeftClick();
    }

    @Test
    public void shouldCallFunctionOnEquipmentWhenRightClicked() {
        when(equipment.getHolder()).thenReturn(gamePlayer);

        register.addAssignedItem(equipment, gamePlayer);

        EquipmentBehavior behavior = new EquipmentBehavior(register);
        boolean performAction = behavior.handleRightClickAction(gamePlayer, itemStack);

        assertFalse(performAction);

        verify(equipment).onRightClick();
    }

    @Test
    public void shouldDoNothingWhenRightClickedButEquipmentIsNotRegistered() {
        EquipmentBehavior behavior = new EquipmentBehavior(register);
        boolean performAction = behavior.handleRightClickAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(equipment, never()).onRightClick();
    }

    @Test
    public void shouldDoNothingWhenRightClickedButHolderDoesNotMatch() {
        register.addAssignedItem(equipment, gamePlayer);

        EquipmentBehavior behavior = new EquipmentBehavior(register);
        boolean performAction = behavior.handleRightClickAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(equipment, never()).onRightClick();
    }
}
