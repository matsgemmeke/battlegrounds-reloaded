package nl.matsgemmeke.battlegrounds.game.component.controls.handler;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.DispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemControllerRegistry;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    private ItemControllerRegistry itemControllerRegistry;
    @InjectMocks
    private GunInteractionHandler interactionHandler;

    @Test
    @DisplayName("handleInteraction returns unhandled dispatch result when given combination of player and item stack is not registered")
    void handleInteraction_playerAndItemStackNotRegistered() {
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        DispatchResult result = interactionHandler.handleInteraction(gamePlayer, ITEM_STACK, Action.LEFT_CLICK);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("handleInteraction returns unhandled dispatch result when item controller cannot be found")
    void handleInteraction_itemControllerNotFound() {
        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));
        when(gun.getId()).thenReturn(GUN_ID);
        when(itemControllerRegistry.getGunController(GUN_ID)).thenReturn(Optional.empty());

        DispatchResult result = interactionHandler.handleInteraction(gamePlayer, ITEM_STACK, Action.LEFT_CLICK);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("handleInteraction returns display result with values from the item controller's action result")
    void handleInteraction_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        ItemController<GunUser> controller = mock();
        when(controller.performActionNew(Action.LEFT_CLICK, gamePlayer)).thenReturn(actionResult);

        when(gunRegistry.getAssignedGun(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(gun));
        when(gun.getId()).thenReturn(GUN_ID);
        when(itemControllerRegistry.getGunController(GUN_ID)).thenReturn(Optional.of(controller));

        DispatchResult result = interactionHandler.handleInteraction(gamePlayer, ITEM_STACK, Action.LEFT_CLICK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }
}
