package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import org.bukkit.Material;
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
class EquipmentActionExecutorTest {

    private static final ItemStack ITEM_STACK = new ItemStack(Material.IRON_HOE);
    private static final UUID UNIQUE_ID = UUID.randomUUID();

    @Mock
    private EquipmentRegistry equipmentRegistry;
    @Mock
    private GamePlayer gamePlayer;
    @Mock
    private Player player;
    @Mock
    private PlayerRegistry playerRegistry;
    @InjectMocks
    private EquipmentActionExecutor actionExecutor;

    @BeforeEach
    void setUp() {
        when(player.getUniqueId()).thenReturn(UNIQUE_ID);
    }

    @Test
    @DisplayName("handleChangeFromAction does nothing and returns true when given player is no GamePlayer")
    void handleChangeFromAction_unknownGamePlayer() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleChangeFromAction does nothing and returns true when no Equipment matches with GamePlayer and ItemStack")
    void handleChangeFromAction_unknownEquipment() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleChangeFromAction does nothing and returns true when Equipment holder does not equal GamePlayer")
    void handleChangeFromAction_differentHolder() {
        EquipmentHolder otherHolder = mock(EquipmentHolder.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(equipment, never()).onChangeFrom();
    }

    @Test
    @DisplayName("handleChangeFromAction calls change from action on Equipment and returns true")
    void handleChangeFromAction_matchingEquipment() {
        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        boolean performAction = actionExecutor.handleChangeFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(equipment).onChangeFrom();
    }

    @Test
    @DisplayName("handleChangeToAction does nothing and returns true when given player is no GamePlayer")
    void handleChangeToAction_unknownGamePlayer() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleChangeToAction does nothing and returns true when no Equipment matches with GamePlayer and ItemStack")
    void handleChangeToAction_unknownEquipment() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleChangeToAction does nothing and returns true when Equipment holder does not equal GamePlayer")
    void handleChangeToAction_differentHolder() {
        EquipmentHolder otherHolder = mock(EquipmentHolder.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(equipment, never()).onChangeTo();
    }

    @Test
    @DisplayName("handleChangeToAction calls change to action on Equipment and returns true")
    void handleChangeToAction_matchingEquipment() {
        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        boolean performAction = actionExecutor.handleChangeToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(equipment).onChangeTo();
    }

    @Test
    @DisplayName("handleDropItemAction does nothing and returns true when given player is no GamePlayer")
    void handleDropItemAction_unknownGamePlayer() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleDropItemAction does nothing and returns true when no Equipment matches with GamePlayer and ItemStack")
    void handleDropItemAction_unknownEquipment() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleDropItemAction does nothing and returns true when Equipment holder does not equal GamePlayer")
    void handleDropItemAction_differentHolder() {
        EquipmentHolder otherHolder = mock(EquipmentHolder.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(equipmentRegistry, never()).unassign(any(Equipment.class));
        verify(equipment, never()).onDrop();
    }

    @Test
    @DisplayName("handleDropItemAction calls drop action on Equipment and returns true")
    void handleDropItemAction_matchingEquipment() {
        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        boolean performAction = actionExecutor.handleDropItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(equipmentRegistry).unassign(equipment);
        verify(equipment).onDrop();
    }

    @Test
    @DisplayName("handleLeftClickAction does nothing and returns true when given player is no GamePlayer")
    void handleLeftClickAction_unknownPlayer() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleLeftClickAction does nothing and returns true when no Equipment matches with GamePlayer and ItemStack")
    void handleLeftClickAction_unknownEquipment() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleLeftClickAction does nothing and returns true when Equipment holder does not equal GamePlayer")
    void handleLeftClickAction_differentHolder() {
        EquipmentHolder otherHolder = mock(EquipmentHolder.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(equipment, never()).onLeftClick();
    }

    @Test
    @DisplayName("handleLeftClickAction calls change to action on Equipment and returns true")
    void handleLeftClickAction_matchingEquipment() {
        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(equipment).onLeftClick();
    }

    @Test
    @DisplayName("handlePickupItemAction does nothing and returns true when given player is no GamePlayer")
    void handlePickupItemAction_unknownPlayer() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handlePickupItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handlePickupItemAction does nothing and returns true when no Equipment matches with GamePlayer and ItemStack")
    void handlePickupItemAction_unknownEquipment() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getUnassignedEquipment(ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handlePickupItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handlePickupAction calls change to action on Equipment and returns true")
    void handlePickupItemAction_matchingEquipment() {
        Equipment equipment = mock(Equipment.class);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getUnassignedEquipment(ITEM_STACK)).thenReturn(Optional.of(equipment));

        boolean performAction = actionExecutor.handlePickupItemAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(equipmentRegistry).assign(equipment, gamePlayer);
        verify(equipment).onPickUp(gamePlayer);
    }

    @Test
    @DisplayName("handleRightClickAction does nothing and returns true when given player is no GamePlayer")
    void handleRightClickAction_unknownPlayer() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleRightClickAction does nothing and returns true when no Equipment matches with GamePlayer and ItemStack")
    void handleRightClickAction_unknownEquipment() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleRightClickAction does nothing and returns true when Equipment holder does not equal GamePlayer")
    void handleRightClickAction_differentHolder() {
        EquipmentHolder otherHolder = mock(EquipmentHolder.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(equipment, never()).onRightClick();
    }

    @Test
    @DisplayName("handleRightClickAction calls change to action on Equipment and returns true")
    void handleRightClickAction_matchingEquipment() {
        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(equipment).onRightClick();
    }

    @Test
    @DisplayName("handleSwapFromAction does nothing and returns true when given player is no GamePlayer")
    void handleSwapFromAction_unknownGamePlayer() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleSwapFromAction does nothing and returns true when no Equipment matches with GamePlayer and ItemStack")
    void handleSwapFromAction_unknownEquipment() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleSwapFromAction does nothing and returns true when Equipment holder does not equal GamePlayer")
    void handleSwapFromAction_differentHolder() {
        EquipmentHolder otherHolder = mock(EquipmentHolder.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(equipment, never()).onSwapFrom();
    }

    @Test
    @DisplayName("handleSwapFromAction calls change from action on Equipment and returns true")
    void handleSwapFromAction_matchingEquipment() {
        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        boolean performAction = actionExecutor.handleSwapFromAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(equipment).onSwapFrom();
    }

    @Test
    @DisplayName("handleSwapToAction does nothing and returns true when given player is no GamePlayer")
    void handleSwapToAction_unknownGamePlayer() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleSwapToAction does nothing and returns true when no Equipment matches with GamePlayer and ItemStack")
    void handleSwapToAction_unknownEquipment() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    @DisplayName("handleSwapToAction does nothing and returns true when Equipment holder does not equal GamePlayer")
    void handleSwapToAction_differentHolder() {
        EquipmentHolder otherHolder = mock(EquipmentHolder.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(equipment, never()).onSwapTo();
    }

    @Test
    @DisplayName("handleSwapToAction calls change to action on Equipment and returns true")
    void handleSwapToAction_matchingEquipment() {
        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        boolean performAction = actionExecutor.handleSwapToAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(equipment).onSwapTo();
    }
}
