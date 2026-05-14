package nl.matsgemmeke.battlegrounds.item.gun.controls.shoot;

import nl.matsgemmeke.battlegrounds.item.controls.FunctionResult;
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
    @Mock
    private GunUser user;
    @InjectMocks
    private ShootFunction function;

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
    @DisplayName("perform returns FAILED when gun cannot shoot")
    void perform_gunCannotShoot() {
        when(gun.canShoot()).thenReturn(false);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.FAILED);

        verify(gun, never()).shoot(any(ShotPerformer.class));
    }

    @Test
    @DisplayName("perform returns SUCCESS and shoots gun")
    void perform_shootsGun() {
        when(gun.canShoot()).thenReturn(true);

        FunctionResult result = function.perform(user);

        assertThat(result).isEqualTo(FunctionResult.SUCCESS);

        verify(gun).shoot(user);
    }
}
