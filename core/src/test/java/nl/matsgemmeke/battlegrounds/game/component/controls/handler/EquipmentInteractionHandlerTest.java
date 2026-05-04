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
import static org.mockito.Mockito.*;

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
    @DisplayName("handleChangeFrom returns unhandled dispatch result when given combination of player and item stack is not registered")
    void handleChangeFrom_playerAndItemStackNotRegistered() {
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        DispatchResult result = interactionHandler.handleChangeFrom(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("handleChangeFrom returns unhandled dispatch result when item controller cannot be found")
    void handleChangeFrom_itemControllerNotFound() {
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));
        when(equipment.getId()).thenReturn(EQUIPMENT_ID);
        when(itemControllerRegistry.getEquipmentController(EQUIPMENT_ID)).thenReturn(Optional.empty());

        DispatchResult result = interactionHandler.handleChangeFrom(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isFalse();
        assertThat(result.cancelEvent()).isFalse();
    }

    @Test
    @DisplayName("handleChangeFrom returns display result with values from the item controller's action result")
    void handleChangeFrom_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        ItemController<EquipmentUser> controller = mock();
        when(controller.performActionNew(Action.CHANGE_FROM, gamePlayer)).thenReturn(actionResult);

        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));
        when(equipment.getId()).thenReturn(EQUIPMENT_ID);
        when(itemControllerRegistry.getEquipmentController(EQUIPMENT_ID)).thenReturn(Optional.of(controller));

        DispatchResult result = interactionHandler.handleChangeFrom(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("handleChangeTo returns display result with values from the item controller's action result")
    void handleChangeTo_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        ItemController<EquipmentUser> controller = mock();
        when(controller.performActionNew(Action.CHANGE_TO, gamePlayer)).thenReturn(actionResult);

        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));
        when(equipment.getId()).thenReturn(EQUIPMENT_ID);
        when(itemControllerRegistry.getEquipmentController(EQUIPMENT_ID)).thenReturn(Optional.of(controller));

        DispatchResult result = interactionHandler.handleChangeTo(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("handleDropItem returns display result with values from the item controller's action result")
    void handleDropItem_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        ItemController<EquipmentUser> controller = mock();
        when(controller.performActionNew(Action.DROP_ITEM, gamePlayer)).thenReturn(actionResult);

        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));
        when(equipment.getId()).thenReturn(EQUIPMENT_ID);
        when(itemControllerRegistry.getEquipmentController(EQUIPMENT_ID)).thenReturn(Optional.of(controller));

        DispatchResult result = interactionHandler.handleDropItem(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();

        verify(equipment).setUser(null);
    }

    @Test
    @DisplayName("handleLeftClick returns display result with values from the item controller's action result")
    void handleLeftClick_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        ItemController<EquipmentUser> controller = mock();
        when(controller.performActionNew(Action.LEFT_CLICK, gamePlayer)).thenReturn(actionResult);

        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));
        when(equipment.getId()).thenReturn(EQUIPMENT_ID);
        when(itemControllerRegistry.getEquipmentController(EQUIPMENT_ID)).thenReturn(Optional.of(controller));

        DispatchResult result = interactionHandler.handleLeftClick(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("handlePickupItem returns display result with values from the item controller's action result")
    void handlePickupItem_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        ItemController<EquipmentUser> controller = mock();
        when(controller.performActionNew(Action.PICKUP_ITEM, gamePlayer)).thenReturn(actionResult);

        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));
        when(equipment.getId()).thenReturn(EQUIPMENT_ID);
        when(itemControllerRegistry.getEquipmentController(EQUIPMENT_ID)).thenReturn(Optional.of(controller));

        DispatchResult result = interactionHandler.handlePickupItem(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();

        verify(equipment).setUser(gamePlayer);
    }

    @Test
    @DisplayName("handleRightClick returns display result with values from the item controller's action result")
    void handleRightClick_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        ItemController<EquipmentUser> controller = mock();
        when(controller.performActionNew(Action.RIGHT_CLICK, gamePlayer)).thenReturn(actionResult);

        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));
        when(equipment.getId()).thenReturn(EQUIPMENT_ID);
        when(itemControllerRegistry.getEquipmentController(EQUIPMENT_ID)).thenReturn(Optional.of(controller));

        DispatchResult result = interactionHandler.handleRightClick(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("handleSwapFrom returns display result with values from the item controller's action result")
    void handleSwapFrom_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        ItemController<EquipmentUser> controller = mock();
        when(controller.performActionNew(Action.SWAP_FROM, gamePlayer)).thenReturn(actionResult);

        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));
        when(equipment.getId()).thenReturn(EQUIPMENT_ID);
        when(itemControllerRegistry.getEquipmentController(EQUIPMENT_ID)).thenReturn(Optional.of(controller));

        DispatchResult result = interactionHandler.handleSwapFrom(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }

    @Test
    @DisplayName("handleSwapTo returns display result with values from the item controller's action result")
    void handleSwapTo_successful() {
        ActionResult actionResult = new ActionResult(true, true);

        ItemController<EquipmentUser> controller = mock();
        when(controller.performActionNew(Action.SWAP_TO, gamePlayer)).thenReturn(actionResult);

        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));
        when(equipment.getId()).thenReturn(EQUIPMENT_ID);
        when(itemControllerRegistry.getEquipmentController(EQUIPMENT_ID)).thenReturn(Optional.of(controller));

        DispatchResult result = interactionHandler.handleSwapTo(gamePlayer, ITEM_STACK);

        assertThat(result.handled()).isTrue();
        assertThat(result.cancelEvent()).isTrue();
    }
}
