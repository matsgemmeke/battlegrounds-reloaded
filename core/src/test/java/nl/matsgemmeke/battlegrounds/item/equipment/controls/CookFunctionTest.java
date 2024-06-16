package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.game.audio.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.equipment.activation.EquipmentActivation;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class CookFunctionTest {

    private AudioEmitter audioEmitter;
    private EquipmentActivation activation;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        activation = mock(EquipmentActivation.class);
    }

    @Test
    public void shouldOnlyBeAvailableIfActivationIsNotPrimed() {
        when(activation.isPrimed()).thenReturn(false);

        CookFunction function = new CookFunction(activation, audioEmitter);
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

        CookFunction function = new CookFunction(activation, audioEmitter);
        function.perform(holder);

        verify(audioEmitter).playSounds(any(), eq(location));
        verify(activation).prime();
    }
}
