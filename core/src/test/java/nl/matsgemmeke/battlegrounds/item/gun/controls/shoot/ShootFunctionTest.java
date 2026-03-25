package nl.matsgemmeke.battlegrounds.item.gun.controls.shoot;

import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import nl.matsgemmeke.battlegrounds.item.shoot.ShotPerformer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShootFunctionTest {

    @Mock
    private Gun gun;
    @InjectMocks
    private ShootFunction function;

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    @DisplayName("isAvailable returns whether gun can shoot")
    void isAvailable_returnsGunCanShoot(boolean canShoot, boolean expectedAvailable) {
        when(gun.canShoot()).thenReturn(canShoot);

        boolean available = function.isAvailable();

        assertThat(available).isEqualTo(expectedAvailable);
    }

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    @DisplayName("isPerforming returns whether gun is shooting")
    void isPerforming_returnsGunIsShooting(boolean shooting, boolean expectedPerforming) {
        when(gun.isShooting()).thenReturn(shooting);

        boolean performing = function.isPerforming();

        assertThat(performing).isEqualTo(expectedPerforming);
    }

    @Test
    @DisplayName("cancel cancels gun shooting")
    void cancel_cancelsShooting() {
        boolean cancelled = function.cancel();

        assertThat(cancelled).isTrue();

        verify(gun).cancelShooting();
    }

    @Test
    @DisplayName("perform returns false when gun cannot shoot")
    void perform_gunCannotShoot() {
        GunUser user = mock(GunUser.class);

        when(gun.canShoot()).thenReturn(false);

        boolean performed = function.perform(user);

        assertThat(performed).isFalse();

        verify(gun, never()).shoot(any(ShotPerformer.class));
    }

    @Test
    @DisplayName("perform returns true and shoots when gun can shoot")
    void perform_shootsGun() {
        GunUser user = mock(GunUser.class);

        when(gun.canShoot()).thenReturn(true);

        boolean performed = function.perform(user);

        assertThat(performed).isTrue();

        verify(gun).shoot(user);
    }
}
