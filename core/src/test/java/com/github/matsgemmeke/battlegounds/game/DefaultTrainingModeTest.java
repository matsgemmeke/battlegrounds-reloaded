package com.github.matsgemmeke.battlegounds.game;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleEntity;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.item.BattleItem;
import com.github.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import com.github.matsgemmeke.battlegrounds.game.DefaultTrainingMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultTrainingModeTest {

    private BlockCollisionChecker collisionChecker;

    @Before
    public void setUp() {
        this.collisionChecker = mock(BlockCollisionChecker.class);
    }

    @Test
    public void addingPlayersAddThemToPlayerList() {
        Player player = mock(Player.class);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(collisionChecker);
        BattlePlayer battlePlayer = trainingMode.addPlayer(player);

        assertNotNull(battlePlayer);
    }

    @Test
    public void executesLeftClickMethodIfActionIsLeftClick() {
        BattleItem battleItem = mock(BattleItem.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        PlayerInteractEvent event = new PlayerInteractEvent(null, Action.LEFT_CLICK_AIR, itemStack, null, null);

        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        when(battlePlayer.getBattleItem(itemStack)).thenReturn(battleItem);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(collisionChecker);
        boolean result = trainingMode.onInteract(battlePlayer, event);

        verify(battleItem).onLeftClick(battlePlayer);

        assertTrue(result);
        assertTrue(event.isCancelled());
    }

    @Test
    public void executesRightClickMethodIfActionIsRightClick() {
        BattleItem battleItem = mock(BattleItem.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        PlayerInteractEvent event = new PlayerInteractEvent(null, Action.RIGHT_CLICK_AIR, itemStack, null, null);

        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        when(battlePlayer.getBattleItem(itemStack)).thenReturn(battleItem);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(collisionChecker);
        boolean result = trainingMode.onInteract(battlePlayer, event);

        verify(battleItem).onRightClick(battlePlayer);

        assertTrue(result);
        assertTrue(event.isCancelled());
    }

    @Test
    public void doesNothingWithInteractionWithoutItemStack() {
        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        PlayerInteractEvent event = new PlayerInteractEvent(null, null, null, null, null);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(collisionChecker);
        boolean result = trainingMode.onInteract(battlePlayer, event);

        assertFalse(result);
    }

    @Test
    public void doesNothingWithInteractionWhenBattleItemDoesNotExist() {
        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        PlayerInteractEvent event = new PlayerInteractEvent(null, null, itemStack, null, null);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(collisionChecker);
        boolean result = trainingMode.onInteract(battlePlayer, event);

        assertFalse(result);
    }

    @Test
    public void returnsNoTargetsIfGivenLocationHasNoWorld() {
        BattleEntity entity = mock(BattleEntity.class);
        Location location = new Location(null, 1.0, 1.0, 1.0);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(collisionChecker);
        Collection<BattleEntity> targets = trainingMode.getTargets(entity, location, 0.1);

        assertEquals(0, targets.size());
    }

    @Test
    public void returnsListOfTargetsBasedOnNearbyEntities() {
        double range = 0.1;

        BattleEntity entity = mock(BattleEntity.class);
        World world = mock(World.class);

        List<Entity> nearbyEntities = Collections.singletonList(mock(Player.class));
        Location location = new Location(world, 1.0, 1.0, 1.0);

        when(world.getNearbyEntities(location, range, range, range)).thenReturn(nearbyEntities);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(collisionChecker);
        Collection<BattleEntity> targets = trainingMode.getTargets(entity, location, range);

        assertEquals(1, targets.size());
    }

    @Test
    public void doesNothingIfDroppedItemIsNotInList() {
        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        Item item = mock(Item.class);
        ItemStack itemStack = mock(ItemStack.class);
        Player player = mock(Player.class);

        when(item.getItemStack()).thenReturn(itemStack);

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, item);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(collisionChecker);
        boolean accepted = trainingMode.onItemDrop(battlePlayer, event);

        assertFalse(accepted);
    }

    @Test
    public void addsItemToListAndCallsMethod() {
        BattleItem battleItem = mock(BattleItem.class);
        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        Item item = mock(Item.class);
        ItemStack itemStack = mock(ItemStack.class);
        Player player = mock(Player.class);

        when(battlePlayer.getBattleItem(itemStack)).thenReturn(battleItem);
        when(item.getItemStack()).thenReturn(itemStack);

        PlayerDropItemEvent event = new PlayerDropItemEvent(player, item);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(collisionChecker);
        boolean accepted = trainingMode.onItemDrop(battlePlayer, event);

        verify(battleItem, times(1)).onDrop(battlePlayer);

        assertTrue(accepted);
    }

    @Test
    public void doesNothingIfChangedItemIsNoBattleItem() {
        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        ItemStack itemStack = mock(ItemStack.class);
        Player player = mock(Player.class);
        PlayerInventory inventory = mock(PlayerInventory.class);

        when(battlePlayer.getEntity()).thenReturn(player);
        when(inventory.getItemInMainHand()).thenReturn(itemStack);
        when(player.getInventory()).thenReturn(inventory);

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(collisionChecker);
        boolean accepted = trainingMode.onItemHeld(battlePlayer, event);

        assertFalse(accepted);
    }

    @Test
    public void callsMethodWhenChangingItemThatHasBattleItemInstance() {
        BattleItem battleItem = mock(BattleItem.class);
        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        ItemStack itemStack = mock(ItemStack.class);
        Player player = mock(Player.class);
        PlayerInventory inventory = mock(PlayerInventory.class);

        when(battlePlayer.getBattleItem(itemStack)).thenReturn(battleItem);
        when(battlePlayer.getEntity()).thenReturn(player);
        when(inventory.getItemInMainHand()).thenReturn(itemStack);
        when(player.getInventory()).thenReturn(inventory);

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(collisionChecker);
        boolean accepted = trainingMode.onItemHeld(battlePlayer, event);

        verify(battleItem, times(1)).onChangeHeldItem(battlePlayer);

        assertTrue(accepted);
    }

    @Test
    public void doNothingIfPickupItemHasNoBattleItemInstance() {
        BattleItem battleItem = mock(BattleItem.class);
        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        Item item = mock(Item.class);
        ItemStack itemStack = mock(ItemStack.class);
        Player player = mock(Player.class);

        when(battlePlayer.getBattleItem(itemStack)).thenReturn(battleItem);
        when(item.getItemStack()).thenReturn(itemStack);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(collisionChecker);
        // First call onDropItem to add the BattleItem to the dropped items list
        trainingMode.onItemDrop(battlePlayer, new PlayerDropItemEvent(player, item));

        boolean accepted = trainingMode.onPickupItem(battlePlayer, event);

        assertFalse(accepted);
    }

    @Test
    public void callsMethodWhenPickingUpItemThatHasBattleItemInstance() {
        BattleItem battleItem = mock(BattleItem.class);
        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        Item item = mock(Item.class);
        ItemStack itemStack = mock(ItemStack.class);
        Player player = mock(Player.class);

        when(battleItem.getItemStack()).thenReturn(itemStack);
        when(battlePlayer.getBattleItem(itemStack)).thenReturn(battleItem);
        when(item.getItemStack()).thenReturn(itemStack);
        when(itemStack.isSimilar(itemStack)).thenReturn(true);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, item, 0);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(collisionChecker);
        // First call onDropItem to add the BattleItem to the dropped items list
        trainingMode.onItemDrop(battlePlayer, new PlayerDropItemEvent(player, item));

        boolean accepted = trainingMode.onPickupItem(battlePlayer, event);

        verify(battlePlayer, times(1)).addItem(battleItem);

        assertTrue(accepted);
    }
}
