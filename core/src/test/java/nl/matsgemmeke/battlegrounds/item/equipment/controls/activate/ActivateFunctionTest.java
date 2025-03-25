package nl.matsgemmeke.battlegrounds.item.equipment.controls.activate;

import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ActivateFunctionTest {

    private Equipment equipment;

    @BeforeEach
    public void setUp() {
        equipment = mock(Equipment.class);
    }

    @Test
    public void isAvailableReturnsFalseWhenEquipmentActivatorIsNotReady() {
        when(equipment.isActivatorReady()).thenReturn(false);

        ActivateFunction function = new ActivateFunction(equipment);
        boolean available = function.isAvailable();

        assertThat(available).isFalse();
    }

    @Test
    public void isAvailableReturnsTrueWhenEquipmentActivatorIsReady() {
        when(equipment.isActivatorReady()).thenReturn(true);

        ActivateFunction function = new ActivateFunction(equipment);
        boolean available = function.isAvailable();

        assertThat(available).isTrue();
    }

    @Test
    public void performReturnsTrueAndActivatesDeployment() {
        Player player = mock(Player.class);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getEntity()).thenReturn(player);

        ActivateFunction function = new ActivateFunction(equipment);
        function.perform(holder);

        verify(equipment).activateDeployment(holder, player);
    }
}
