package nl.matsgemmeke.battlegrounds.item.melee;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.item.action.PickupActionResult;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    private MeleeWeaponRegistry meleeWeaponRegistry;
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
    void handleChangeFromActionReturnsTrueAndDoesNotPerformActionWhenMeleeWeaponHolderDoesNotMatchWithGamePlayer() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getHolder()).thenReturn(Optional.empty());

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
        when(meleeWeapon.getHolder()).thenReturn(Optional.of(gamePlayer));

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
    void handleChangeToActionReturnsTrueAndDoesNotPerformActionWhenMeleeWeaponHolderDoesNotMatchWithGamePlayer() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getHolder()).thenReturn(Optional.empty());

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
        when(meleeWeapon.getHolder()).thenReturn(Optional.of(gamePlayer));

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
    void handleDropItemActionReturnsTrueAndDoesNotPerformActionWhenMeleeWeaponHolderDoesNotMatchWithGamePlayer() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getHolder()).thenReturn(Optional.empty());

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(meleeWeapon, never()).onDrop();
        verify(meleeWeaponRegistry, never()).unassign(any(MeleeWeapon.class));
    }

    @Test
    void handleDropItemActionReturnsTrueAndPerformsActionAndUnassignsMeleeWeapon() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getHolder()).thenReturn(Optional.of(gamePlayer));

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(meleeWeapon).onDrop();
        verify(meleeWeaponRegistry).unassign(meleeWeapon);
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
    void handleLeftClickActionReturnsTrueAndDoesNotPerformActionWhenMeleeWeaponHolderDoesNotMatchWithGamePlayer() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getHolder()).thenReturn(Optional.empty());

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
        when(meleeWeapon.getHolder()).thenReturn(Optional.of(gamePlayer));

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(meleeWeapon).onLeftClick();
    }

    @Test
    @DisplayName("handlePickupAction does not perform pickup action when player is not registered")
    void handlePickupAction_playerNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        PickupActionResult result = actionExecutor.handlePickupAction(player, ITEM_STACK);

        assertThat(result.performAction()).isTrue();
        assertThat(result.removeItem()).isFalse();
    }

    @Test
    @DisplayName("handlePickupAction does not perform pickup action when item stack is not a registered melee weapon")
    void handlePickupAction_meleeWeaponNotRegistered() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getUnassignedMeleeWeapon(ITEM_STACK)).thenReturn(Optional.empty());

        PickupActionResult result = actionExecutor.handlePickupAction(player, ITEM_STACK);

        assertThat(result.performAction()).isTrue();
        assertThat(result.removeItem()).isFalse();
    }

    @Test
    @DisplayName("handlePickupAction does not resupply existing melee weapon when unable to find item slot for existing melee weapon")
    void handlePickupAction_existingMeleeWeaponItemSlotNotFound() {
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

        assertThat(result.performAction()).isFalse();
        assertThat(result.removeItem()).isTrue();

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

        assertThat(result.performAction()).isFalse();
        assertThat(result.removeItem()).isTrue();
        assertThat(existingResourceContainer.getReserveAmount()).isEqualTo(expectedReserveAmount);

        verify(existingMeleeWeapon).update();
        verify(gamePlayer).setItem(ITEM_SLOT, existingItemStack);
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
    void handleRightClickActionReturnsTrueAndDoesNotPerformActionWhenMeleeWeaponHolderDoesNotMatchWithGamePlayer() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getHolder()).thenReturn(Optional.empty());

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
        when(meleeWeapon.getHolder()).thenReturn(Optional.of(gamePlayer));

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
    void handleSwapFromActionReturnsTrueAndDoesNotPerformActionWhenMeleeWeaponHolderDoesNotMatchWithGamePlayer() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getHolder()).thenReturn(Optional.empty());

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
        when(meleeWeapon.getHolder()).thenReturn(Optional.of(gamePlayer));

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
    void handleSwapToActionReturnsTrueAndDoesNotPerformActionWhenMeleeWeaponHolderDoesNotMatchWithGamePlayer() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getHolder()).thenReturn(Optional.empty());

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
        when(meleeWeapon.getHolder()).thenReturn(Optional.of(gamePlayer));

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(meleeWeapon).onSwapTo();
    }
}
