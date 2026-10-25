package nl.matsgemmeke.battlegrounds.item.recoil;

import org.bukkit.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Timer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CameraMovementRecoilTest {

    private static final Float[] HORIZONTAL_RECOIL = { 1.0f };
    private static final Float[] VERTICAL_RECOIL = { 2.0f };
    private static final int ROTATION_DURATION = 20;
    private static final int RECOVERY_DURATION = 300;
    private static final int KICKBACK_DURATION = 200;
    private static final float RECOVERY_RATE = 0.5f;

    @Mock
    private Timer timer;
    @InjectMocks
    private CameraMovementRecoil recoil;

    @Test
    @DisplayName("produceRecoil sets camera rotation once when there is no kickback duration")
    void produceRecoil_withoutKickbackDuration() {
        RecoilReceiver receiver = mock(RecoilReceiver.class);

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        recoil.setHorizontalRecoilValues(HORIZONTAL_RECOIL);
        recoil.setVerticalRecoilValues(VERTICAL_RECOIL);
        recoil.setRotationDuration(ROTATION_DURATION);
        Location result = recoil.produceRecoil(receiver, direction);

        verify(receiver).modifyCameraRotation(HORIZONTAL_RECOIL[0], VERTICAL_RECOIL[0]);

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
        recoil.setKickbackDuration(KICKBACK_DURATION);

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

        recoil.setHorizontalRecoilValues(HORIZONTAL_RECOIL);
        recoil.setVerticalRecoilValues(VERTICAL_RECOIL);
        recoil.setKickbackDuration(KICKBACK_DURATION);
        recoil.setRecoveryDuration(RECOVERY_DURATION);
        recoil.setRecoveryRate(RECOVERY_RATE);
        recoil.setRotationDuration(ROTATION_DURATION);
        Location result = recoil.produceRecoil(receiver, direction);

        ArgumentCaptor<CameraMovementTask> taskCaptor = ArgumentCaptor.forClass(CameraMovementTask.class);
        verify(timer).scheduleAtFixedRate(taskCaptor.capture(), eq(0L), eq(20L));

        assertThat(taskCaptor.getValue()).satisfies(task -> {
            assertThat(task.getRecoilRotations()).isEqualTo(10);
            assertThat(task.getRecoveryRate()).isEqualTo(RECOVERY_RATE);
            assertThat(task.getRecoveryRotations()).isEqualTo(15);
            assertThat(task.getYawRotation()).isEqualTo(0.1f);
            assertThat(task.getPitchRotation()).isEqualTo(0.2f);
        });

        assertThat(result).isEqualTo(direction);
    }
}
