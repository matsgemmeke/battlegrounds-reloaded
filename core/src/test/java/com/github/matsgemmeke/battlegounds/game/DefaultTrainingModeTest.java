package com.github.matsgemmeke.battlegounds.game;

import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.item.Item;
import com.github.matsgemmeke.battlegrounds.game.DefaultTrainingMode;
import com.github.matsgemmeke.battlegrounds.game.DefaultTrainingModeContext;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultTrainingModeTest {

    private DefaultTrainingModeContext context;

    @Before
    public void setUp() {
        this.context = mock(DefaultTrainingModeContext.class);
    }

    @Test
    public void addingPlayersAddThemToPlayerList() {
        Player player = mock(Player.class);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode();
        GamePlayer gamePlayer = trainingMode.addPlayer(player);

        assertNotNull(gamePlayer);
    }

    @Test
    public void executesLeftClickMethodIfActionIsLeftClick() {
        Item item = mock(Item.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        PlayerInteractEvent event = new PlayerInteractEvent(null, Action.LEFT_CLICK_AIR, itemStack, null, null);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getItem(itemStack)).thenReturn(item);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode();
        boolean result = trainingMode.onInteract(gamePlayer, event);

        verify(item).onLeftClick(gamePlayer);

        assertTrue(result);
        assertTrue(event.isCancelled());
    }

    @Test
    public void executesRightClickMethodIfActionIsRightClick() {
        Item item = mock(Item.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        PlayerInteractEvent event = new PlayerInteractEvent(null, Action.RIGHT_CLICK_AIR, itemStack, null, null);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getItem(itemStack)).thenReturn(item);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode();
        boolean result = trainingMode.onInteract(gamePlayer, event);

        verify(item).onRightClick(gamePlayer);

        assertTrue(result);
        assertTrue(event.isCancelled());
    }

    @Test
    public void doesNothingWithInteractionWithoutItemStack() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        PlayerInteractEvent event = new PlayerInteractEvent(null, null, null, null, null);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode();
        boolean result = trainingMode.onInteract(gamePlayer, event);

        assertFalse(result);
    }

    @Test
    public void doesNothingWithInteractionWhenItemDoesNotExist() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        PlayerInteractEvent event = new PlayerInteractEvent(null, null, itemStack, null, null);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode();
        boolean result = trainingMode.onInteract(gamePlayer, event);

        assertFalse(result);
    }

    @Test
    public void doesNothingIfDroppedItemIsNotInList() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        org.bukkit.entity.Item itemEntity = mock(org.bukkit.entity.Item.class);
        ItemStack itemStack = mock(ItemStack.class);
        Player player = mock(Player.class);

        when(itemEntity.getItemStack()).thenReturn(itemStack);

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, itemEntity);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode();
        boolean accepted = trainingMode.onItemDrop(gamePlayer, event);

        assertFalse(accepted);
    }

    @Test
    public void addsItemToListAndCallsMethod() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        Item item = mock(Item.class);
        org.bukkit.entity.Item itemEntity = mock(org.bukkit.entity.Item.class);
        ItemStack itemStack = mock(ItemStack.class);
        Player player = mock(Player.class);

        when(gamePlayer.getItem(itemStack)).thenReturn(item);
        when(itemEntity.getItemStack()).thenReturn(itemStack);

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, itemEntity);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode();
        boolean accepted = trainingMode.onItemDrop(gamePlayer, event);

        verify(item, times(1)).onDrop(gamePlayer);

        assertTrue(accepted);
    }

    @Test
    public void doesNothingIfChangedItemIsItem() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack itemStack = mock(ItemStack.class);
        Player player = mock(Player.class);
        PlayerInventory inventory = mock(PlayerInventory.class);

        when(gamePlayer.getEntity()).thenReturn(player);
        when(inventory.getItemInMainHand()).thenReturn(itemStack);
        when(player.getInventory()).thenReturn(inventory);

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode();
        boolean accepted = trainingMode.onItemHeld(gamePlayer, event);

        assertFalse(accepted);
    }

    @Test
    public void callsMethodWhenChangingItemThatHasItemInstance() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        Item item = mock(Item.class);
        ItemStack itemStack = mock(ItemStack.class);
        Player player = mock(Player.class);
        PlayerInventory inventory = mock(PlayerInventory.class);

        when(gamePlayer.getItem(itemStack)).thenReturn(item);
        when(gamePlayer.getEntity()).thenReturn(player);
        when(inventory.getItemInMainHand()).thenReturn(itemStack);
        when(player.getInventory()).thenReturn(inventory);

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode();
        boolean accepted = trainingMode.onItemHeld(gamePlayer, event);

        verify(item, times(1)).onChangeHeldItem(gamePlayer);

        assertTrue(accepted);
    }

    @Test
    public void doNothingIfPickupItemHasNoItemInstance() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        Item item = mock(Item.class);
        org.bukkit.entity.Item itemEntity = mock(org.bukkit.entity.Item.class);
        ItemStack itemStack = mock(ItemStack.class);
        Player player = mock(Player.class);

        when(gamePlayer.getItem(itemStack)).thenReturn(item);
        when(itemEntity.getItemStack()).thenReturn(itemStack);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, itemEntity, 0);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode();
        // First call onDropItem to add the item to the dropped items list
        trainingMode.onItemDrop(gamePlayer, new PlayerDropItemEvent(player, itemEntity));

        boolean accepted = trainingMode.onPickupItem(gamePlayer, event);

        assertFalse(accepted);
    }

    @Test
    public void callsMethodWhenPickingUpItemThatHasItemInstance() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        Item item = mock(Item.class);
        org.bukkit.entity.Item itemEntity = mock(org.bukkit.entity.Item.class);
        ItemStack itemStack = mock(ItemStack.class);
        Player player = mock(Player.class);

        when(gamePlayer.getItem(itemStack)).thenReturn(item);
        when(item.getItemStack()).thenReturn(itemStack);
        when(itemEntity.getItemStack()).thenReturn(itemStack);
        when(itemStack.isSimilar(itemStack)).thenReturn(true);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, itemEntity, 0);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode();
        // First call onDropItem to add the item to the dropped items list
        trainingMode.onItemDrop(gamePlayer, new PlayerDropItemEvent(player, itemEntity));

        boolean accepted = trainingMode.onPickupItem(gamePlayer, event);

        verify(gamePlayer, times(1)).addItem(item);

        assertTrue(accepted);
    }
}
