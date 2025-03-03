package nl.matsgemmeke.battlegrounds.item.gun.controls.shoot;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ShootFunctionTest {

    private AmmunitionStorage ammunitionStorage;
    private AudioEmitter audioEmitter;
    private FireMode fireMode;

    @BeforeEach
    public void setUp() {
        this.ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);
        this.audioEmitter = mock(AudioEmitter.class);
        this.fireMode = mock(FireMode.class);
    }

    @Test
    public void shouldReturnAvailabilityBasedOnFireModeState() {
        when(fireMode.isCycling()).thenReturn(true);

        ShootFunction function = new ShootFunction(ammunitionStorage, audioEmitter, fireMode);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void shouldReturnPerformingStateBasedOnFireMode() {
        when(fireMode.isCycling()).thenReturn(true);

        ShootFunction function = new ShootFunction(ammunitionStorage, audioEmitter, fireMode);
        boolean performing = function.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void shouldCancelFireModeCycleWhenCancelling() {
        when(fireMode.cancelCycle()).thenReturn(true);

        ShootFunction function = new ShootFunction(ammunitionStorage, audioEmitter, fireMode);
        boolean cancelled = function.cancel();

        assertTrue(cancelled);
    }

    @Test
    public void shouldActivateFireModeCycleWhenPerformingAction() {
        GunHolder holder = mock(GunHolder.class);

        ShootFunction function = new ShootFunction(ammunitionStorage, audioEmitter, fireMode);
        boolean result = function.perform(holder);

        assertTrue(result);

        verify(fireMode).activateCycle();
    }

    @Test
    public void shouldPlayTriggerSoundsWhenPerformingActionIfThereIsNotEnoughAmmo() {
        ammunitionStorage.setMagazineAmmo(0);

        Iterable<GameSound> triggerSounds = Collections.emptySet();
        Location location = new Location(null, 1.0, 1.0, 1.0);

        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(location);

        GunHolder holder = mock(GunHolder.class);
        when(holder.getEntity()).thenReturn(player);

        ShootFunction function = new ShootFunction(ammunitionStorage, audioEmitter, fireMode);
        function.setTriggerSounds(triggerSounds);
        boolean result = function.perform(holder);

        assertFalse(result);

        verify(audioEmitter).playSounds(triggerSounds, location);
    }
}
