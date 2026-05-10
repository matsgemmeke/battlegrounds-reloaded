package nl.matsgemmeke.battlegrounds.game.component.controls.handler;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemControllerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.controls.result.DispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.item.ItemCreator;
import nl.matsgemmeke.battlegrounds.game.component.item.ItemNotFoundException;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionResult;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeleeWeaponInteractionHandlerTest {

    private static final ItemStack ITEM_STACK = new ItemStack(Material.IRON_HOE);
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
    private Logger logger;
    @Mock
    private MeleeWeapon meleeWeapon;
    @Mock
    private MeleeWeaponRegistry meleeWeaponRegistry;
    @Mock
    private NamespacedKeyCreator namespacedKeyCreator;
    @InjectMocks
    private MeleeWeaponInteractionHandler interactionHandler;

    @Test
    @DisplayName("handleChangeFrom returns unhandled dispatch result when given combination of player and item stack is not registered")
    void handleChangeFrom_playerAndItemStackNotRegistered() {
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        DispatchResult result = interactionHandler.handleChangeFrom(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("handleChangeFrom returns unhandled dispatch result when item controller cannot be found")
    void handleChangeFrom_itemControllerNotFound() {
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.empty());

        DispatchResult result = interactionHandler.handleChangeFrom(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("handleChangeFrom returns display result with values from the item controller's action result")
    void handleChangeFrom_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));
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

        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));
        when(controller.performActionNew(Action.CHANGE_TO, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleChangeTo(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("handleDropItem returns display result with values from the item controller's action result")
    void handleDropItem_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));
        when(controller.performActionNew(Action.DROP_ITEM, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleDropItem(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();

        verify(controller).cancelAllFunctions();
        verify(meleeWeapon).unassign();
    }

    @Test
    @DisplayName("handleLeftClick returns display result with values from the item controller's action result")
    void handleLeftClick_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));
        when(controller.performActionNew(Action.LEFT_CLICK, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleLeftClick(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("handlePickupItem does not resupply existing melee weapon when unable to find item slot for existing melee weapon")
    void handlePickupItem_resupplyExistingMeleeWeaponItemSlotNotFound() {
        MeleeWeapon existingMeleeWeapon = mock(MeleeWeapon.class);
        when(existingMeleeWeapon.getName()).thenReturn(NAME);

        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of(existingMeleeWeapon));
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getName()).thenReturn(NAME);
        when(gamePlayer.getItemSlot(existingMeleeWeapon)).thenReturn(Optional.empty());

        DispatchResult result = interactionHandler.handlePickupItem(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();

        verify(existingMeleeWeapon, never()).update();
        verify(gamePlayer, never()).setItem(anyInt(), any(ItemStack.class));
    }

    @ParameterizedTest
    @CsvSource({ "2,2", "100,3" })
    @DisplayName("handlePickupAction supplies existing melee weapon with resources from picked up melee weapon")
    void handlePickupAction_resuppliesExistingMeleeWeapon(int pickedUpReserveAmount, int expectedReserveAmount) {
        ItemStack existingItemStack = new ItemStack(Material.IRON_SWORD);
        ResourceContainer pickedUpResourceContainer = new ResourceContainer(1, 0, pickedUpReserveAmount, 2);
        ResourceContainer existingResourceContainer = new ResourceContainer(1, 1, 0, 3);

        MeleeWeapon existingMeleeWeapon = mock(MeleeWeapon.class);
        when(existingMeleeWeapon.getName()).thenReturn(NAME);
        when(existingMeleeWeapon.getResourceContainer()).thenReturn(existingResourceContainer);
        when(existingMeleeWeapon.getItemStack()).thenReturn(existingItemStack);

        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of(existingMeleeWeapon));
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getName()).thenReturn(NAME);
        when(gamePlayer.getItemSlot(existingMeleeWeapon)).thenReturn(Optional.of(ITEM_SLOT));
        when(meleeWeapon.getResourceContainer()).thenReturn(pickedUpResourceContainer);

        DispatchResult result = interactionHandler.handlePickupItem(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
        assertThat(existingResourceContainer.getReserveAmount()).isEqualTo(expectedReserveAmount);

        verify(existingMeleeWeapon).update();
        verify(gamePlayer).setItem(ITEM_SLOT, existingItemStack);
    }

    @Test
    @DisplayName("handlePickupAction returns unhandled result when unable to find controller for weapon id")
    void handlePickupAction_controllerNotFound() {
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of());
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.empty());

        DispatchResult result = interactionHandler.handlePickupItem(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();

        verify(meleeWeapon, never()).assign(gamePlayer);
        verify(meleeWeapon, never()).onPickUp(gamePlayer);
    }

    @Test
    @DisplayName("handlePickupAction assigns existing melee weapon to player")
    void handlePickupAction_assignsExistingMeleeWeapon() {
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of());
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));

        DispatchResult result = interactionHandler.handlePickupItem(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();

        verify(meleeWeapon).assign(gamePlayer);
        verify(meleeWeapon).onPickUp(gamePlayer);
    }

    @Test
    @DisplayName("handlePickupAction does not add a single resource to an existing melee weapon when unable to find item slot")
    void handlePickupAction_addSingleResourceExistingMeleeWeaponItemSlotNotFound() {
        NamespacedKey weaponNameKey = MockUtils.createNamespacedKey("weapon-name");
        ResourceContainer resourceContainer = new ResourceContainer(1, 1, 2, 5);

        MeleeWeapon existingMeleeWeapon = mock(MeleeWeapon.class);
        when(existingMeleeWeapon.getName()).thenReturn(NAME);
        when(existingMeleeWeapon.getResourceContainer()).thenReturn(resourceContainer);

        ItemStack itemStack = mock(ItemStack.class, Mockito.RETURNS_DEEP_STUBS);
        when(itemStack.getItemMeta().getPersistentDataContainer().get(weaponNameKey, PersistentDataType.STRING)).thenReturn(NAME);

        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of(existingMeleeWeapon));
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(itemStack)).thenReturn(Optional.empty());
        when(gamePlayer.getItemSlot(existingMeleeWeapon)).thenReturn(Optional.empty());
        when(namespacedKeyCreator.create("weapon-name")).thenReturn(weaponNameKey);

        DispatchResult result = interactionHandler.handlePickupItem(gamePlayer, itemStack);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isFalse();

        verify(existingMeleeWeapon, never()).update();
        verify(gamePlayer, never()).setItem(anyInt(), any(ItemStack.class));
    }

    @Test
    @DisplayName("handlePickupAction adds a single resource to an existing melee weapon")
    void handlePickupAction_addsResourceToExistingMeleeWeapon() {
        ItemStack existingItemStack = new ItemStack(Material.IRON_SWORD);
        NamespacedKey weaponNameKey = MockUtils.createNamespacedKey("weapon-name");
        ResourceContainer resourceContainer = new ResourceContainer(1, 1, 2, 5);

        MeleeWeapon existingMeleeWeapon = mock(MeleeWeapon.class);
        when(existingMeleeWeapon.getName()).thenReturn(NAME);
        when(existingMeleeWeapon.getResourceContainer()).thenReturn(resourceContainer);
        when(existingMeleeWeapon.getItemStack()).thenReturn(existingItemStack);

        ItemStack itemStack = mock(ItemStack.class, Mockito.RETURNS_DEEP_STUBS);
        when(itemStack.getItemMeta().getPersistentDataContainer().get(weaponNameKey, PersistentDataType.STRING)).thenReturn(NAME);

        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of(existingMeleeWeapon));
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(itemStack)).thenReturn(Optional.empty());
        when(gamePlayer.getItemSlot(existingMeleeWeapon)).thenReturn(Optional.of(ITEM_SLOT));
        when(namespacedKeyCreator.create("weapon-name")).thenReturn(weaponNameKey);

        DispatchResult result = interactionHandler.handlePickupItem(gamePlayer, itemStack);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
        assertThat(resourceContainer.getReserveAmount()).isEqualTo(3);

        verify(existingMeleeWeapon).update();
        verify(gamePlayer).setItem(ITEM_SLOT, existingItemStack);
    }

    @Test
    @DisplayName("handlePickupAction returns unhandled result when picked up item has an unknown name")
    void handlePickupAction_newMeleeWeaponUnknownName() {
        NamespacedKey weaponNameKey = MockUtils.createNamespacedKey("weapon-name");

        ItemStack itemStack = mock(ItemStack.class, Mockito.RETURNS_DEEP_STUBS);
        when(itemStack.getItemMeta().getPersistentDataContainer().get(weaponNameKey, PersistentDataType.STRING)).thenReturn(NAME);

        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of());
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(itemStack)).thenReturn(Optional.empty());
        when(namespacedKeyCreator.create("weapon-name")).thenReturn(weaponNameKey);
        when(itemCreator.createMeleeWeapon(NAME, gamePlayer)).thenThrow(new ItemNotFoundException("error"));
        when(gamePlayer.getName()).thenReturn(PLAYER_NAME);

        DispatchResult result = interactionHandler.handlePickupItem(gamePlayer, itemStack);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();

        verify(logger).warning("Player TestPlayer picked up item with unrecognized melee weapon name 'Test Melee Weapon' - ignoring pickup");
        verify(meleeWeapon, never()).update();
        verify(gamePlayer, never()).addItem(any(ItemStack.class));
    }

    @Test
    @DisplayName("handlePickupAction returns unhandled result when unable to find controller of newly created melee weapon")
    void handlePickupAction_newMeleeWeaponControllerNotFound() {
        NamespacedKey weaponNameKey = MockUtils.createNamespacedKey("weapon-name");

        ItemStack itemStack = mock(ItemStack.class, Mockito.RETURNS_DEEP_STUBS);
        when(itemStack.getItemMeta().getPersistentDataContainer().get(weaponNameKey, PersistentDataType.STRING)).thenReturn(NAME);

        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of());
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(itemStack)).thenReturn(Optional.empty());
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(namespacedKeyCreator.create("weapon-name")).thenReturn(weaponNameKey);
        when(itemCreator.createMeleeWeapon(NAME, gamePlayer)).thenReturn(meleeWeapon);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.empty());

        DispatchResult result = interactionHandler.handlePickupItem(gamePlayer, itemStack);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();

        verify(meleeWeapon, never()).update();
        verify(gamePlayer, never()).addItem(any(ItemStack.class));
    }

    @Test
    @DisplayName("handlePickupAction creates a new melee weapon with 1 resource and assigns it to the player")
    void handlePickupAction_createsNewMeleeWeaponAssignment() {
        ItemStack newItemStack = new ItemStack(Material.IRON_SWORD);
        NamespacedKey weaponNameKey = MockUtils.createNamespacedKey("weapon-name");
        ResourceContainer resourceContainer = new ResourceContainer(1, 0, 2, 5);

        ItemStack itemStack = mock(ItemStack.class, Mockito.RETURNS_DEEP_STUBS);
        when(itemStack.getItemMeta().getPersistentDataContainer().get(weaponNameKey, PersistentDataType.STRING)).thenReturn(NAME);

        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of());
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(itemStack)).thenReturn(Optional.empty());
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(namespacedKeyCreator.create("weapon-name")).thenReturn(weaponNameKey);
        when(meleeWeapon.getResourceContainer()).thenReturn(resourceContainer);
        when(meleeWeapon.getItemStack()).thenReturn(newItemStack);
        when(itemCreator.createMeleeWeapon(NAME, gamePlayer)).thenReturn(meleeWeapon);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));

        DispatchResult result = interactionHandler.handlePickupItem(gamePlayer, itemStack);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
        assertThat(resourceContainer.getLoadedAmount()).isOne();
        assertThat(resourceContainer.getReserveAmount()).isZero();

        verify(controller).performActionNew(Action.PICKUP_ITEM, gamePlayer);
        verify(meleeWeapon).update();
        verify(gamePlayer).addItem(newItemStack);
    }

    @Test
    @DisplayName("handleRightClick returns display result with values from the item controller's action result")
    void handleRightClick_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));
        when(controller.performActionNew(Action.RIGHT_CLICK, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleRightClick(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("handleSwapFrom returns display result with values from the item controller's action result")
    void handleSwapFrom_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));
        when(controller.performActionNew(Action.SWAP_FROM, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleSwapFrom(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("handleSwapTo returns display result with values from the item controller's action result")
    void handleSwapTo_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));
        when(controller.performActionNew(Action.SWAP_TO, gamePlayer)).thenReturn(actionResult);

        DispatchResult result = interactionHandler.handleSwapTo(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }
}
