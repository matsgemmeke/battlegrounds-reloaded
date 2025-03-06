package nl.matsgemmeke.battlegrounds.item.gun.controls.scope;

import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ChangeScopeMagnificationFunctionTest {

    private Gun gun;

    @BeforeEach
    public void setUp() {
        gun = mock(Gun.class);
    }

    @Test
    public void isAvailableReturnsTrueWhenGunIsScoped() {
        when(gun.isUsingScope()).thenReturn(true);

        ChangeScopeMagnificationFunction function = new ChangeScopeMagnificationFunction(gun);
        boolean available = function.isAvailable();

        assertTrue(available);
    }

    @Test
    public void isAvailableReturnsFalseWhenGunIsNotScoped() {
        when(gun.isUsingScope()).thenReturn(false);

        ChangeScopeMagnificationFunction function = new ChangeScopeMagnificationFunction(gun);
        boolean available = function.isAvailable();

        assertFalse(available);
    }

    @Test
    public void performReturnsFalseWhenGunIsNotScoped() {
        GunHolder holder = mock(GunHolder.class);

        when(gun.isUsingScope()).thenReturn(false);

        ChangeScopeMagnificationFunction function = new ChangeScopeMagnificationFunction(gun);
        boolean performed = function.perform(holder);

        assertFalse(performed);

        verify(gun, never()).changeScopeMagnification();
    }

    @Test
    public void performReturnsTrueAndChangesMagnificationWhenGunIsScoped() {
        GunHolder holder = mock(GunHolder.class);

        when(gun.isUsingScope()).thenReturn(true);

        ChangeScopeMagnificationFunction function = new ChangeScopeMagnificationFunction(gun);
        boolean performed = function.perform(holder);

        assertTrue(performed);

        verify(gun).changeScopeMagnification();
    }
}
