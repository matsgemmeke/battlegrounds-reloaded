package nl.matsgemmeke.battlegrounds.item.gun.controls.reload;

import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ReloadFunctionTest {

    private Gun gun;
    private ReloadSystem reloadSystem;

    @BeforeEach
    public void setUp() {
        gun = mock(Gun.class);
        reloadSystem = mock(ReloadSystem.class);
    }

    @Test
    public void isAvailableReturnsFalseIfReloadSystemIsPerforming() {
        when(reloadSystem.isPerforming()).thenReturn(true);

        ReloadFunction function = new ReloadFunction(gun, reloadSystem);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void isAvailableReturnsFalseIfGunMagazineStillHasItsFullCapacity() {
        when(reloadSystem.isPerforming()).thenReturn(false);
        when(gun.getMagazineAmmo()).thenReturn(10);
        when(gun.getMagazineSize()).thenReturn(10);

        ReloadFunction function = new ReloadFunction(gun, reloadSystem);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void isAvailableReturnsFalseIfGunHasNoReserveAmmo() {
        when(reloadSystem.isPerforming()).thenReturn(false);
        when(gun.getMagazineAmmo()).thenReturn(0);
        when(gun.getMagazineSize()).thenReturn(10);
        when(gun.getReserveAmmo()).thenReturn(0);

        ReloadFunction function = new ReloadFunction(gun, reloadSystem);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void isAvailableReturnsTrueIfReloadSystemIsNotPerformingAndGunHasSufficientAmmoForReloading() {
        when(reloadSystem.isPerforming()).thenReturn(false);
        when(gun.getMagazineAmmo()).thenReturn(0);
        when(gun.getMagazineSize()).thenReturn(10);
        when(gun.getReserveAmmo()).thenReturn(10);

        ReloadFunction function = new ReloadFunction(gun, reloadSystem);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void isPerformingReturnsTrueIfReloadSystemIsPerforming() {
        when(reloadSystem.isPerforming()).thenReturn(true);

        ReloadFunction function = new ReloadFunction(gun, reloadSystem);
        boolean performing = function.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void isPerformingReturnsFalseIfReloadSystemIsNotPerforming() {
        when(reloadSystem.isPerforming()).thenReturn(false);

        ReloadFunction function = new ReloadFunction(gun, reloadSystem);
        boolean performing = function.isPerforming();

        assertFalse(performing);
    }

    @Test
    public void cancelCancelsReloadSystem() {
        when(reloadSystem.cancelReload()).thenReturn(true);

        ReloadFunction function = new ReloadFunction(gun, reloadSystem);
        boolean cancelled = function.cancel();

        assertTrue(cancelled);

        verify(reloadSystem).cancelReload();
    }

    @Test
    public void performReturnsFalseIfNotAvailable() {
        when(gun.getMagazineAmmo()).thenReturn(10);
        when(gun.getMagazineSize()).thenReturn(10);

        GunHolder holder = mock(GunHolder.class);

        ReloadFunction function = new ReloadFunction(gun, reloadSystem);
        boolean performed = function.perform(holder);

        assertFalse(performed);
    }

    @Test
    public void performReturnsTrueAndPerformsReload() {
        GunHolder holder = mock(GunHolder.class);

        when(gun.getMagazineAmmo()).thenReturn(5);
        when(gun.getMagazineSize()).thenReturn(10);
        when(gun.getReserveAmmo()).thenReturn(10);
        when(reloadSystem.performReload(holder)).thenReturn(true);

        ReloadFunction function = new ReloadFunction(gun, reloadSystem);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        verify(reloadSystem).performReload(holder);
    }
}
