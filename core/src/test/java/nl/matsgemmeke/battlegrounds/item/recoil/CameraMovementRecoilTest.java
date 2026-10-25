package nl.matsgemmeke.battlegrounds.item.recoil;

import org.bukkit.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Timer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CameraMovementRecoilTest {

    @Mock
    private Timer timer;
    @InjectMocks
    private CameraMovementRecoil recoil;

    @Test
    @DisplayName("produceRecoil sets camera rotation once when there is no kickback duration")
    void produceRecoil_withoutKickbackDuration() {
        RecoilReceiver receiver = mock(RecoilReceiver.class);
        Float[] horizontalRecoil = new Float[] { 1.0f };
        Float[] verticalRecoil = new Float[] { 1.0f };

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        recoil.setHorizontalRecoilValues(horizontalRecoil);
        recoil.setVerticalRecoilValues(verticalRecoil);
        recoil.setRotationDuration(20);
        Location result = recoil.produceRecoil(receiver, direction);

        verify(receiver).modifyCameraRotation(1.0f, 1.0f);

        assertThat(result).isEqualTo(direction);
    }

    @Test
    @DisplayName("produceRecoil sets camera rotation once when there is no rotation duration")
    void produceRecoil_withoutRotationDuration() {
        RecoilReceiver receiver = mock(RecoilReceiver.class);
        Float[] horizontalRecoil = new Float[] { 1.0f };
        Float[] verticalRecoil = new Float[] { 1.0f };

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        recoil.setHorizontalRecoilValues(horizontalRecoil);
        recoil.setVerticalRecoilValues(verticalRecoil);
        recoil.setKickbackDuration(200);

        Location result = recoil.produceRecoil(receiver, direction);

        verify(receiver).modifyCameraRotation(1.0f, 1.0f);

        assertThat(result).isEqualTo(direction);
    }

    @Test
    @DisplayName("produceRecoil schedules task for camera rotation steps")
    void produceRecoil_successful() {
        RecoilReceiver receiver = mock(RecoilReceiver.class);
        when(receiver.getRelativeAccuracy()).thenReturn(1.0f);

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        CameraMovementRecoil recoil = new CameraMovementRecoil(timer);
        recoil.setHorizontalRecoilValues(new Float[] { 1.0f });
        recoil.setVerticalRecoilValues(new Float[] { 1.0f });
        recoil.setKickbackDuration(200);
        recoil.setRecoveryDuration(300);
        recoil.setRecoveryRate(0.5f);
        recoil.setRotationDuration(20);

        Location result = recoil.produceRecoil(receiver, direction);

        verify(timer).scheduleAtFixedRate(any(), eq(0L), eq(20L));

        assertThat(result).isEqualTo(direction);
    }
}
