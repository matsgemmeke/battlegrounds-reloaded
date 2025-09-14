package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class EquipmentActionExecutorTest {

    private static final ItemStack ITEM_STACK = new ItemStack(Material.IRON_HOE);

    private EquipmentRegistry equipmentRegistry;
    private GamePlayer gamePlayer;
    private Player player;
    private PlayerRegistry playerRegistry;

    @BeforeEach
    public void setUp() {
        equipmentRegistry = mock(EquipmentRegistry.class);
        gamePlayer = mock(GamePlayer.class);
        player = mock(Player.class);
        playerRegistry = mock(PlayerRegistry.class);
    }

    @Test
    public void handleLeftClickActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.empty());

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleLeftClickActionDoesNothingWhenNoEquipmentMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleLeftClickActionDoesNothingWhenEquipmentHolderDoesNotMatch() {
        EquipmentHolder otherHolder = mock(EquipmentHolder.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(equipment, never()).onLeftClick();
    }

    @Test
    public void handleLeftClickActionCallsEquipmentFunctionWhenMatchingEquipmentIsFound() {
        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleLeftClickAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(equipment).onLeftClick();
    }

    @Test
    public void handleRightClickActionDoesNothingWhenGivenPlayerIsNotRegistered() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.empty());

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleRightClickActionDoesNothingWhenNoEquipmentMatchesWithGivenHolderAndItemStack() {
        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();
    }

    @Test
    public void handleRightClickActionDoesNothingWhenEquipmentHolderDoesNotMatch() {
        EquipmentHolder otherHolder = mock(EquipmentHolder.class);

        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(otherHolder);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isTrue();

        verify(equipment, never()).onRightClick();
    }

    @Test
    public void handleRightClickActionCallsEquipmentFunctionWhenMatchingEquipmentIsFound() {
        Equipment equipment = mock(Equipment.class);
        when(equipment.getHolder()).thenReturn(gamePlayer);

        when(playerRegistry.findByEntity(player)).thenReturn(Optional.of(gamePlayer));
        when(equipmentRegistry.getAssignedEquipment(gamePlayer, ITEM_STACK)).thenReturn(Optional.of(equipment));

        EquipmentActionExecutor actionExecutor = new EquipmentActionExecutor(equipmentRegistry, playerRegistry);
        boolean performAction = actionExecutor.handleRightClickAction(player, ITEM_STACK);

        assertThat(performAction).isFalse();

        verify(equipment).onRightClick();
    }
}
