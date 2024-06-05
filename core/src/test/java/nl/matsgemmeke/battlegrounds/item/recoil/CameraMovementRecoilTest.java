package nl.matsgemmeke.battlegrounds.item.recoil;

import nl.matsgemmeke.battlegrounds.entity.RecoilReceiver;
import org.bukkit.Location;
import org.junit.Before;
import org.junit.Test;

import java.util.Timer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CameraMovementRecoilTest {

    private Timer timer;

    @Before
    public void setUp() {
        this.timer = mock(Timer.class);
    }

    @Test
    public void setsCameraRotationOnceWhenThereIsNoKickbackDuration() {
        RecoilReceiver receiver = mock(RecoilReceiver.class);
        when(receiver.getRelativeAccuracy()).thenReturn(1.0f);

        Float[] horizontalRecoil = new Float[] { 1.0f };
        Float[] verticalRecoil = new Float[] { 1.0f };

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        CameraMovementRecoil recoil = new CameraMovementRecoil(timer);
        recoil.setHorizontalRecoilValues(horizontalRecoil);
        recoil.setVerticalRecoilValues(verticalRecoil);
        recoil.setRotationDuration(20);

        Location result = recoil.produceRecoil(receiver, direction);

        verify(receiver).modifyCameraRotation(1.0f, 1.0f);

        assertEquals(direction, result);
    }

    @Test
    public void setsCameraRotationOnceWhenThereIsNoRotationDuration() {
        RecoilReceiver receiver = mock(RecoilReceiver.class);
        when(receiver.getRelativeAccuracy()).thenReturn(1.0f);

        Float[] horizontalRecoil = new Float[] { 1.0f };
        Float[] verticalRecoil = new Float[] { 1.0f };

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        CameraMovementRecoil recoil = new CameraMovementRecoil(timer);
        recoil.setHorizontalRecoilValues(horizontalRecoil);
        recoil.setVerticalRecoilValues(verticalRecoil);
        recoil.setKickbackDuration(200);

        Location result = recoil.produceRecoil(receiver, direction);

        verify(receiver).modifyCameraRotation(1.0f, 1.0f);

        assertEquals(direction, result);
    }

    @Test
    public void schedulesTaskForSmoothCameraRotation() {
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

        assertEquals(direction, result);
    }
}
