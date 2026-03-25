package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.action.PickupActionResult;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
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
    @InjectMocks
    private GunActionExecutor actionExecutor;

    @BeforeEach
    void setUp() {
        when(player.getUniqueId()).thenReturn(PLAYER_UNIQUE_ID);
    }

    @Test
    @DisplayName("handleChangeFromAction does nothing when given player is not registered")
    void handleChangeFromAction_playerNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleChangeFromAction does nothing when no gun matches with given user and item stack")
    void handleChangeFromAction_noMatchFound() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleChangeFromAction does nothing when gun user does not match")
    void handleChangeFromAction_differentUser() {
        GunUser otherUser = mock(GunUser.class);

        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(otherUser);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onChangeFrom();
    }

    @Test
    @DisplayName("handleChangeFromAction calls gun function when match is found")
    void handleChangeFromAction_matchingGun() {
        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onChangeFrom();
    }

    @Test
    @DisplayName("handleChangeToAction does nothing when given player is not registered")
    void handleChangeToAction_playerNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleChangeToAction does nothing when no gun matches with given user and item stack")
    void handleChangeToAction_noMatchFound() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleChangeToAction does nothing when gun user does not match")
    void handleChangeToAction_differentUser() {
        GunUser otherUser = mock(GunUser.class);

        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(otherUser);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onChangeTo();
    }

    @Test
    @DisplayName("handleChangeToAction calls gun function when matching gun is found")
    void handleChangeToAction_matchingGun() {
        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onChangeTo();
    }

    @Test
    @DisplayName("handleDropItemAction does nothing when given player is not registered")
    void handleDropItemAction_playerNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleDropItemAction does nothing when no gun matches with given user and item stack")
    void handleDropItemAction_noMatchFound() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gunRegistry, never()).unassign(any(Gun.class));
    }

    @Test
    @DisplayName("handleDropItemAction does nothing when gun user does not match")
    void handleDropItemAction_differentUser() {
        GunUser otherUser = mock(GunUser.class);

        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(otherUser);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onDrop();
        verify(gunRegistry, never()).unassign(gun);
    }

    @Test
    @DisplayName("handleDropItemAction calls gun function and unassigns gun when match is found")
    void handleDropItemAction_matchingGun() {
        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onDrop();
        verify(gunRegistry).unassign(gun);
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
    @DisplayName("handlePickupAction does not perform pickup action when item stack is not a registered gun")
    void handlePickupAction_itemStackNotRegistered() {
        Item item = mock(Item.class);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getUnassignedGun(ITEM_STACK)).thenReturn(Optional.empty());

        PickupActionResult result = actionExecutor.handlePickupAction(player, ITEM_STACK);
        result.itemAction().accept(item);

        assertThat(result.performAction()).isTrue();

        verify(gunRegistry, never()).assign(any(Gun.class), any(GamePlayer.class));
        verifyNoInteractions(item);
    }

    @Test
    @DisplayName("handlePickupAction performs pickup action")
    void handlePickupAction_performsAction() {
        Gun gun = mock(Gun.class);
        Item item = mock(Item.class);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getUnassignedGun(ITEM_STACK)).thenReturn(Optional.of(gun));

        PickupActionResult result = actionExecutor.handlePickupAction(player, ITEM_STACK);
        result.itemAction().accept(item);

        assertThat(result.performAction()).isTrue();

        verify(gun).onPickUp(gamePlayer);
        verify(gunRegistry).assign(gun, gamePlayer);
        verifyNoInteractions(item);
    }

    @Test
    @DisplayName("handleLeftClickAction does nothing when given player is not registered")
    void handleLeftClickAction_playerNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleLeftClickAction does nothing when no gun matches with given user and item stack")
    void handleLeftClickAction_noMatchFound() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleLeftClickAction does nothing when gun user does not match")
    void handleLeftClickAction_differentUser() {
        GunUser otherUser = mock(GunUser.class);

        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(otherUser);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onLeftClick();
    }

    @Test
    @DisplayName("handleLeftClickAction calls gun function when matching gun is found")
    void handleLeftClickAction_matchingGun() {
        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(gun).onLeftClick();
    }

    @Test
    @DisplayName("handleRightClickAction does nothing when given player is not registered")
    void handleRightClickAction_playerNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleRightClickAction does nothing when no gun matches with given user and item stack")
    void handleRightClickAction_noMatchFound() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleRightClickAction does nothing when gun user does not match")
    void handleRightClickAction_differentUser() {
        GunUser otherUser = mock(GunUser.class);

        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(otherUser);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onRightClick();
    }

    @Test
    @DisplayName("handleRightClickAction calls gun function when matching gun is found")
    void handleRightClickAction_matchingGun() {
        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(gun).onRightClick();
    }

    @Test
    @DisplayName("handleSwapFromAction does nothing when given player is not registered")
    void handleSwapFromAction_playerNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleSwapFromAction does nothing when no gun matches with given user and item stack")
    void handleSwapFromAction_noMatchFound() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleSwapFromAction does nothing when gun user does not match")
    void handleSwapFromAction_differentUser() {
        GunUser otherUser = mock(GunUser.class);

        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(otherUser);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onSwapFrom();
    }

    @Test
    @DisplayName("handleSwapFromAction calls gun function when matching gun is found")
    void handleSwapFromAction_matchingGun() {
        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(gun).onSwapFrom();
    }

    @Test
    @DisplayName("handleSwapToAction does nothing when given player is not registered")
    void handleSwapToAction_playerNotRegistered() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleSwapToAction does nothing when no gun matches with given user and item stack")
    void handleSwapToAction_noMatchFound() {
        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleSwapToAction does nothing when gun user does not match")
    void handleSwapToAction_differentUser() {
        GunUser otherUser = mock(GunUser.class);

        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(otherUser);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun, never()).onSwapTo();
    }

    @Test
    @DisplayName("handleSwapToAction calls gun function when matching gun is found")
    void handleSwapToAction_matchingGun() {
        Gun gun = mock(Gun.class);
        when(gun.getUser()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(PLAYER_UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));

        GunActionExecutor actionExecutor = new GunActionExecutor(gunRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(gun).onSwapTo();
    }
}
