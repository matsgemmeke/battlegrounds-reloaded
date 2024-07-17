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

public class ChangeScopeMagnificationFunctionTest {

    private AudioEmitter audioEmitter;
    private ScopeAttachment scopeAttachment;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        scopeAttachment = mock(ScopeAttachment.class);
    }

    @Test
    public void shouldReturnAvailabilityWhetherScopeIsScoped() {
        when(scopeAttachment.isScoped()).thenReturn(true);

        ChangeScopeMagnificationFunction function = new ChangeScopeMagnificationFunction(scopeAttachment, audioEmitter);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void shouldNotPerformIfScopeIsNotScoped() {
        GunHolder holder = mock(GunHolder.class);

        ChangeScopeMagnificationFunction function = new ChangeScopeMagnificationFunction(scopeAttachment, audioEmitter);
        boolean performed = function.perform(holder);

        assertFalse(performed);
    }

    @Test
    public void shouldChangeScopeMagnificationWhenPerforming() {
        when(scopeAttachment.isScoped()).thenReturn(true);
        when(scopeAttachment.nextMagnification()).thenReturn(true);

        Location location = new Location(null, 1.0, 1.0, 1.0);

        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(location);

        GunHolder holder = mock(GunHolder.class);
        when(holder.getEntity()).thenReturn(player);

        ChangeScopeMagnificationFunction function = new ChangeScopeMagnificationFunction(scopeAttachment, audioEmitter);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        verify(audioEmitter).playSounds(any(), eq(location));
        verify(scopeAttachment).nextMagnification();
    }
}
