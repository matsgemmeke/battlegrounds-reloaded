package nl.matsgemmeke.battlegrounds.item.gun.controls.reload;

import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ReloadFunctionTest {

    private Gun gun;

    @BeforeEach
    public void setUp() {
        gun = mock(Gun.class);
    }

    @Test
    public void isAvailableReturnsFalseIfGunCannotPerformReload() {
        when(gun.isReloadAvailable()).thenReturn(false);

        ReloadFunction function = new ReloadFunction(gun);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void isAvailableReturnsTrueIfGunCanReload() {
        when(gun.isReloadAvailable()).thenReturn(true);

        ReloadFunction function = new ReloadFunction(gun);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void isPerformingReturnsTrueIfGunIsReloading() {
        when(gun.isReloading()).thenReturn(true);

        ReloadFunction function = new ReloadFunction(gun);
        boolean performing = function.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void isPerformingReturnsFalseIfGunIsNotReloading() {
        when(gun.isReloading()).thenReturn(false);

        ReloadFunction function = new ReloadFunction(gun);
        boolean performing = function.isPerforming();

        assertFalse(performing);
    }

    @Test
    public void cancelCancelsGunReloadOperation() {
        when(gun.cancelReload()).thenReturn(true);

        ReloadFunction function = new ReloadFunction(gun);
        boolean cancelled = function.cancel();

        assertTrue(cancelled);

        verify(gun).cancelReload();
    }

    @Test
    public void performReturnsFalseIfGunIsNotAvailable() {
        when(gun.isReloadAvailable()).thenReturn(false);

        GunHolder holder = mock(GunHolder.class);

        ReloadFunction function = new ReloadFunction(gun);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verify(gun, never()).reload(any(GunHolder.class));
    }

    @Test
    public void performReturnsTrueAndPerformsReload() {
        GunHolder holder = mock(GunHolder.class);

        when(gun.isReloadAvailable()).thenReturn(true);

        ReloadFunction function = new ReloadFunction(gun);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        verify(gun).reload(holder);
    }
}
