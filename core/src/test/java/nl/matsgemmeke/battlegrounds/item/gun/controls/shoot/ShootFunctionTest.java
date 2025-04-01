package nl.matsgemmeke.battlegrounds.item.gun.controls.shoot;

import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ShootFunctionTest {

    private Gun gun;

    @BeforeEach
    public void setUp() {
        gun = mock(Gun.class);
    }

    @Test
    public void isAvailableReturnsTrueIfGunCanShoot() {
        when(gun.canShoot()).thenReturn(true);

        ShootFunction function = new ShootFunction(gun);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void isAvailableReturnsFalseIfGunCannotShoot() {
        when(gun.canShoot()).thenReturn(false);

        ShootFunction function = new ShootFunction(gun);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void isPerformingReturnsTrueIfGunIsShooting() {
        when(gun.isShooting()).thenReturn(true);

        ShootFunction function = new ShootFunction(gun);
        boolean performing = function.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void isPerformingReturnsFalseIfGunIsNotShooting() {
        when(gun.isShooting()).thenReturn(false);

        ShootFunction function = new ShootFunction(gun);
        boolean performing = function.isPerforming();

        assertFalse(performing);
    }

    @Test
    public void cancelCancelsGunShootingCycle() {
        when(gun.cancelShootingCycle()).thenReturn(true);

        ShootFunction function = new ShootFunction(gun);
        boolean cancelled = function.cancel();

        assertTrue(cancelled);

        verify(gun).cancelShootingCycle();
    }

    @Test
    public void performReturnsFalseIfGunCannotShoot() {
        GunHolder holder = mock(GunHolder.class);

        when(gun.canShoot()).thenReturn(false);

        ShootFunction function = new ShootFunction(gun);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verify(gun, never()).shoot();
    }

    @Test
    public void performReturnsTrueAndShootsIfGunCanShoot() {
        GunHolder holder = mock(GunHolder.class);

        when(gun.canShoot()).thenReturn(true);

        ShootFunction function = new ShootFunction(gun);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        verify(gun).startShootCycle();
    }
}
