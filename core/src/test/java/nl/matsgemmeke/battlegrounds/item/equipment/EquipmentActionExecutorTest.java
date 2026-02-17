package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @BeforeEach
    void setUp() {
        when(player.getUniqueId()).thenReturn(UNIQUE_ID);
    }

    @Test
    void handleLeftClickActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.empty());

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleLeftClickActionDoesNothingWhenNoEquipmentMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleLeftClickActionDoesNothingWhenEquipmentHolderDoesNotMatch() {
        EquipmentHolder otherHolder = mock(EquipmentHolder.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(equipment, never()).onLeftClick();
    }

    @Test
    void handleLeftClickActionCallsEquipmentFunctionWhenMatchingEquipmentIsFound() {
        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(equipment).onLeftClick();
    }

    @Test
    void handleRightClickActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.empty());

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleRightClickActionDoesNothingWhenNoEquipmentMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    void handleRightClickActionDoesNothingWhenEquipmentHolderDoesNotMatch() {
        EquipmentHolder otherHolder = mock(EquipmentHolder.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(equipment, never()).onRightClick();
    }

    @Test
    void handleRightClickActionCallsEquipmentFunctionWhenMatchingEquipmentIsFound() {
        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(equipment).onRightClick();
    }
}
