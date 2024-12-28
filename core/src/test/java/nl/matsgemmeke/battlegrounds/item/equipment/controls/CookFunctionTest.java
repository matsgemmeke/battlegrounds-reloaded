package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunctionException;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectNew;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CookFunctionTest {

    private static final Iterable<GameSound> THROW_SOUNDS = Collections.emptySet();

    private AudioEmitter audioEmitter;
    private CookProperties properties;
    private Equipment equipment;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        properties = new CookProperties(THROW_SOUNDS);
        equipment = mock(Equipment.class);
    }

    @Test
    public void isAvailableReturnsTrueIfActivationIsNotAwaitingDeployment() {
        ItemEffectNew effect = mock(ItemEffectNew.class);
        when(effect.isAwaitingDeployment()).thenReturn(false);

        when(equipment.getEffect()).thenReturn(effect);

        CookFunction function = new CookFunction(properties, equipment, audioEmitter);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void isAvailableReturnsFalseIfEquipmentHasNoEffectActivation() {
        when(equipment.getEffect()).thenReturn(null);

        CookFunction function = new CookFunction(properties, equipment, audioEmitter);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void isAvailableReturnsFalseIfActivationIsAwaitingDeployment() {
        ItemEffectNew effect = mock(ItemEffectNew.class);
        when(effect.isAwaitingDeployment()).thenReturn(true);

        when(equipment.getEffect()).thenReturn(effect);

        CookFunction function = new CookFunction(properties, equipment, audioEmitter);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void performThrowsExceptionIfEquipmentHasNoEffectActivation() {
        EquipmentHolder holder = mock(EquipmentHolder.class);

        when(equipment.getEffect()).thenReturn(null);

        CookFunction function = new CookFunction(properties, equipment, audioEmitter);

        assertThrows(ItemFunctionException.class, () -> function.perform(holder));
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

        ItemEffectNew effect = mock(ItemEffectNew.class);
        when(equipment.getEffect()).thenReturn(effect);

        CookFunction function = new CookFunction(properties, equipment, audioEmitter);
        function.perform(holder);

        ArgumentCaptor<ItemEffectContext> contextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(effect).prime(contextCaptor.capture());

        verify(audioEmitter).playSounds(any(), eq(location));
    }
}
