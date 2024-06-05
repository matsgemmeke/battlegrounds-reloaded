package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.GunHolder;
import nl.matsgemmeke.battlegrounds.item.ItemRegister;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class GunBehaviorTest {

    private GamePlayer gamePlayer;
    private Gun gun;
    private ItemRegister<Gun, GunHolder> register;
    private ItemStack itemStack;

    @Before
    public void setUp() {
        this.gamePlayer = mock(GamePlayer.class);
        this.register = new ItemRegister<>();
        this.itemStack = new ItemStack(Material.IRON_HOE);

        this.gun = mock(Gun.class);
        when(gun.isMatching(itemStack)).thenReturn(true);

        PowerMockito.mockStatic(Bukkit.class);
        ItemFactory itemFactory = mock(ItemFactory.class);
        when(Bukkit.getItemFactory()).thenReturn(itemFactory);
    }

    @Test
    public void shouldCallFunctionOnGunWhenHolderPutsGunAway() {
        when(gun.getHolder()).thenReturn(gamePlayer);

        register.addAssignedItem(gun, gamePlayer);

        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleChangeFromAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun).onChangeFrom();
    }

    @Test
    public void shouldDoNothingWhenHolderPutsAwayGunButItIsNotRegistered() {
        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleChangeFromAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onChangeFrom();
    }

    @Test
    public void shouldDoNothingWhenHolderPutsAwayGunButHolderDoesNotMatch() {
        register.addAssignedItem(gun, gamePlayer);

        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleChangeToAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onChangeTo();
    }

    @Test
    public void shouldCallFunctionOnGunWhenHolderSwitchesToGun() {
        when(gun.getHolder()).thenReturn(gamePlayer);

        register.addAssignedItem(gun, gamePlayer);

        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleChangeToAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun).onChangeTo();
    }

    @Test
    public void shouldDoNothingWhenHolderSwitchesToGunButItIsNotInTheRegister() {
        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleChangeToAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onChangeTo();
    }

    @Test
    public void shouldDoNothingWhenHolderSwitchesToGunButHolderDoesNotMatch() {
        register.addAssignedItem(gun, gamePlayer);

        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleChangeToAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onChangeTo();
    }

    @Test
    public void shouldCallFunctionOnGunWhenHolderDropsTheGun() {
        when(gun.getHolder()).thenReturn(gamePlayer);

        register.addAssignedItem(gun, gamePlayer);

        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleDropItemAction(gamePlayer, itemStack);

        Gun assignedResult = register.getAssignedItem(gamePlayer, itemStack);
        Gun unassignedResult = register.getUnassignedItem(itemStack);

        assertTrue(performAction);
        assertEquals(gun, unassignedResult);
        assertNull(assignedResult);

        verify(gun).onDrop();
    }

    @Test
    public void shouldDoNothingWhenHolderDropsTheGunButItIsNotInTheRegister() {
        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleDropItemAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onDrop();
    }

    @Test
    public void shouldDoNothingWhenHolderDropsTheGunButHolderDoesNotMatch() {
        register.addAssignedItem(gun, gamePlayer);

        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleDropItemAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onDrop();
    }

    @Test
    public void shouldCallFunctionOnGunWhenHolderPicksUpTheGun() {
        register.addUnassignedItem(gun);

        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handlePickupItemAction(gamePlayer, itemStack);

        Gun assignedResult = register.getAssignedItem(gamePlayer, itemStack);
        Gun unassignedResult = register.getUnassignedItem(itemStack);

        assertTrue(performAction);
        assertEquals(gun, assignedResult);
        assertNull(unassignedResult);

        verify(gun).onPickUp(gamePlayer);
    }

    @Test
    public void shouldDoNothingUponItemPickupWhenGunIsNotRegistered() {
        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handlePickupItemAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onPickUp(gamePlayer);
    }

    @Test
    public void shouldCallFunctionOnGunWhenHolderLeftClicks() {
        when(gun.getHolder()).thenReturn(gamePlayer);

        register.addAssignedItem(gun, gamePlayer);

        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleLeftClickAction(gamePlayer, itemStack);

        assertFalse(performAction);

        verify(gun).onLeftClick();
    }

    @Test
    public void shouldDoNothingWhenHolderLeftClicksButGunIsNotRegistered() {
        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleLeftClickAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onLeftClick();
    }

    @Test
    public void shouldDoNothingWhenHolderLeftClicksButHolderDoesNotMatch() {
        register.addAssignedItem(gun, gamePlayer);

        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleLeftClickAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onLeftClick();
    }

    @Test
    public void shouldCallFunctionOnGunWhenHolderRightClicks() {
        when(gun.getHolder()).thenReturn(gamePlayer);

        register.addAssignedItem(gun, gamePlayer);

        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleRightClickAction(gamePlayer, itemStack);

        assertFalse(performAction);

        verify(gun).onRightClick();
    }

    @Test
    public void shouldDoNothingWhenHolderRightClicksButGunIsNotRegistered() {
        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleRightClickAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onRightClick();
    }

    @Test
    public void shouldDoNothingWhenHolderRightClicksButHolderDoesNotMatch() {
        register.addAssignedItem(gun, gamePlayer);

        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleRightClickAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onRightClick();
    }

    @Test
    public void shouldCallFunctionOnGunWhenHolderSwapsItemAway() {
        when(gun.getHolder()).thenReturn(gamePlayer);

        register.addAssignedItem(gun, gamePlayer);

        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleSwapFromAction(gamePlayer, itemStack);

        assertFalse(performAction);

        verify(gun).onSwapFrom();
    }

    @Test
    public void shouldDoNothingWhenHolderSwapsItemAwayButGunIsNotRegistered() {
        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleSwapFromAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onSwapFrom();
    }

    @Test
    public void shouldDoNothingWhenHolderSwapsItemAwayButHolderDoesNotMatch() {
        register.addAssignedItem(gun, gamePlayer);

        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleSwapFromAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onSwapFrom();
    }

    @Test
    public void shouldCallFunctionOnGunWhenHolderSwapsToItem() {
        when(gun.getHolder()).thenReturn(gamePlayer);

        register.addAssignedItem(gun, gamePlayer);

        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleSwapToAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun).onSwapTo();
    }

    @Test
    public void shouldDoNothingWhenHolderSwapsToItemButGunIsNotRegistered() {
        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleSwapToAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onSwapTo();
    }

    @Test
    public void shouldDoNothingWhenHolderSwapsToItemButHolderDoesNotMatch() {
        register.addAssignedItem(gun, gamePlayer);

        GunBehavior behavior = new GunBehavior(register);
        boolean performAction = behavior.handleSwapToAction(gamePlayer, itemStack);

        assertTrue(performAction);

        verify(gun, never()).onSwapTo();
    }
}
