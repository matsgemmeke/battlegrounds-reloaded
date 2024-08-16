package nl.matsgemmeke.battlegrounds.item.gun.controls;

import nl.matsgemmeke.battlegrounds.item.AmmunitionHolder;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ReloadFunctionTest {

    private AmmunitionHolder ammunitionHolder;
    private ReloadSystem reloadSystem;

    @Before
    public void setUp() {
        ammunitionHolder = mock(AmmunitionHolder.class);
        reloadSystem = mock(ReloadSystem.class);
    }

    @Test
    public void shouldReturnAvailabilityBasedOnReloadSystemState() {
        when(reloadSystem.isPerforming()).thenReturn(true);

        ReloadFunction function = new ReloadFunction(ammunitionHolder, reloadSystem);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void shouldReturnPerformingStateBasedOnReloadSystem() {
        when(reloadSystem.isPerforming()).thenReturn(true);

        ReloadFunction function = new ReloadFunction(ammunitionHolder, reloadSystem);
        boolean performing = function.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void shouldCancelReloadSystemWhenCancelling() {
        when(reloadSystem.cancelReload()).thenReturn(true);

        ReloadFunction function = new ReloadFunction(ammunitionHolder, reloadSystem);
        boolean cancelled = function.cancel();

        assertTrue(cancelled);
    }

    @Test
    public void shouldNotPerformIfAmmunitionHolderIsAlreadyFullyReloaded() {
        when(ammunitionHolder.getMagazineAmmo()).thenReturn(10);
        when(ammunitionHolder.getMagazineSize()).thenReturn(10);

        GunHolder holder = mock(GunHolder.class);

        ReloadFunction function = new ReloadFunction(ammunitionHolder, reloadSystem);
        boolean performed = function.perform(holder);

        assertFalse(performed);
    }

    @Test
    public void shouldNotPerformIfAmmunitionHolderHasNoReserveAmmoLeft() {
        when(ammunitionHolder.getMagazineAmmo()).thenReturn(5);
        when(ammunitionHolder.getMagazineSize()).thenReturn(10);
        when(ammunitionHolder.getReserveAmmo()).thenReturn(0);

        GunHolder holder = mock(GunHolder.class);

        ReloadFunction function = new ReloadFunction(ammunitionHolder, reloadSystem);
        boolean performed = function.perform(holder);

        assertFalse(performed);
    }

    @Test
    public void shouldPerformReloadWithReloadSystemWhenPerforming() {
        GunHolder holder = mock(GunHolder.class);

        when(ammunitionHolder.getMagazineAmmo()).thenReturn(5);
        when(ammunitionHolder.getMagazineSize()).thenReturn(10);
        when(ammunitionHolder.getReserveAmmo()).thenReturn(10);
        when(reloadSystem.performReload(holder)).thenReturn(true);

        ReloadFunction function = new ReloadFunction(ammunitionHolder, reloadSystem);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        verify(reloadSystem).performReload(holder);
    }
}
