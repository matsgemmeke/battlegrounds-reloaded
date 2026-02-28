package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.action.PickupActionResult;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GunActionExecutorTest {

    private static final ItemStack ITEM_STACK = new ItemStack(Material.IRON_HOE);
    private static final UUID PLAYER_UNIQUE_ID = UUID.randomUUID();

    @Mock
    private GamePlayer gamePlayer;
    @Mock
    private GunRegistry gunRegistry;
    @Mock
    private Player player;
    @Mock
    private PlayerRegistry playerRegistry;

    @BeforeEach
    void setUp() {
        when(player.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);
    }

    @Test
    void handleChangeFromActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleChangeFromActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleChangeFromActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onChangeFrom();
    }

    @Test
    void handleChangeFromActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onChangeFrom();
    }

    @Test
    void handleChangeToActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleChangeToActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleChangeToActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onChangeTo();
    }

    @Test
    void handleChangeToActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onChangeTo();
    }

    @Test
    void handleDropItemActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleDropItemActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gunRegistry, never()).unassign(any(Gun.class));
    }

    @Test
    void handleDropItemActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onDrop();
        verify(gunRegistry, never()).unassign(gun);
    }

    @Test
    void handleDropItemActionCallsGunFunctionAndUnassignsGunWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onDrop();
        verify(gunRegistry).unassign(gun);
    }

    @Test
    @DisplayName("handlePickupAction does not perform pickup action when player is not registered")
    void handlePickupAction_playerNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        PickupActionResult result = actionExecutor.handlePickupAction(player, ITEM_STACK);

        assertThat(result.performAction()).isTrue();
        assertThat(result.removeItem()).isFalse();
    }

    @Test
    @DisplayName("handlePickupAction does not perform pickup action when item stack is not a registered gun")
    void handlePickupAction_itemStackNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getUnassignedGun(ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        PickupActionResult result = actionExecutor.handlePickupAction(player, ITEM_STACK);

        assertThat(result.performAction()).isTrue();
        assertThat(result.removeItem()).isFalse();

        verify(gunRegistry, never()).assign(any(Gun.class), any(GamePlayer.class));
    }

    @Test
    @DisplayName("handlePickupAction performs pickup action")
    void handlePickupAction_performsAction() {
        Gun gun = mock(Gun.class);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getUnassignedGun(ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        PickupActionResult result = actionExecutor.handlePickupAction(player, ITEM_STACK);

        assertThat(result.performAction()).isTrue();
        assertThat(result.removeItem()).isFalse();

        verify(gun).onPickUp(gamePlayer);
        verify(gunRegistry).assign(gun, gamePlayer);
    }

    @Test
    void handleLeftClickActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleLeftClickActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleLeftClickActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onLeftClick();
    }

    @Test
    void handleLeftClickActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(gun).onLeftClick();
    }

    @Test
    void handleRightClickActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleRightClickActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleRightClickActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onRightClick();
    }

    @Test
    void handleRightClickActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(gun).onRightClick();
    }

    @Test
    void handleSwapFromActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleSwapFromActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleSwapFromActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onSwapFrom();
    }

    @Test
    void handleSwapFromActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(gun).onSwapFrom();
    }

    @Test
    void handleSwapToActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleSwapToActionDoesNothingWhenNoGunMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleSwapToActionDoesNothingWhenGunHolderDoesNotMatch() {
        GunHolder otherHolder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onSwapTo();
    }

    @Test
    void handleSwapToActionCallsGunFunctionWhenMatchingGunIsFound() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onSwapTo();
    }
}
