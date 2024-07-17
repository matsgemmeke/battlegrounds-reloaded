package nl.matsgemmeke.battlegrounds.item.gun.controls;

import nl.matsgemmeke.battlegrounds.entity.GunHolder;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeAttachment;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class UseScopeFunctionTest {

    @NotNull
    private AudioEmitter audioEmitter;
    @NotNull
    private ScopeAttachment scopeAttachment;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        scopeAttachment = mock(ScopeAttachment.class);
    }

    @Test
    public void shouldReturnAvailabilityBasedOnScopeState() {
        when(scopeAttachment.isScoped()).thenReturn(true);

        UseScopeFunction function = new UseScopeFunction(scopeAttachment, audioEmitter);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void shouldNotCancelIfScopeIsNotBeingUsed() {
        UseScopeFunction function = new UseScopeFunction(scopeAttachment, audioEmitter);
        boolean cancelled = function.cancel();

        assertFalse(cancelled);
    }

    @Test
    public void shouldReturnWhetherAttachmentHasSuccessfullyCancelled() {
        when(scopeAttachment.isScoped()).thenReturn(true);
        when(scopeAttachment.removeEffect()).thenReturn(true);

        UseScopeFunction function = new UseScopeFunction(scopeAttachment, audioEmitter);
        boolean cancelled = function.cancel();

        assertTrue(cancelled);
    }

    @Test
    public void shouldInitiateScopeEffect() {
        Location location = new Location(null, 1.0, 1.0, 1.0);

        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(location);

        GunHolder holder = mock(GunHolder.class);
        when(holder.getEntity()).thenReturn(player);

        when(scopeAttachment.applyEffect(holder)).thenReturn(true);

        UseScopeFunction function = new UseScopeFunction(scopeAttachment, audioEmitter);
        boolean result = function.perform(holder);

        assertTrue(result);

        verify(audioEmitter).playSounds(any(), eq(location));
    }

    @Test
    public void shouldNotPerformIfScopeIsAlreadyBeingUsed() {
        when(scopeAttachment.isScoped()).thenReturn(true);

        GunHolder holder = mock(GunHolder.class);

        UseScopeFunction function = new UseScopeFunction(scopeAttachment, audioEmitter);
        boolean result = function.perform(holder);

        assertFalse(result);
    }
}
