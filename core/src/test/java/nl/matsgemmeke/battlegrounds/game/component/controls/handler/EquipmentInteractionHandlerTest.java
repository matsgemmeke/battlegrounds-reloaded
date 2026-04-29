package nl.matsgemmeke.battlegrounds.game.component.controls.handler;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.DispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemControllerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionResult;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;
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
class EquipmentInteractionHandlerTest {

    private static final ItemStack ITEM_STACK = new ItemStack(Material.IRON_HOE);
    private static final UUID EQUIPMENT_ID = UUID.randomUUID();

    @Mock
    private Equipment equipment;
    @Mock
    private EquipmentRegistry equipmentRegistry;
    @Mock
    private GamePlayer gamePlayer;
    @Mock
    private ItemControllerRegistry itemControllerRegistry;
    @InjectMocks
    private EquipmentInteractionHandler interactionHandler;

    @Test
    @DisplayName("resolve returns optional with corresponding equipment when given combination of player and item stack is registered")
    void resolve_playerAndItemStackRegistered() {
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        Optional<Equipment> equipmentOptional = interactionHandler.resolve(gamePlayer, ITEM_STACK);

        assertThat(equipmentOptional).isEmpty();
    }

    @Test
    @DisplayName("resolve returns empty optional when given combination of player and item stack is not registered")
    void resolve_playerAndItemStackNotRegistered() {
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        Optional<Equipment> equipmentOptional = interactionHandler.resolve(gamePlayer, ITEM_STACK);

        assertThat(equipmentOptional).hasValue(equipment);
    }

    @Test
    @DisplayName("dispatch returns unhandled display result when item controller cannot be found")
    void dispatch_itemControllerNotFound() {
        when(equipment.getId()).thenReturn(EQUIPMENT_ID);
        when(itemControllerRegistry.getEquipmentController(EQUIPMENT_ID)).thenReturn(Optional.empty());

        DispatchResult result = interactionHandler.dispatch(equipment, gamePlayer, Action.LEFT_CLICK);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("dispatch returns display result with values from the item controller's action result")
    void dispatch_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        ItemController<EquipmentUser> controller = mock();
        when(controller.performActionNew(Action.LEFT_CLICK, gamePlayer)).thenReturn(actionResult);

        when(equipment.getId()).thenReturn(EQUIPMENT_ID);
        when(itemControllerRegistry.getEquipmentController(EQUIPMENT_ID)).thenReturn(Optional.of(controller));

        DispatchResult result = interactionHandler.dispatch(equipment, gamePlayer, Action.LEFT_CLICK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }
}
