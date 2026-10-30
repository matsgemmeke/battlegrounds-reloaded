package nl.matsgemmeke.battlegrounds.game.component.controls.handler;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemControllerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.controls.result.DispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.controls.result.PickupDispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.item.ItemCreator;
import nl.matsgemmeke.battlegrounds.game.component.item.ItemNotFoundException;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionResult;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeleeWeaponInteractionHandlerTest {

    private static final UUID MELEE_WEAPON_ID = UUID.randomUUID();
    private static final String NAME = "Test Melee Weapon";
    private static final String PLAYER_NAME = "TestPlayer";
    private static final int ITEM_SLOT = 5;

    @Mock
    private GamePlayer gamePlayer;
    @Mock
    private ItemController<MeleeWeaponUser> controller;
    @Mock
    private ItemControllerRegistry itemControllerRegistry;
    @Mock
    private ItemCreator itemCreator;
    @Mock
    private ItemStack itemStack;
    @Mock
    private Logger logger;
    @Mock
    private MeleeWeapon meleeWeapon;
    @Mock
    private MeleeWeaponRegistry meleeWeaponRegistry;
    @Mock
    private WeaponNameExtractor weaponNameExtractor;
    @InjectMocks
    private MeleeWeaponInteractionHandler interactionHandler;

    @Test
    @DisplayName("handleChangeFrom returns unhandled dispatch result when given combination of player and item stack is not registered")
    void handleChangeFrom_playerAndItemStackNotRegistered() {
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, itemStack)).thenReturn(Optional.empty());

        DispatchResult result = interactionHandler.handleChangeFrom(gamePlayer, itemStack);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("handleChangeFrom returns unhandled dispatch result when item controller cannot be found")
    void handleChangeFrom_itemControllerNotFound() {
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, itemStack)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.empty());

        DispatchResult result = interactionHandler.handleChangeFrom(gamePlayer, itemStack);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("handleChangeFrom returns display result with values from the item controller's action result")
    void handleChangeFrom_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, itemStack)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));
        when(controller.performAction(Action.CHANGE_FROM, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleChangeFrom(gamePlayer, itemStack);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();

        verify(controller).cancelAllFunctions();
    }

    @Test
    @DisplayName("handleChangeTo returns display result with values from the item controller's action result")
    void handleChangeTo_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, itemStack)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));
        when(controller.performAction(Action.CHANGE_TO, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleChangeTo(gamePlayer, itemStack);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("handleDropItem returns display result with values from the item controller's action result")
    void handleDropItem_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, itemStack)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));
        when(controller.performAction(Action.DROP_ITEM, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleDropItem(gamePlayer, itemStack);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();

        verify(controller).cancelAllFunctions();
        verify(meleeWeapon).unassign();
    }

    @Test
    @DisplayName("handleLeftClick returns display result with values from the item controller's action result")
    void handleLeftClick_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, itemStack)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));
        when(controller.performAction(Action.LEFT_CLICK, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleLeftClick(gamePlayer, itemStack);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("handlePickupAction returns unhandled result when picking up complete melee weapon that the player already has, but unable to find controller")
    void handlePickupAction_controllerNotFound() {
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(itemStack)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.empty());

        PickupDispatchResult result = interactionHandler.handlePickupItem(gamePlayer, itemStack);

        assertThat(result.dispatched()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
        assertThat(result.removeItem()).isFalse();

        verify(meleeWeapon, never()).assign(gamePlayer);
    }

    @Test
    @DisplayName("handlePickupAction return handled result when picking up complete melee weapon that the player already has")
    void handlePickupAction_assignsCompleteMeleeWeapon() {
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(itemStack)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));

        PickupDispatchResult result = interactionHandler.handlePickupItem(gamePlayer, itemStack);

        assertThat(result.dispatched()).isTrue();
        assertThat(result.cancelEvent()).isFalse();
        assertThat(result.removeItem()).isFalse();
    }

    @Test
    @DisplayName("handlePickupAction returns unhandled result when picking up single projectile, but the weapon name is null")
    void handlePickupAction_singleProjectileSelfContainedNullWeaponName() {
        when(weaponNameExtractor.extractWeaponName(itemStack)).thenReturn(Optional.empty());

        PickupDispatchResult result = interactionHandler.handlePickupItem(gamePlayer, itemStack);

        assertThat(result.dispatched()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
        assertThat(result.removeItem()).isFalse();

        verify(gamePlayer, never()).addItem(any(ItemStack.class));
    }

    @Test
    @DisplayName("handlePickupAction returns unhandled result when picking up single projectile, but the weapon name is invalid")
    void handlePickupAction_singleProjectileSelfContainedInvalidWeaponName() {
        when(weaponNameExtractor.extractWeaponName(itemStack)).thenReturn(Optional.of(NAME));
        when(itemCreator.createMeleeWeapon(NAME, gamePlayer)).thenThrow(new ItemNotFoundException("error"));
        when(gamePlayer.getName()).thenReturn(PLAYER_NAME);

        PickupDispatchResult result = interactionHandler.handlePickupItem(gamePlayer, itemStack);

        assertThat(result.dispatched()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
        assertThat(result.removeItem()).isFalse();

        verify(logger).warning("Player TestPlayer picked up item with unrecognized melee weapon name 'Test Melee Weapon' - ignoring pickup");
        verify(gamePlayer, never()).addItem(any(ItemStack.class));
    }

    @Test
    @DisplayName("handlePickupAction returns ignore result when picking up single projectile that is self contained, but controller cannot be found")
    void handlePickupAction_singleProjectileSelfContainedControllerNotFound() {
        when(weaponNameExtractor.extractWeaponName(itemStack)).thenReturn(Optional.of(NAME));
        when(itemCreator.createMeleeWeapon(NAME, gamePlayer)).thenReturn(meleeWeapon);
        when(meleeWeapon.isSelfContained()).thenReturn(true);
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.empty());

        PickupDispatchResult result = interactionHandler.handlePickupItem(gamePlayer, itemStack);

        assertThat(result.dispatched()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
        assertThat(result.removeItem()).isFalse();
    }

    @Test
    @DisplayName("handlePickupAction returns cancelled result when picking up single projectile that is self contained")
    void handlePickupAction_singleProjectileSelfContained() {
        when(weaponNameExtractor.extractWeaponName(itemStack)).thenReturn(Optional.of(NAME));
        when(itemCreator.createMeleeWeapon(NAME, gamePlayer)).thenReturn(meleeWeapon);
        when(meleeWeapon.isSelfContained()).thenReturn(true);
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));

        PickupDispatchResult result = interactionHandler.handlePickupItem(gamePlayer, itemStack);

        assertThat(result.dispatched()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
        assertThat(result.removeItem()).isTrue();

        verify(controller).performAction(Action.PICKUP_ITEM, gamePlayer);
    }

    @Test
    @DisplayName("handlePickupAction returns ignore result when picking up single projectile that is not self contained and is already owned by player, but controller cannot be found")
    void handlePickupAction_singleProjectileNotSelfContainedAlreadyOwnedByPlayerControllerNotFound() {
        ResourceContainer resourceContainer = new ResourceContainer(1, 1, 2, 5);

        MeleeWeapon existingMeleeWeapon = mock(MeleeWeapon.class);
        when(existingMeleeWeapon.getName()).thenReturn(NAME);
        when(existingMeleeWeapon.getResourceContainer()).thenReturn(resourceContainer);
        when(existingMeleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);

        when(weaponNameExtractor.extractWeaponName(itemStack)).thenReturn(Optional.of(NAME));
        when(itemCreator.createMeleeWeapon(NAME, gamePlayer)).thenReturn(meleeWeapon);
        when(meleeWeapon.isSelfContained()).thenReturn(false);
        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of(existingMeleeWeapon));
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.empty());

        PickupDispatchResult result = interactionHandler.handlePickupItem(gamePlayer, itemStack);

        assertThat(result.dispatched()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
        assertThat(result.removeItem()).isFalse();

        verify(existingMeleeWeapon, never()).update();
        verify(gamePlayer, never()).setItem(anyInt(), any(ItemStack.class));
        verify(controller, never()).performAction(any(Action.class), any(GamePlayer.class));
    }

    @Test
    @DisplayName("handlePickupAction returns ignore result when picking up single projectile that is not self contained and is already owned by player, but item slot cannot be determined")
    void handlePickupAction_singleProjectileNotSelfContainedAlreadyOwnedByPlayerItemSlotCannotBeDetermined() {
        ResourceContainer resourceContainer = new ResourceContainer(1, 1, 2, 5);

        MeleeWeapon existingMeleeWeapon = mock(MeleeWeapon.class);
        when(existingMeleeWeapon.getName()).thenReturn(NAME);
        when(existingMeleeWeapon.getResourceContainer()).thenReturn(resourceContainer);
        when(existingMeleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);

        when(weaponNameExtractor.extractWeaponName(itemStack)).thenReturn(Optional.of(NAME));
        when(itemCreator.createMeleeWeapon(NAME, gamePlayer)).thenReturn(meleeWeapon);
        when(meleeWeapon.isSelfContained()).thenReturn(false);
        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of(existingMeleeWeapon));
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));
        when(gamePlayer.getItemSlot(existingMeleeWeapon)).thenReturn(Optional.empty());

        PickupDispatchResult result = interactionHandler.handlePickupItem(gamePlayer, itemStack);

        assertThat(result.dispatched()).isTrue();
        assertThat(result.cancelEvent()).isFalse();
        assertThat(result.removeItem()).isFalse();

        verify(existingMeleeWeapon, never()).update();
        verify(gamePlayer, never()).setItem(anyInt(), any(ItemStack.class));
        verify(controller, never()).performAction(any(Action.class), any(GamePlayer.class));
    }

    @Test
    @DisplayName("handlePickupAction returns cancel result when picking up single projectile that is not self contained and is already owned by player")
    void handlePickupAction_singleProjectileNotSelfContainedAlreadyOwnedByPlayer() {
        ResourceContainer resourceContainer = new ResourceContainer(1, 1, 2, 5);
        ItemStack existingItemStack = mock(ItemStack.class);

        MeleeWeapon existingMeleeWeapon = mock(MeleeWeapon.class);
        when(existingMeleeWeapon.getName()).thenReturn(NAME);
        when(existingMeleeWeapon.getResourceContainer()).thenReturn(resourceContainer);
        when(existingMeleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(existingMeleeWeapon.getItemStack()).thenReturn(existingItemStack);

        when(weaponNameExtractor.extractWeaponName(itemStack)).thenReturn(Optional.of(NAME));
        when(itemCreator.createMeleeWeapon(NAME, gamePlayer)).thenReturn(meleeWeapon);
        when(meleeWeapon.isSelfContained()).thenReturn(false);
        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of(existingMeleeWeapon));
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));
        when(gamePlayer.getItemSlot(existingMeleeWeapon)).thenReturn(Optional.of(ITEM_SLOT));

        PickupDispatchResult result = interactionHandler.handlePickupItem(gamePlayer, itemStack);

        assertThat(result.dispatched()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
        assertThat(result.removeItem()).isTrue();
        assertThat(resourceContainer.getReserveAmount()).isEqualTo(3);

        verify(existingMeleeWeapon).update();
        verify(gamePlayer).setItem(ITEM_SLOT, existingItemStack);
        verify(controller).performAction(Action.PICKUP_ITEM, gamePlayer);
    }

    @Test
    @DisplayName("handlePickupAction returns ignore result when picking up single projectile that is not self contained and is not owned by player")
    void handlePickupAction_singleProjectileNotSelfContainedNotOwnedByPlayer() {
        when(weaponNameExtractor.extractWeaponName(itemStack)).thenReturn(Optional.of(NAME));
        when(itemCreator.createMeleeWeapon(NAME, gamePlayer)).thenReturn(meleeWeapon);
        when(meleeWeapon.isSelfContained()).thenReturn(false);
        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of());

        PickupDispatchResult result = interactionHandler.handlePickupItem(gamePlayer, itemStack);

        assertThat(result.dispatched()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
        assertThat(result.removeItem()).isFalse();
    }

    @Test
    @DisplayName("handleRightClick returns display result with values from the item controller's action result")
    void handleRightClick_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, itemStack)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));
        when(controller.performAction(Action.RIGHT_CLICK, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleRightClick(gamePlayer, itemStack);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("handleSwapFrom returns display result with values from the item controller's action result")
    void handleSwapFrom_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, itemStack)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));
        when(controller.performAction(Action.SWAP_FROM, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleSwapFrom(gamePlayer, itemStack);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("handleSwapTo returns display result with values from the item controller's action result")
    void handleSwapTo_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, itemStack)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));
        when(controller.performAction(Action.SWAP_TO, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleSwapTo(gamePlayer, itemStack);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }
}
