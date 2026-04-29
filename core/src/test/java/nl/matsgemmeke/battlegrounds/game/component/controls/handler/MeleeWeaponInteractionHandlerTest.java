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
    @DisplayName("resolve returns optional with corresponding melee weapon when given combination of player and item stack is registered")
    void resolve_playerAndItemStackRegistered() {
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        Optional<MeleeWeapon> meleeWeaponOptional = interactionHandler.resolve(gamePlayer, ITEM_STACK);

        assertThat(meleeWeaponOptional).isEmpty();
    }

    @Test
    @DisplayName("resolve returns empty optional when given combination of player and item stack is not registered")
    void resolve_playerAndItemStackNotRegistered() {
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        Optional<MeleeWeapon> meleeWeaponOptional = interactionHandler.resolve(gamePlayer, ITEM_STACK);

        assertThat(meleeWeaponOptional).hasValue(meleeWeapon);
    }

    @Test
    @DisplayName("dispatch returns unhandled display result when item controller cannot be found")
    void dispatch_itemControllerNotFound() {
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.empty());

        DispatchResult result = interactionHandler.dispatch(meleeWeapon, gamePlayer, Action.LEFT_CLICK);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("dispatch returns display result with values from the item controller's action result")
    void dispatch_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        ItemController<MeleeWeaponUser> controller = mock();
        when(controller.performActionNew(Action.LEFT_CLICK, gamePlayer)).thenReturn(actionResult);

        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
        when(itemControllerRegistry.getMeleeWeaponController(MELEE_WEAPON_ID)).thenReturn(Optional.of(controller));

        DispatchResult result = interactionHandler.dispatch(meleeWeapon, gamePlayer, Action.LEFT_CLICK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }
}
