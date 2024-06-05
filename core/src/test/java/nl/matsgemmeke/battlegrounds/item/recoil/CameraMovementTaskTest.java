package nl.matsgemmeke.battlegrounds.item.recoil;

import nl.matsgemmeke.battlegrounds.entity.RecoilReceiver;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class CameraMovementTaskTest {

    private RecoilReceiver receiver;

    @Before
    public void setUp() {
        this.receiver = mock(RecoilReceiver.class);
    }

    @Test
    public void shouldStopTaskWhenReceiverCanNotReceiveRecoil() {
        CameraMovementTask task = new CameraMovementTask(receiver);
        task.run();

        verify(receiver, never()).modifyCameraRotation(anyFloat(), anyFloat());
    }

    @Test
    public void shouldOnlyRotateReceiverAsManyTimesAsGivenRotationAmount() {
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
    public void shouldApplyRecoveryAndKeepRotatingWhenRecoveryRateIsSet() {
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
