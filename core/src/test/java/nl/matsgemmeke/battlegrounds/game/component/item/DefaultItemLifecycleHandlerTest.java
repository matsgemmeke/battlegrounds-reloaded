package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultItemLifecycleHandlerTest {

    @Mock
    private EquipmentRegistry equipmentRegistry;
    @InjectMocks
    private DefaultItemLifecycleHandler itemLifecycleHandler;

    @Test
    @DisplayName("resetItems calls reset on all items the player has")
    void reset() {
        Equipment equipment = mock(Equipment.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(equipmentRegistry.getAssignedEquipmentList(gamePlayer)).thenReturn(List.of(equipment));

        itemLifecycleHandler.resetItems(gamePlayer);

        verify(equipment).reset();
    }
}
