package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.source.HeldItem;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class CookFunctionTest {

    private AudioEmitter audioEmitter;
    private ItemEffectActivation effectActivation;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        effectActivation = mock(ItemEffectActivation.class);
    }

    @Test
    public void isAvailableReturnsTrueIfActivationIsNotAwaitingDeployment() {
        when(effectActivation.isAwaitingDeployment()).thenReturn(false);

        CookFunction function = new CookFunction(effectActivation, audioEmitter);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void isAvailableReturnsFalseIfActivationIsAwaitingDeployment() {
        when(effectActivation.isAwaitingDeployment()).thenReturn(true);

        CookFunction function = new CookFunction(effectActivation, audioEmitter);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void primeEffectActivationAndPrepareActivatorWhenPerforming() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);
        Location location = new Location(null, 1, 1, 1);

        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(location);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getEntity()).thenReturn(player);
        when(holder.getHeldItem()).thenReturn(itemStack);

        CookFunction function = new CookFunction(effectActivation, audioEmitter);
        function.perform(holder);

        ArgumentCaptor<HeldItem> heldItemCaptor = ArgumentCaptor.forClass(HeldItem.class);
        verify(effectActivation).prime(eq(holder), heldItemCaptor.capture());

        verify(audioEmitter).playSounds(any(), eq(location));
    }
}
