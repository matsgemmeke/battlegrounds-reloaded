package nl.matsgemmeke.battlegrounds.item.gun.controls.scope;

import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeUser;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class UseScopeFunctionTest {

    @NotNull
    private Gun gun;

    @BeforeEach
    public void setUp() {
        gun = mock(Gun.class);
    }

    @Test
    public void isAvailableReturnsTrueIfGunIsNotUsingScope() {
        when(gun.isUsingScope()).thenReturn(false);

        UseScopeFunction function = new UseScopeFunction(gun);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void isAvailableReturnsFalseIfGunIsUsingScope() {
        when(gun.isUsingScope()).thenReturn(true);

        UseScopeFunction function = new UseScopeFunction(gun);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void cancelCancelsGunScope() {
        when(gun.cancelScope()).thenReturn(true);

        UseScopeFunction function = new UseScopeFunction(gun);
        boolean cancelled = function.cancel();

        assertTrue(cancelled);

        verify(gun).cancelScope();
    }

    @Test
    public void performReturnsFalseIfGunIsUsingScope() {
        GunHolder holder = mock(GunHolder.class);

        when(gun.isUsingScope()).thenReturn(true);

        UseScopeFunction function = new UseScopeFunction(gun);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verify(gun, never()).applyScope(any(ScopeUser.class));
    }

    @Test
    public void performReturnsTrueAndAppliesScope() {
        GunHolder holder = mock(GunHolder.class);

        when(gun.applyScope(holder)).thenReturn(true);
        when(gun.isUsingScope()).thenReturn(false);

        UseScopeFunction function = new UseScopeFunction(gun);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        verify(gun).applyScope(holder);
    }
}
