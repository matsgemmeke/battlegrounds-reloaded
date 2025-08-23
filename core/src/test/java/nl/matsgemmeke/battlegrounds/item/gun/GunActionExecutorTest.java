package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.ItemContainer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GunActionExecutorTest {

    private GamePlayer gamePlayer;
    private Gun gun;
    private ItemContainer<Gun, GunHolder> gunContainer;
    private ItemStack itemStack;

    @BeforeEach
    public void setUp() {
        this.gamePlayer = mock(GamePlayer.class);
        this.gunContainer = new ItemContainer<>();
        this.itemStack = new ItemStack(Material.IRON_HOE);

        this.gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);
    }

    @Test
    public void shouldCallFunctionOnGunWhenHolderPutsGunAway() {
        when(gun.getHolder()).thenReturn(gamePlayer);

        gunContainer.addAssignedItem(gun, gamePlayer);

        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleChangeFromAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun).onChangeFrom();
    }

    @Test
    public void shouldDoNothingWhenHolderPutsAwayGunButItIsNotRegistered() {
        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleChangeFromAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onChangeFrom();
    }

    @Test
    public void shouldDoNothingWhenHolderPutsAwayGunButHolderDoesNotMatch() {
        gunContainer.addAssignedItem(gun, gamePlayer);

        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleChangeToAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onChangeTo();
    }

    @Test
    public void shouldCallFunctionOnGunWhenHolderSwitchesToGun() {
        when(gun.getHolder()).thenReturn(gamePlayer);

        gunContainer.addAssignedItem(gun, gamePlayer);

        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleChangeToAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun).onChangeTo();
    }

    @Test
    public void shouldDoNothingWhenHolderSwitchesToGunButItIsNotInTheStorage() {
        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleChangeToAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onChangeTo();
    }

    @Test
    public void shouldDoNothingWhenHolderSwitchesToGunButHolderDoesNotMatch() {
        gunContainer.addAssignedItem(gun, gamePlayer);

        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleChangeToAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onChangeTo();
    }

    @Test
    public void shouldCallFunctionOnGunWhenHolderDropsTheGun() {
        when(gun.getHolder()).thenReturn(gamePlayer);

        gunContainer.addAssignedItem(gun, gamePlayer);

        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleDropItemAction(gamePlayer, itemStack);

        Gun assignedResult = gunContainer.getAssignedItem(gamePlayer, itemStack);
        Gun unassignedResult = gunContainer.getUnassignedItem(itemStack);

        assertTrue(performAction);
        assertEquals(gun, unassignedResult);
        assertNull(assignedResult);

        verify(gun).onDrop();
    }

    @Test
    public void shouldDoNothingWhenHolderDropsTheGunButItIsNotInTheStorage() {
        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleDropItemAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onDrop();
    }

    @Test
    public void shouldDoNothingWhenHolderDropsTheGunButHolderDoesNotMatch() {
        gunContainer.addAssignedItem(gun, gamePlayer);

        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleDropItemAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onDrop();
    }

    @Test
    public void shouldCallFunctionOnGunWhenHolderPicksUpTheGun() {
        gunContainer.addUnassignedItem(gun);

        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handlePickupItemAction(gamePlayer, itemStack);

        Gun assignedResult = gunContainer.getAssignedItem(gamePlayer, itemStack);
        Gun unassignedResult = gunContainer.getUnassignedItem(itemStack);

        assertTrue(performAction);
        assertEquals(gun, assignedResult);
        assertNull(unassignedResult);

        verify(gun).onPickUp(gamePlayer);
    }

    @Test
    public void shouldDoNothingUponItemPickupWhenGunIsNotRegistered() {
        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handlePickupItemAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onPickUp(gamePlayer);
    }

    @Test
    public void shouldCallFunctionOnGunWhenHolderLeftClicks() {
        when(gun.getHolder()).thenReturn(gamePlayer);

        gunContainer.addAssignedItem(gun, gamePlayer);

        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleLeftClickAction(gamePlayer, itemStack);

        assertFalse(performAction);

        verify(gun).onLeftClick();
    }

    @Test
    public void shouldDoNothingWhenHolderLeftClicksButGunIsNotRegistered() {
        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleLeftClickAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onLeftClick();
    }

    @Test
    public void shouldDoNothingWhenHolderLeftClicksButHolderDoesNotMatch() {
        gunContainer.addAssignedItem(gun, gamePlayer);

        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleLeftClickAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onLeftClick();
    }

    @Test
    public void shouldCallFunctionOnGunWhenHolderRightClicks() {
        when(gun.getHolder()).thenReturn(gamePlayer);

        gunContainer.addAssignedItem(gun, gamePlayer);

        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleRightClickAction(gamePlayer, itemStack);

        assertFalse(performAction);

        verify(gun).onRightClick();
    }

    @Test
    public void shouldDoNothingWhenHolderRightClicksButGunIsNotRegistered() {
        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleRightClickAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onRightClick();
    }

    @Test
    public void shouldDoNothingWhenHolderRightClicksButHolderDoesNotMatch() {
        gunContainer.addAssignedItem(gun, gamePlayer);

        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleRightClickAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onRightClick();
    }

    @Test
    public void shouldCallFunctionOnGunWhenHolderSwapsItemAway() {
        when(gun.getHolder()).thenReturn(gamePlayer);

        gunContainer.addAssignedItem(gun, gamePlayer);

        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleSwapFromAction(gamePlayer, itemStack);

        assertFalse(performAction);

        verify(gun).onSwapFrom();
    }

    @Test
    public void shouldDoNothingWhenHolderSwapsItemAwayButGunIsNotRegistered() {
        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleSwapFromAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onSwapFrom();
    }

    @Test
    public void shouldDoNothingWhenHolderSwapsItemAwayButHolderDoesNotMatch() {
        gunContainer.addAssignedItem(gun, gamePlayer);

        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleSwapFromAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onSwapFrom();
    }

    @Test
    public void shouldCallFunctionOnGunWhenHolderSwapsToItem() {
        when(gun.getHolder()).thenReturn(gamePlayer);

        gunContainer.addAssignedItem(gun, gamePlayer);

        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleSwapToAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun).onSwapTo();
    }

    @Test
    public void shouldDoNothingWhenHolderSwapsToItemButGunIsNotRegistered() {
        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleSwapToAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onSwapTo();
    }

    @Test
    public void shouldDoNothingWhenHolderSwapsToItemButHolderDoesNotMatch() {
        gunContainer.addAssignedItem(gun, gamePlayer);

        GunActionExecutor actionExecutor = new GunActionExecutor(gunContainer);
        boolean performAction = actionExecutor.handleSwapToAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onSwapTo();
    }
}
