package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.GunHolder;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
import nl.matsgemmeke.battlegrounds.item.ItemRegister;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultTrainingModeTest {

    private InternalsProvider internals;
    private ItemRegister<Equipment, EquipmentHolder> equipmentRegister;
    private ItemRegister<Gun, GunHolder> gunRegister;

    @Before
    public void setUp() {
        internals = mock(InternalsProvider.class);
        equipmentRegister = new ItemRegister<>();
        gunRegister = new ItemRegister<>();
    }

    @Test
    public void addingPlayersAddThemToPlayerList() {
        Player player = mock(Player.class);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentRegister, gunRegister);
        GamePlayer gamePlayer = trainingMode.addPlayer(player);

        assertNotNull(gamePlayer);
    }

    @Test
    public void shouldPassOnItemDropsToBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack itemStack = mock(ItemStack.class);

        ItemBehavior behavior1 = mock(ItemBehavior.class);
        when(behavior1.handleDropItemAction(gamePlayer, itemStack)).thenReturn(true);

        ItemBehavior behavior2 = mock(ItemBehavior.class);
        when(behavior2.handleDropItemAction(gamePlayer, itemStack)).thenReturn(false);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentRegister, gunRegister);
        trainingMode.addBehavior(behavior1);
        trainingMode.addBehavior(behavior2);
        boolean result = trainingMode.handleItemDrop(gamePlayer, itemStack);

        assertFalse(result);

        verify(behavior1).handleDropItemAction(gamePlayer, itemStack);
        verify(behavior2).handleDropItemAction(gamePlayer, itemStack);
    }

    @Test
    public void shouldPassOnLeftClicksToBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack itemStack = mock(ItemStack.class);

        ItemBehavior behavior1 = mock(ItemBehavior.class);
        when(behavior1.handleLeftClickAction(gamePlayer, itemStack)).thenReturn(false);

        ItemBehavior behavior2 = mock(ItemBehavior.class);
        when(behavior2.handleLeftClickAction(gamePlayer, itemStack)).thenReturn(true);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentRegister, gunRegister);
        trainingMode.addBehavior(behavior1);
        trainingMode.addBehavior(behavior2);
        boolean result = trainingMode.handleItemLeftClick(gamePlayer, itemStack);

        assertFalse(result);

        verify(behavior1).handleLeftClickAction(gamePlayer, itemStack);
        verify(behavior2).handleLeftClickAction(gamePlayer, itemStack);
    }

    @Test
    public void shouldPassOnRightClicksToBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack itemStack = mock(ItemStack.class);

        ItemBehavior behavior1 = mock(ItemBehavior.class);
        when(behavior1.handleRightClickAction(gamePlayer, itemStack)).thenReturn(true);

        ItemBehavior behavior2 = mock(ItemBehavior.class);
        when(behavior2.handleRightClickAction(gamePlayer, itemStack)).thenReturn(true);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentRegister, gunRegister);
        trainingMode.addBehavior(behavior1);
        trainingMode.addBehavior(behavior2);
        boolean result = trainingMode.handleItemRightClick(gamePlayer, itemStack);

        assertTrue(result);

        verify(behavior1).handleRightClickAction(gamePlayer, itemStack);
        verify(behavior2).handleRightClickAction(gamePlayer, itemStack);
    }

    @Test
    public void shouldPassChangeFromToBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack fromItem = mock(ItemStack.class);
        ItemStack toItem = mock(ItemStack.class);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItemInMainHand()).thenReturn(fromItem);

        Player player = mock(Player.class);
        when(player.getInventory()).thenReturn(inventory);

        ItemBehavior behavior = mock(ItemBehavior.class);
        when(behavior.handleChangeFromAction(gamePlayer, fromItem)).thenReturn(true);
        when(behavior.handleChangeToAction(gamePlayer, toItem)).thenReturn(false);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentRegister, gunRegister);
        trainingMode.addBehavior(behavior);
        boolean result = trainingMode.handleItemChange(gamePlayer, fromItem, toItem);

        assertFalse(result);

        verify(behavior).handleChangeFromAction(gamePlayer, fromItem);
    }

    @Test
    public void shouldPassItemChangeToBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack fromItem = mock(ItemStack.class);
        ItemStack toItem = mock(ItemStack.class);

        ItemBehavior behavior = mock(ItemBehavior.class);
        when(behavior.handleChangeFromAction(gamePlayer, toItem)).thenReturn(false);
        when(behavior.handleChangeToAction(gamePlayer, toItem)).thenReturn(true);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentRegister, gunRegister);
        trainingMode.addBehavior(behavior);
        boolean result = trainingMode.handleItemChange(gamePlayer, fromItem, toItem);

        assertFalse(result);

        verify(behavior).handleChangeFromAction(gamePlayer, fromItem);
        verify(behavior).handleChangeToAction(gamePlayer, toItem);
    }

    @Test
    public void shouldPassOnItemPickupsToBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack itemStack = mock(ItemStack.class);

        ItemBehavior behavior1 = mock(ItemBehavior.class);
        when(behavior1.handlePickupItemAction(gamePlayer, itemStack)).thenReturn(true);

        ItemBehavior behavior2 = mock(ItemBehavior.class);
        when(behavior2.handlePickupItemAction(gamePlayer, itemStack)).thenReturn(false);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentRegister, gunRegister);
        trainingMode.addBehavior(behavior1);
        trainingMode.addBehavior(behavior2);
        boolean result = trainingMode.handleItemPickup(gamePlayer, itemStack);

        assertFalse(result);

        verify(behavior1).handlePickupItemAction(gamePlayer, itemStack);
        verify(behavior2).handlePickupItemAction(gamePlayer, itemStack);
    }

    @Test
    public void shouldPassOnItemSwapsToBehaviorInstancesAndReturnResult() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack swapFrom = mock(ItemStack.class);
        ItemStack swapTo = mock(ItemStack.class);

        ItemBehavior behavior = mock(ItemBehavior.class);
        when(behavior.handleSwapFromAction(gamePlayer, swapFrom)).thenReturn(true);
        when(behavior.handleSwapToAction(gamePlayer, swapTo)).thenReturn(false);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentRegister, gunRegister);
        trainingMode.addBehavior(behavior);
        boolean result = trainingMode.handleItemSwap(gamePlayer, swapFrom, swapTo);

        assertFalse(result);
    }
}
