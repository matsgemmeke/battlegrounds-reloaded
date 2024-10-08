package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
        when(mechanismActivation.isPrimed()).thenReturn(false);

        CookFunction function = new CookFunction(mechanismActivation, audioEmitter);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void shouldPrimeActivationWhenPerforming() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);
        Location location = new Location(null, 1, 1, 1);

        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(location);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getEntity()).thenReturn(player);
        when(holder.getHeldItem()).thenReturn(itemStack);

        CookFunction function = new CookFunction(mechanismActivation, audioEmitter);
        function.perform(holder);

        verify(audioEmitter).playSounds(any(), eq(location));
        verify(mechanismActivation).primeInHand(holder, itemStack);
    }
}
