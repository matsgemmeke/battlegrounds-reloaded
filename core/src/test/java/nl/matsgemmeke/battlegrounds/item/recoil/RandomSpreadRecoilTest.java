package nl.matsgemmeke.battlegrounds.item.recoil;

import nl.matsgemmeke.battlegrounds.entity.RecoilReceiver;
import org.bukkit.Location;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RandomSpreadRecoilTest {

    @Test
    public void producesRecoilBasedOnModifyingDirection() {
        RecoilReceiver receiver = mock(RecoilReceiver.class);
        when(receiver.getRelativeAccuracy()).thenReturn(1.0f);

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        RandomSpreadRecoil recoil = new RandomSpreadRecoil();
        recoil.setHorizontalRecoilValues(new Float[] { 5.0f, 10.0f });
        recoil.setVerticalRecoilValues(new Float[] { 5.0f, 10.0f });

        Location result = recoil.produceRecoil(receiver, direction);

        assertTrue(result.getYaw() >= 80.0f);
        assertTrue(result.getYaw() <= 100.0f);
        assertTrue(result.getPitch() >= 80.0f);
        assertTrue(result.getPitch() <= 100.0f);
    }

    @Test
    public void modifyingRelativeAccuracyChangesDirectionDeviation() {
        RecoilReceiver receiverAccurate = mock(RecoilReceiver.class);
        when(receiverAccurate.getRelativeAccuracy()).thenReturn(2.0f);

        RecoilReceiver receiverNotAccurate = mock(RecoilReceiver.class);
        when(receiverNotAccurate.getRelativeAccuracy()).thenReturn(0.5f);

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        RandomSpreadRecoil recoil = new RandomSpreadRecoil();
        recoil.setHorizontalRecoilValues(new Float[] { 5.0f, 10.0f });
        recoil.setVerticalRecoilValues(new Float[] { 5.0f, 10.0f });

        Location result1 = recoil.produceRecoil(receiverAccurate, direction.clone());
        Location result2 = recoil.produceRecoil(receiverNotAccurate, direction.clone());

        assertTrue(result1.getYaw() >= 85.0f);
        assertTrue(result1.getYaw() <= 95.0f);
        assertTrue(result1.getPitch() >= 85.0f);
        assertTrue(result1.getPitch() <= 95.0f);
        assertTrue(result2.getYaw() >= 70.0f);
        assertTrue(result2.getYaw() <= 110.0f);
        assertTrue(result2.getPitch() >= 70.0f);
        assertTrue(result2.getPitch() <= 110.0f);
    }
}
