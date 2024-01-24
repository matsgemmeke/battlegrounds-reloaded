package com.github.matsgemmeke.battlegounds.game;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.item.Weapon;
import com.github.matsgemmeke.battlegrounds.game.DefaultTrainingMode;
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

    private InternalsProvider internals;

    @Before
    public void setUp() {
        this.internals = mock(InternalsProvider.class);
    }

    @Test
    public void addingPlayersAddThemToPlayerList() {
        Player player = mock(Player.class);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals);
        GamePlayer gamePlayer = trainingMode.addPlayer(player);

        assertNotNull(gamePlayer);
    }

    @Test
    public void executesLeftClickMethodIfActionIsLeftClick() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        PlayerInteractEvent event = new PlayerInteractEvent(null, Action.LEFT_CLICK_AIR, itemStack, null, null);

        Weapon weapon = mock(Weapon.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getWeapon(itemStack)).thenReturn(weapon);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals);
        boolean result = trainingMode.onInteract(gamePlayer, event);

        verify(weapon).onLeftClick(gamePlayer);

        assertTrue(result);
        assertTrue(event.isCancelled());
    }

    @Test
    public void executesRightClickMethodIfActionIsRightClick() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        PlayerInteractEvent event = new PlayerInteractEvent(null, Action.RIGHT_CLICK_AIR, itemStack, null, null);

        Weapon weapon = mock(Weapon.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getWeapon(itemStack)).thenReturn(weapon);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals);
        boolean result = trainingMode.onInteract(gamePlayer, event);

        verify(weapon).onRightClick(gamePlayer);

        assertTrue(result);
        assertTrue(event.isCancelled());
    }

    @Test
    public void doesNothingWithInteractionWithoutItemStack() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        PlayerInteractEvent event = new PlayerInteractEvent(null, null, null, null, null);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals);
        boolean result = trainingMode.onInteract(gamePlayer, event);

        assertFalse(result);
    }

    @Test
    public void doesNothingWithInteractionWhenWeaponDoesNotExist() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        PlayerInteractEvent event = new PlayerInteractEvent(null, null, itemStack, null, null);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals);
        boolean result = trainingMode.onInteract(gamePlayer, event);

        assertFalse(result);
    }

    @Test
    public void shouldDoNothingIfDroppedItemIsNoWeapon() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack itemStack = mock(ItemStack.class);

        org.bukkit.entity.Item itemEntity = mock(org.bukkit.entity.Item.class);
        when(itemEntity.getItemStack()).thenReturn(itemStack);

        PlayerDropItemEvent event = new PlayerDropItemEvent(null, itemEntity);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals);
        boolean accepted = trainingMode.onItemDrop(gamePlayer, event);

        assertFalse(accepted);
    }

    @Test
    public void shouldAddWeaponToListAndCallDropMethod() {
        ItemStack itemStack = mock(ItemStack.class);
        Weapon weapon = mock(Weapon.class);

        org.bukkit.entity.Item itemEntity = mock(org.bukkit.entity.Item.class);
        when(itemEntity.getItemStack()).thenReturn(itemStack);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getWeapon(itemStack)).thenReturn(weapon);

        PlayerDropItemEvent event = new PlayerDropItemEvent(null, itemEntity);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals);
        boolean accepted = trainingMode.onItemDrop(gamePlayer, event);

        verify(weapon).onDrop(gamePlayer);

        assertTrue(accepted);
    }

    @Test
    public void shouldDoNothingIfChangedItemIsNoWeapon() {
        ItemStack itemStack = mock(ItemStack.class);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItemInMainHand()).thenReturn(itemStack);

        Player player = mock(Player.class);
        when(player.getInventory()).thenReturn(inventory);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals);
        boolean accepted = trainingMode.onItemHeld(gamePlayer, event);

        assertFalse(accepted);
    }

    @Test
    public void shouldCallMethodWhenChangingItemWhichIsWeapon() {
        ItemStack itemStack = mock(ItemStack.class);
        Weapon weapon = mock(Weapon.class);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItemInMainHand()).thenReturn(itemStack);

        Player player = mock(Player.class);
        when(player.getInventory()).thenReturn(inventory);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);
        when(gamePlayer.getWeapon(itemStack)).thenReturn(weapon);

        PlayerItemHeldEvent event = new PlayerItemHeldEvent(player, 0, 1);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals);
        boolean accepted = trainingMode.onItemHeld(gamePlayer, event);

        verify(weapon).onChangeHeldItem(gamePlayer);

        assertTrue(accepted);
    }

    @Test
    public void shouldDoNothingIfPickedUpItemIsNoWeapon() {
        ItemStack itemStack = mock(ItemStack.class);
        Player player = mock(Player.class);
        Weapon weapon = mock(Weapon.class);

        org.bukkit.entity.Item itemEntity = mock(org.bukkit.entity.Item.class);
        when(itemEntity.getItemStack()).thenReturn(itemStack);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getWeapon(itemStack)).thenReturn(weapon);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, itemEntity, 0);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals);
        // First call onDropItem, so it adds the weapon to the dropped weapons list so we have at least one entry
        trainingMode.onItemDrop(gamePlayer, new PlayerDropItemEvent(player, itemEntity));

        boolean accepted = trainingMode.onPickupItem(gamePlayer, event);

        assertFalse(accepted);
    }

    @Test
    public void callsMethodWhenPickingUpItemThatHasItemInstance() {
        ItemStack itemStack = mock(ItemStack.class);

        org.bukkit.entity.Item itemEntity = mock(org.bukkit.entity.Item.class);
        when(itemEntity.getItemStack()).thenReturn(itemStack);

        Player player = mock(Player.class);

        Weapon weapon = mock(Weapon.class);
        when(weapon.getItemStack()).thenReturn(itemStack);
        when(weapon.isMatching(itemStack)).thenReturn(true);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getWeapon(itemStack)).thenReturn(weapon);

        EntityPickupItemEvent event = new EntityPickupItemEvent(player, itemEntity, 0);

        DefaultTrainingMode trainingMode = new DefaultTrainingMode(internals);
        // First call onDropItem to add the weapon to the dropped weapons list
        trainingMode.onItemDrop(gamePlayer, new PlayerDropItemEvent(player, itemEntity));

        boolean accepted = trainingMode.onPickupItem(gamePlayer, event);

        verify(gamePlayer).addWeapon(weapon);

        assertTrue(accepted);
    }
}
