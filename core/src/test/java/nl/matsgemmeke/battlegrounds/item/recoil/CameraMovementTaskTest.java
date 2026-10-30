package nl.matsgemmeke.battlegrounds.item.recoil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CameraMovementTaskTest {

    @Mock
    private RecoilReceiver receiver;
    @InjectMocks
    private CameraMovementTask task;

    @Test
    @DisplayName("run stops task when receiver cannot receive recoil")
    void run_receiverCannotReceiveRecoil() {
        when(receiver.canReceiveRecoil()).thenReturn(false);

        task.run();

        verify(receiver, never()).modifyCameraRotation(anyFloat(), anyFloat());
    }

    @Test
    @DisplayName("run rotates receiver as many times as given rotation amount")
    void run_receiverRotationsEqualsRotationAmount() {
        when(receiver.canReceiveRecoil()).thenReturn(true);

        CameraMovementTask task = new CameraMovementTask(receiver);
        task.setRecoilRotations(2);
        task.setPitchRotation(1.0f);
        task.setYawRotation(1.0f);
        task.run();
        task.run();
        task.run();

        verify(receiver, times(2)).modifyCameraRotation(1.0f, 1.0f);
    }

    @Test
    @DisplayName("run rotates receiver including recovery")
    void run_recovery() {
        when(receiver.canReceiveRecoil()).thenReturn(true);

        CameraMovementTask task = new CameraMovementTask(receiver);
        task.setRecoilRotations(2);
        task.setRecoveryRate(0.5f);
        task.setRecoveryRotations(4);
        task.setPitchRotation(1.0f);
        task.setYawRotation(1.0f);

        for (int i = 0; i < 6; i++) {
            task.run();
        }

        verify(receiver, times(2)).modifyCameraRotation(1.0f, 1.0f);
        verify(receiver, times(4)).modifyCameraRotation(-0.25f, -0.25f);
    }
}
