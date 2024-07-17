package nl.matsgemmeke.battlegrounds.item.gun.controls;

import nl.matsgemmeke.battlegrounds.entity.GunHolder;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.AmmunitionHolder;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ShootFunctionTest {

    private AmmunitionHolder ammunitionHolder;
    private AudioEmitter audioEmitter;
    private FireMode fireMode;

    @Before
    public void setUp() {
        this.ammunitionHolder = mock(AmmunitionHolder.class);
        this.audioEmitter = mock(AudioEmitter.class);
        this.fireMode = mock(FireMode.class);
    }

    @Test
    public void shouldReturnAvailabilityBasedOnFireModeState() {
        when(fireMode.isCycling()).thenReturn(true);

        ShootFunction function = new ShootFunction(ammunitionHolder, audioEmitter, fireMode);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void shouldReturnPerformingStateBasedOnFireMode() {
        when(fireMode.isCycling()).thenReturn(true);

        ShootFunction function = new ShootFunction(ammunitionHolder, audioEmitter, fireMode);
        boolean performing = function.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void shouldCancelFireModeCycleWhenCancelling() {
        when(fireMode.cancel()).thenReturn(true);

        ShootFunction function = new ShootFunction(ammunitionHolder, audioEmitter, fireMode);
        boolean cancelled = function.cancel();

        assertTrue(cancelled);
    }

    @Test
    public void shouldActivateFireModeCycleWhenPerformingAction() {
        when(ammunitionHolder.getMagazineAmmo()).thenReturn(100);

        GunHolder holder = mock(GunHolder.class);

        ShootFunction function = new ShootFunction(ammunitionHolder, audioEmitter, fireMode);
        boolean result = function.perform(holder);

        assertTrue(result);

        verify(fireMode).activateCycle();
    }

    @Test
    public void shouldPlayTriggerSoundsWhenPerformingActionIfThereIsNotEnoughAmmo() {
        when(ammunitionHolder.getMagazineAmmo()).thenReturn(-10);

        Iterable<GameSound> triggerSounds = Collections.emptySet();
        Location location = new Location(null, 1.0, 1.0, 1.0);

        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(location);

        GunHolder holder = mock(GunHolder.class);
        when(holder.getEntity()).thenReturn(player);

        ShootFunction function = new ShootFunction(ammunitionHolder, audioEmitter, fireMode);
        function.setTriggerSounds(triggerSounds);
        boolean result = function.perform(holder);

        assertFalse(result);

        verify(audioEmitter).playSounds(triggerSounds, location);
    }
}
