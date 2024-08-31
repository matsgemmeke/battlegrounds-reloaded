package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class CookFunctionTest {

    private AudioEmitter audioEmitter;
    private ItemMechanismActivation mechanismActivation;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        mechanismActivation = mock(ItemMechanismActivation.class);
    }

    @Test
    public void shouldOnlyBeAvailableIfActivationIsNotPrimed() {
        when(mechanismActivation.isPriming()).thenReturn(false);

        CookFunction function = new CookFunction(mechanismActivation, audioEmitter);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void shouldPrimeActivationWhenPerforming() {
        Location location = new Location(null, 1, 1, 1);

        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(location);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getEntity()).thenReturn(player);

        CookFunction function = new CookFunction(mechanismActivation, audioEmitter);
        function.perform(holder);

        verify(audioEmitter).playSounds(any(), eq(location));
        verify(mechanismActivation).prime(holder, null);
    }
}
