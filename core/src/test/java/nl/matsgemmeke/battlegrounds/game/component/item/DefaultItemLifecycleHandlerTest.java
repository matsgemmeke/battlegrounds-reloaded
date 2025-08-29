package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class DefaultItemLifecycleHandlerTest {

    private EquipmentRegistry equipmentRegistry;

    @BeforeEach
    public void setUp() {
        equipmentRegistry = mock(EquipmentRegistry.class);
    }

    @Test
    public void cleanupItemsCallsCleanupOnAllItemsThatThePlayerHas() {
        Equipment equipment = mock(Equipment.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(equipmentRegistry.getAssignedEquipment(gamePlayer)).thenReturn(List.of(equipment));

        DefaultItemLifecycleHandler itemLifecycleHandler = new DefaultItemLifecycleHandler(equipmentRegistry);
        itemLifecycleHandler.cleanupItems(gamePlayer);

        verify(equipment).cleanup();
    }
}
