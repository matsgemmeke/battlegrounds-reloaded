package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.GunHolder;
import nl.matsgemmeke.battlegrounds.item.ItemBehavior;
import nl.matsgemmeke.battlegrounds.item.ItemRegister;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
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
    public void addingItemsReturnsNewGameItemInstance() {
        Item item = mock(Item.class);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentRegister, gunRegister);
        GameItem gameItem = trainingMode.addItem(item);

        assertNotNull(gameItem);
    }

    @Test
    public void addingPlayersReturnsNewGamePlayerInstance() {
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
        trainingMode.addItemBehavior(behavior1);
        trainingMode.addItemBehavior(behavior2);
        boolean result = trainingMode.handleItemDrop(gamePlayer, itemStack);

        assertFalse(result);

        verify(behavior1).handleDropItemAction(gamePlayer, itemStack);
        verify(behavior2).handleDropItemAction(gamePlayer, itemStack);
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
        trainingMode.addItemBehavior(behavior);
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
        trainingMode.addItemBehavior(behavior);
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
        trainingMode.addItemBehavior(behavior1);
        trainingMode.addItemBehavior(behavior2);
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
        trainingMode.addItemBehavior(behavior);
        boolean result = trainingMode.handleItemSwap(gamePlayer, swapFrom, swapTo);

        assertFalse(result);
    }

    @Test
    public void shouldReturnMatchingEntityInAnyRegisterWhenGettingGameEntity() {
        Player player = mock(Player.class);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentRegister, gunRegister);
        trainingMode.addPlayer(player);

        GameEntity result = trainingMode.getGameEntity(player);

        assertNotNull(result);
        assertEquals(player, result.getEntity());
    }

    @Test
    public void shouldReturnNullIfNoneOfTheRegistersContainsTheCorrespondingEntityWrapper() {
        Player player = mock(Player.class);
        Player otherPlayer = mock(Player.class);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentRegister, gunRegister);
        trainingMode.addPlayer(otherPlayer);

        GameEntity result = trainingMode.getGameEntity(player);

        assertNull(result);
    }

    @Test
    public void shouldReturnCorrespondingPlayerWrapperWhenGettingPlayer() {
        Player player = mock(Player.class);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentRegister, gunRegister);
        trainingMode.addPlayer(player);

        GamePlayer result = trainingMode.getGamePlayer(player);

        assertNotNull(result);
        assertEquals(player, result.getEntity());
    }

    @Test
    public void shouldReturnWhetherTheInstanceHasRegisteredEntityWrapper() {
        Player player = mock(Player.class);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentRegister, gunRegister);
        trainingMode.addPlayer(player);

        boolean hasEntity = trainingMode.hasEntity(player);

        assertTrue(hasEntity);
    }

    @Test
    public void shouldReturnWhetherTheInstanceHasRegisteredPlayerWrapper() {
        Player player = mock(Player.class);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals, equipmentRegister, gunRegister);
        trainingMode.addPlayer(player);

        boolean hasPlayer = trainingMode.hasPlayer(player);

        assertTrue(hasPlayer);
    }
}
