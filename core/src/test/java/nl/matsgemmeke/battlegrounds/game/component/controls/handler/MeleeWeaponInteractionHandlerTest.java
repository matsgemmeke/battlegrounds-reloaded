package nl.matsgemmeke.battlegrounds.game.component.controls.handler;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.DispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemControllerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionResult;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;
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
class MeleeWeaponInteractionHandlerTest {

    private static final ItemStack ITEM_STACK = new ItemStack(Material.IRON_HOE);
    private static final UUID MELEE_WEAPON_ID = UUID.randomUUID();

    @Mock
    private GamePlayer gamePlayer;
    @Mock
    private ItemControllerRegistry itemControllerRegistry;
    @Mock
    private MeleeWeapon meleeWeapon;
    @Mock
    private MeleeWeaponRegistry meleeWeaponRegistry;
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

        ItemController<MeleeWeaponUser> controller = mock();
        when(controller.performActionNew(Action.CHANGE_FROM, gamePlayer)).thenReturn(actionResult);

        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));

        DispatchResult result = interactionHandler.handleChangeFrom(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }
}
