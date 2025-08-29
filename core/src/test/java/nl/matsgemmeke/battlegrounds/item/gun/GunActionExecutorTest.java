package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
    private Player player;
    private PlayerRegistry playerRegistry;

    @BeforeEach
    public void setUp() {
        gamePlayer = mock(GamePlayer.class);
        gunRegistry = mock(GunRegistry.class);
        player = mock(Player.class);
        playerRegistry = mock(PlayerRegistry.class);
    }

    @Test
    public void handleChangeFromActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleChangeFromActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleChangeFromActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onChangeFrom();
    }

    @Test
    public void handleChangeFromActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onChangeFrom();
    }

    @Test
    public void handleChangeToActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleChangeToActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.empty());
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleChangeToActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onChangeTo();
    }

    @Test
    public void handleChangeToActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onChangeTo();
    }

    @Test
    public void handleDropItemActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleDropItemActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.empty());
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gunRegistry, never()).unassign(any(Gun.class));
    }

    @Test
    public void handleDropItemActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onDrop();
        verify(gunRegistry, never()).unassign(gun);
    }

    @Test
    public void handleDropItemActionCallsGunFunctionAndUnassignsGunWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onDrop();
        verify(gunRegistry).unassign(gun);
    }

    @Test
    public void handlePickupItemActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handlePickupItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handlePickupItemActionDoesNothingWhenNoGunMatchesWithGivenItemStack() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getUnassignedGun(ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handlePickupItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gunRegistry, never()).assign(any(Gun.class), any(GamePlayer.class));
    }

    @Test
    public void handlePickupItemActionCallsGunFunctionAndAssignsGunWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getUnassignedGun(ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handlePickupItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onPickUp(gamePlayer);
        verify(gunRegistry).assign(gun, gamePlayer);
    }

    @Test
    public void handleLeftClickActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleLeftClickActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleLeftClickActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onLeftClick();
    }

    @Test
    public void handleLeftClickActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(gun).onLeftClick();
    }

    @Test
    public void handleRightClickActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleRightClickActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleRightClickActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onRightClick();
    }

    @Test
    public void handleRightClickActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(gun).onRightClick();
    }

    @Test
    public void handleSwapFromActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleSwapFromActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleSwapFromActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onSwapFrom();
    }

    @Test
    public void handleSwapFromActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(gun).onSwapFrom();
    }

    @Test
    public void handleSwapToActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleSwapToActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleSwapToActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onSwapTo();
    }

    @Test
    public void handleSwapToActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onSwapTo();
    }
}
