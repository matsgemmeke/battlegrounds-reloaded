package nl.matsgemmeke.battlegrounds.item.gun.controls;

import nl.matsgemmeke.battlegrounds.entity.GunHolder;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeAttachment;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class StopScopeFunctionTest {

    private AudioEmitter audioEmitter;
    private ScopeAttachment scopeAttachment;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        scopeAttachment = mock(ScopeAttachment.class);
    }

    @Test
    public void shouldReturnAvailabilityBasedOnScopeState() {
        when(scopeAttachment.isScoped()).thenReturn(true);

        StopScopeFunction function = new StopScopeFunction(scopeAttachment, audioEmitter);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void shouldRemoveScopeEffectAndPlaySoundsWhenPerformingAction() {
        Location location = new Location(null, 1.0, 1.0, 1.0);

        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(location);

        GunHolder holder = mock(GunHolder.class);
        when(holder.getEntity()).thenReturn(player);

        when(scopeAttachment.isScoped()).thenReturn(true);
        when(scopeAttachment.removeEffect()).thenReturn(true);

        StopScopeFunction function = new StopScopeFunction(scopeAttachment, audioEmitter);
        boolean result = function.perform(holder);

        assertTrue(result);

        verify(scopeAttachment).removeEffect();
    }

    @Test
    public void shouldNotRemoveScopeEffectWhenPerformingActionIfScopeIsNotBeingUsed() {
        GunHolder holder = mock(GunHolder.class);

        StopScopeFunction function = new StopScopeFunction(scopeAttachment, audioEmitter);
        boolean result = function.perform(holder);

        assertFalse(result);
    }
}
