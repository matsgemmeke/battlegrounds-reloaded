package nl.matsgemmeke.battlegrounds.item.melee;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.ItemCreator;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.item.action.PickupActionResult;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.BeforeEach;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeleeWeaponActionExecutorTest {

    private static final ItemStack ITEM_STACK = new ItemStack(Material.STICK);
    private static final UUID PLAYER_UNIQUE_ID = UUID.randomUUID();
    private static final String NAME = "Test Melee Weapon";
    private static final int ITEM_SLOT = 5;

    @Mock
    private ItemCreator itemCreator;
    @Mock
    private MeleeWeaponRegistry meleeWeaponRegistry;
    @Mock
    private NamespacedKeyCreator namespacedKeyCreator;
    @Mock
    private Player player;
    @Mock
    private PlayerRegistry playerRegistry;
    @InjectMocks
    private MeleeWeaponActionExecutor actionExecutor;

    @BeforeEach
    void setUp() {
        when(player.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);
    }

    @Test
    void handleChangeFromActionReturnsTrueAndDoesNotPerformActionWhenPlayerUniqueIdIsNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleChangeFromActionReturnsTrueAndDoesNotPerformActionWhenItemStackIsNoRegisteredMeleeWeapon() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleChangeFromAction returns true and does not perform action when melee weapon user does not match with player")
    void handleChangeFromAction_differentUser() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getUser()).thenReturn(Optional.empty());

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(meleeWeapon, never()).onChangeFrom();
    }

    @Test
    void handleChangeFromActionReturnsTrueAndPerformsAction() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getUser()).thenReturn(Optional.of(gamePlayer));

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(meleeWeapon).onChangeFrom();
    }

    @Test
    void handleChangeToActionReturnsTrueAndDoesNotPerformActionWhenPlayerUniqueIdIsNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleChangeToActionReturnsTrueAndDoesNotPerformActionWhenItemStackIsNoRegisteredMeleeWeapon() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleChangeToAction returns true and does not perform action when melee weapon user does not match with player")
    void handleChangeToAction_differentUser() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getUser()).thenReturn(Optional.empty());

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(meleeWeapon, never()).onChangeTo();
    }

    @Test
    void handleChangeToActionReturnsTrueAndPerformsAction() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getUser()).thenReturn(Optional.of(gamePlayer));

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(meleeWeapon).onChangeTo();
    }

    @Test
    void handleDropItemActionReturnsTrueAndDoesNotPerformActionWhenPlayerUniqueIdIsNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleDropItemActionReturnsTrueAndDoesNotPerformActionWhenItemStackIsNoRegisteredMeleeWeapon() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleDropItemAction returns true and does not perform action when melee weapon user does not match with player")
    void handleDropItemAction_differentUser() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getUser()).thenReturn(Optional.empty());

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(meleeWeapon, never()).onDrop();
        verify(meleeWeapon, never()).unassign();
    }

    @Test
    void handleDropItemActionReturnsTrueAndPerformsActionAndUnassignsMeleeWeapon() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getUser()).thenReturn(Optional.of(gamePlayer));

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(meleeWeapon).onDrop();
        verify(meleeWeapon).unassign();
    }

    @Test
    void handleLeftClickActionReturnsTrueAndDoesNotPerformActionWhenPlayerUniqueIdIsNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleLeftClickActionReturnsTrueAndDoesNotPerformActionWhenItemStackIsNoRegisteredMeleeWeapon() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleLeftClickAction returns true and does not perform action when melee weapon user does not match with player")
    void handleLeftClickAction_differentUser() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getUser()).thenReturn(Optional.empty());

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(meleeWeapon, never()).onLeftClick();
    }

    @Test
    void handleLeftClickActionReturnsFalseAndPerformsAction() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getUser()).thenReturn(Optional.of(gamePlayer));

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(meleeWeapon).onLeftClick();
    }

    @Test
    @DisplayName("handlePickupAction does not perform pickup action when player is not registered")
    void handlePickupAction_playerNotRegistered() {
        Item item = mock(Item.class);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        PickupActionResult result = actionExecutor.handlePickupAction(player, ITEM_STACK);
        result.itemAction().accept(item);

        assertThat(result.performAction()).isTrue();

        verifyNoInteractions(item);
    }

    @Test
    @DisplayName("handlePickupAction does not resupply existing melee weapon when unable to find item slot for existing melee weapon")
    void handlePickupAction_resupplyExistingMeleeWeaponItemSlotNotFound() {
        Item item = mock(Item.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getName()).thenReturn(NAME);

        MeleeWeapon existingMeleeWeapon = mock(MeleeWeapon.class);
        when(existingMeleeWeapon.getName()).thenReturn(NAME);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getItemSlot(existingMeleeWeapon)).thenReturn(Optional.empty());

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of(existingMeleeWeapon));
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        PickupActionResult result = actionExecutor.handlePickupAction(player, ITEM_STACK);
        result.itemAction().accept(item);

        assertThat(result.performAction()).isFalse();

        verify(existingMeleeWeapon, never()).update();
        verify(gamePlayer, never()).setItem(anyInt(), any(ItemStack.class));
        verify(item).remove();
    }

    @ParameterizedTest
    @CsvSource({ "2,2", "100,3" })
    @DisplayName("handlePickupAction supplies existing melee weapon with resources from picked up melee weapon")
    void handlePickupAction_resuppliesExistingMeleeWeapon(int pickedUpReserveAmount, int expectedReserveAmount) {
        ItemStack existingItemStack = new ItemStack(Material.IRON_SWORD);
        ResourceContainer pickedUpResourceContainer = new ResourceContainer(1, 0, pickedUpReserveAmount, 2);
        ResourceContainer existingResourceContainer = new ResourceContainer(1, 1, 0, 3);
        Item item = mock(Item.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getName()).thenReturn(NAME);
        when(meleeWeapon.getResourceContainer()).thenReturn(pickedUpResourceContainer);

        MeleeWeapon existingMeleeWeapon = mock(MeleeWeapon.class);
        when(existingMeleeWeapon.getName()).thenReturn(NAME);
        when(existingMeleeWeapon.getResourceContainer()).thenReturn(existingResourceContainer);
        when(existingMeleeWeapon.getItemStack()).thenReturn(existingItemStack);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getItemSlot(existingMeleeWeapon)).thenReturn(Optional.of(ITEM_SLOT));

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of(existingMeleeWeapon));
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        PickupActionResult result = actionExecutor.handlePickupAction(player, ITEM_STACK);
        result.itemAction().accept(item);

        assertThat(result.performAction()).isFalse();
        assertThat(existingResourceContainer.getReserveAmount()).isEqualTo(expectedReserveAmount);

        verify(existingMeleeWeapon).update();
        verify(gamePlayer).setItem(ITEM_SLOT, existingItemStack);
        verify(item).remove();
    }

    @Test
    @DisplayName("handlePickupAction assigns existing melee weapon to player")
    void handlePickupAction_assignsExistingMeleeWeapon() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        Item item = mock(Item.class);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of());
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        PickupActionResult result = actionExecutor.handlePickupAction(player, ITEM_STACK);
        result.itemAction().accept(item);

        assertThat(result.performAction()).isTrue();

        verify(meleeWeapon).assign(gamePlayer);
        verify(meleeWeapon).onPickUp(gamePlayer);
        verify(item).remove();
    }

    @Test
    @DisplayName("handlePickupAction does not add a single resource to an existing melee weapon when unable to find item slot")
    void handlePickupAction_addSingleResourceExistingMeleeWeaponItemSlotNotFound() {
        NamespacedKey weaponNameKey = MockUtils.createNamespacedKey("weapon-name");
        ResourceContainer resourceContainer = new ResourceContainer(1, 1, 2, 5);
        Item item = mock(Item.class);

        MeleeWeapon existingMeleeWeapon = mock(MeleeWeapon.class);
        when(existingMeleeWeapon.getName()).thenReturn(NAME);
        when(existingMeleeWeapon.getResourceContainer()).thenReturn(resourceContainer);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getItemSlot(existingMeleeWeapon)).thenReturn(Optional.empty());

        ItemStack itemStack = mock(ItemStack.class, Mockito.RETURNS_DEEP_STUBS);
        when(itemStack.getItemMeta().getPersistentDataContainer().get(weaponNameKey, PersistentDataType.STRING)).thenReturn(NAME);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of(existingMeleeWeapon));
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(itemStack)).thenReturn(Optional.empty());
        when(namespacedKeyCreator.create("weapon-name")).thenReturn(weaponNameKey);

        PickupActionResult result = actionExecutor.handlePickupAction(player, itemStack);
        result.itemAction().accept(item);

        assertThat(result.performAction()).isFalse();

        verify(existingMeleeWeapon, never()).update();
        verify(gamePlayer, never()).setItem(anyInt(), any(ItemStack.class));
        verify(item).remove();
    }

    @Test
    @DisplayName("handlePickupAction adds a single resource to an existing melee weapon")
    void handlePickupAction_addsResourceToExistingMeleeWeapon() {
        ItemStack existingItemStack = new ItemStack(Material.IRON_SWORD);
        NamespacedKey weaponNameKey = MockUtils.createNamespacedKey("weapon-name");
        ResourceContainer resourceContainer = new ResourceContainer(1, 1, 2, 5);
        Item item = mock(Item.class);

        MeleeWeapon existingMeleeWeapon = mock(MeleeWeapon.class);
        when(existingMeleeWeapon.getName()).thenReturn(NAME);
        when(existingMeleeWeapon.getResourceContainer()).thenReturn(resourceContainer);
        when(existingMeleeWeapon.getItemStack()).thenReturn(existingItemStack);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getItemSlot(existingMeleeWeapon)).thenReturn(Optional.of(ITEM_SLOT));

        ItemStack itemStack = mock(ItemStack.class, Mockito.RETURNS_DEEP_STUBS);
        when(itemStack.getItemMeta().getPersistentDataContainer().get(weaponNameKey, PersistentDataType.STRING)).thenReturn(NAME);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of(existingMeleeWeapon));
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(itemStack)).thenReturn(Optional.empty());
        when(namespacedKeyCreator.create("weapon-name")).thenReturn(weaponNameKey);

        PickupActionResult result = actionExecutor.handlePickupAction(player, itemStack);
        result.itemAction().accept(item);

        assertThat(result.performAction()).isFalse();
        assertThat(resourceContainer.getReserveAmount()).isEqualTo(3);

        verify(existingMeleeWeapon).update();
        verify(gamePlayer).setItem(ITEM_SLOT, existingItemStack);
        verify(item).remove();
    }

    @Test
    @DisplayName("handlePickupAction creates a new melee weapon with 1 resource and assigns it to the player")
    void handlePickupAction_createsNewMeleeWeaponAssignment() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemStack newItemStack = new ItemStack(Material.IRON_SWORD);
        NamespacedKey weaponNameKey = MockUtils.createNamespacedKey("weapon-name");
        ResourceContainer resourceContainer = new ResourceContainer(1, 0, 2, 5);
        Item item = mock(Item.class);

        ItemStack itemStack = mock(ItemStack.class, Mockito.RETURNS_DEEP_STUBS);
        when(itemStack.getItemMeta().getPersistentDataContainer().get(weaponNameKey, PersistentDataType.STRING)).thenReturn(NAME);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getResourceContainer()).thenReturn(resourceContainer);
        when(meleeWeapon.getItemStack()).thenReturn(newItemStack);

        when(itemCreator.createMeleeWeapon(NAME, gamePlayer)).thenReturn(meleeWeapon);
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapons(gamePlayer)).thenReturn(List.of());
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(itemStack)).thenReturn(Optional.empty());
        when(namespacedKeyCreator.create("weapon-name")).thenReturn(weaponNameKey);

        PickupActionResult result = actionExecutor.handlePickupAction(player, itemStack);
        result.itemAction().accept(item);

        assertThat(result.performAction()).isFalse();
        assertThat(resourceContainer.getLoadedAmount()).isOne();
        assertThat(resourceContainer.getReserveAmount()).isZero();

        verify(meleeWeapon).update();
        verify(gamePlayer).addItem(newItemStack);
        verify(item).remove();
    }

    @Test
    void handleRightClickActionReturnsTrueAndDoesNotPerformActionWhenPlayerUniqueIdIsNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleRightClickActionReturnsTrueAndDoesNotPerformActionWhenItemStackIsNoRegisteredMeleeWeapon() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleRightClickAction returns true and does not perform action when melee weapon user does not match with player")
    void handleRightClickAction_differentUser() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getUser()).thenReturn(Optional.empty());

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(meleeWeapon, never()).onRightClick();
    }

    @Test
    void handleRightClickActionReturnsFalseAndPerformsAction() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getUser()).thenReturn(Optional.of(gamePlayer));

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(meleeWeapon).onRightClick();
    }

    @Test
    void handleSwapFromActionReturnsTrueAndDoesNotPerformActionWhenPlayerUniqueIdIsNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleSwapFromActionReturnsTrueAndDoesNotPerformActionWhenItemStackIsNoRegisteredMeleeWeapon() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleSwapFromAction returns true and does not perform action when melee weapon user does not match with player")
    void handleSwapFromAction_differentUser() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getUser()).thenReturn(Optional.empty());

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(meleeWeapon, never()).onSwapFrom();
    }

    @Test
    void handleSwapFromActionReturnsFalseAndPerformsAction() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getUser()).thenReturn(Optional.of(gamePlayer));

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(meleeWeapon).onSwapFrom();
    }

    @Test
    void handleSwapToActionReturnsTrueAndDoesNotPerformActionWhenPlayerUniqueIdIsNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleSwapToActionReturnsTrueAndDoesNotPerformActionWhenItemStackIsNoRegisteredMeleeWeapon() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleSwapToAction returns true and does not perform action when melee weapon user does not match with player")
    void handleSwapToAction_differentUser() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getUser()).thenReturn(Optional.empty());

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(meleeWeapon, never()).onSwapTo();
    }

    @Test
    void handleSwapToActionReturnsFalseAndPerformsAction() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getUser()).thenReturn(Optional.of(gamePlayer));

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(meleeWeapon).onSwapTo();
    }
}
