package nl.matsgemmeke.battlegrounds.game.component.controls.handler;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemControllerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.controls.result.DispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionResult;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GunInteractionHandlerTest {

    private static final ItemStack ITEM_STACK = new ItemStack(Material.IRON_HOE);
    private static final UUID GUN_ID = UUID.randomUUID();

    @Mock
    private GamePlayer gamePlayer;
    @Mock
    private Gun gun;
    @Mock
    private GunRegistry gunRegistry;
    @Mock
    private ItemController<GunUser> controller;
    @Mock
    private ItemControllerRegistry itemControllerRegistry;
    @InjectMocks
    private GunInteractionHandler interactionHandler;

    @Test
    @DisplayName("handleChangeFrom returns unhandled dispatch result when given combination of player and item stack is not registered")
    void handleChangeFrom_playerAndItemStackNotRegistered() {
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        DispatchResult result = interactionHandler.handleChangeFrom(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("handleChangeFrom returns unhandled dispatch result when item controller cannot be found")
    void handleChangeFrom_itemControllerNotFound() {
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));
        when(gun.getId()).thenReturn(GUN_ID);
        when(itemControllerRegistry.getGunController(GUN_ID)).thenReturn(Optional.empty());

        DispatchResult result = interactionHandler.handleChangeFrom(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("handleChangeFrom returns display result with values from the item controller's action result")
    void handleChangeFrom_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));
        when(gun.getId()).thenReturn(GUN_ID);
        when(itemControllerRegistry.getGunController(GUN_ID)).thenReturn(Optional.of(controller));
        when(controller.performActionNew(Action.CHANGE_FROM, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleChangeFrom(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();

        verify(controller).cancelAllFunctions();
    }

    @Test
    @DisplayName("handleChangeTo returns display result with values from the item controller's action result")
    void handleChangeTo_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));
        when(gun.getId()).thenReturn(GUN_ID);
        when(itemControllerRegistry.getGunController(GUN_ID)).thenReturn(Optional.of(controller));
        when(controller.performActionNew(Action.CHANGE_TO, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleChangeTo(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("handleDropItem returns display result with values from the item controller's action result")
    void handleDropItem_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));
        when(gun.getId()).thenReturn(GUN_ID);
        when(itemControllerRegistry.getGunController(GUN_ID)).thenReturn(Optional.of(controller));
        when(controller.performActionNew(Action.DROP_ITEM, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleDropItem(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();

        verify(gun).setUser(null);
        verify(controller).cancelAllFunctions();
    }

    @Test
    @DisplayName("handleLeftClick returns display result with values from the item controller's action result")
    void handleLeftClick_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));
        when(gun.getId()).thenReturn(GUN_ID);
        when(itemControllerRegistry.getGunController(GUN_ID)).thenReturn(Optional.of(controller));
        when(controller.performActionNew(Action.LEFT_CLICK, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleLeftClick(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("handlePickupItem returns display result with values from the item controller's action result")
    void handlePickupItem_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));
        when(gun.getId()).thenReturn(GUN_ID);
        when(itemControllerRegistry.getGunController(GUN_ID)).thenReturn(Optional.of(controller));
        when(controller.performActionNew(Action.PICKUP_ITEM, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handlePickupItem(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();

        verify(gun).setUser(gamePlayer);
    }

    @Test
    @DisplayName("handleRightClick returns display result with values from the item controller's action result")
    void handleRightClick_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));
        when(gun.getId()).thenReturn(GUN_ID);
        when(itemControllerRegistry.getGunController(GUN_ID)).thenReturn(Optional.of(controller));
        when(controller.performActionNew(Action.RIGHT_CLICK, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleRightClick(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("handleSwapFrom returns display result with values from the item controller's action result")
    void handleSwapFrom_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));
        when(gun.getId()).thenReturn(GUN_ID);
        when(itemControllerRegistry.getGunController(GUN_ID)).thenReturn(Optional.of(controller));
        when(controller.performActionNew(Action.SWAP_FROM, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleSwapFrom(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("handleSwapTo returns display result with values from the item controller's action result")
    void handleSwapTo_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));
        when(gun.getId()).thenReturn(GUN_ID);
        when(itemControllerRegistry.getGunController(GUN_ID)).thenReturn(Optional.of(controller));
        when(controller.performActionNew(Action.SWAP_TO, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleSwapTo(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }
}
