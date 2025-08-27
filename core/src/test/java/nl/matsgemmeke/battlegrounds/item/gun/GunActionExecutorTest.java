package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GunActionExecutorTest {

    private static final ItemStack ITEM_STACK = new ItemStack(Material.IRON_HOE);

    private GamePlayer gamePlayer;
    private GunRegistry gunRegistry;

    @BeforeEach
    public void setUp() {
        gamePlayer = mock(GamePlayer.class);
        gunRegistry = mock(GunRegistry.class);
    }

    @Test
    public void handleChangeFromActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleChangeFromAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleChangeFromActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleChangeFromAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onChangeFrom();
    }

    @Test
    public void handleChangeFromActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleChangeFromAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onChangeFrom();
    }

    @Test
    public void handleChangeToActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleChangeToAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleChangeToActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleChangeToAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onChangeTo();
    }

    @Test
    public void handleChangeToActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleChangeToAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onChangeTo();
    }

    @Test
    public void handleDropItemActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleDropItemAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gunRegistry, never()).unassign(any(Gun.class));
    }

    @Test
    public void handleDropItemActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleDropItemAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onDrop();
        verify(gunRegistry, never()).unassign(gun);
    }

    @Test
    public void handleDropItemActionCallsGunFunctionAndUnassignsGunWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleDropItemAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onDrop();
        verify(gunRegistry).unassign(gun);
    }

    @Test
    public void handlePickupItemActionDoesNothingWhenNoGunMatchesWithGivenItemStack() {
        when(gunRegistry.getUnassignedGun(ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handlePickupItemAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gunRegistry, never()).assign(any(Gun.class), any(GamePlayer.class));
    }

    @Test
    public void handlePickupItemActionCallsGunFunctionAndAssignsGunWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);

        when(gunRegistry.getUnassignedGun(ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handlePickupItemAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onPickUp(gamePlayer);
        verify(gunRegistry).assign(gun, gamePlayer);
    }

    @Test
    public void handleLeftClickActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleLeftClickActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onLeftClick();
    }

    @Test
    public void handleLeftClickActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(gun).onLeftClick();
    }

    @Test
    public void handleRightClickActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleRightClickActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onRightClick();
    }

    @Test
    public void handleRightClickActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(gun).onRightClick();
    }

    @Test
    public void handleSwapFromActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleSwapFromAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleSwapFromActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleSwapFromAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onSwapFrom();
    }

    @Test
    public void handleSwapFromActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleSwapFromAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(gun).onSwapFrom();
    }

    @Test
    public void handleSwapToActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleSwapToAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleSwapToActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleSwapToAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onSwapTo();
    }

    @Test
    public void handleSwapToActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry);
        boolean performAction = actionExecutor.handleSwapToAction(gamePlayer, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onSwapTo();
    }
}
