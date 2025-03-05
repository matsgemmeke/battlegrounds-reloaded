package nl.matsgemmeke.battlegrounds.item.gun.controls.scope;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeAttachment;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class StopScopeFunctionTest {

    private Gun gun;

    @BeforeEach
    public void setUp() {
        gun = mock(Gun.class);
    }

    @Test
    public void isAvailableReturnsTrueIfGunIsUsingScope() {
        when(gun.isUsingScope()).thenReturn(true);

        StopScopeFunction function = new StopScopeFunction(gun);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void isAvailableReturnsFalseIfGunIsNotUsingScope() {
        when(gun.isUsingScope()).thenReturn(false);

        StopScopeFunction function = new StopScopeFunction(gun);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void performReturnsTrueAndCancelsScope() {
        GunHolder holder = mock(GunHolder.class);

        when(gun.isUsingScope()).thenReturn(true);

        StopScopeFunction function = new StopScopeFunction(gun);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        verify(gun).cancelScope();
    }

    @Test
    public void performReturnsFalseWhenGunIsNotUsingScope() {
        GunHolder holder = mock(GunHolder.class);

        when(gun.isUsingScope()).thenReturn(false);

        StopScopeFunction function = new StopScopeFunction(gun);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verify(gun, never()).cancelScope();
    }
}
