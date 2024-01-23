package com.github.matsgemmeke.battlegounds.item.recoil;

import com.github.matsgemmeke.battlegrounds.api.entity.ItemHolder;
import com.github.matsgemmeke.battlegrounds.item.recoil.RandomSpreadRecoil;
import org.bukkit.Location;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RandomSpreadRecoilTest {

    @Test
    public void producesRecoilBasedOnModifyingDirection() {
        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getRelativeAccuracy()).thenReturn(1.0f);

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        RandomSpreadRecoil recoil = new RandomSpreadRecoil();
        recoil.setHorizontalRecoilValues(new Float[] { 5.0f, 10.0f });
        recoil.setVerticalRecoilValues(new Float[] { 5.0f, 10.0f });

        Location result = recoil.produceRecoil(holder, direction);

        assertTrue(result.getYaw() >= 80.0f);
        assertTrue(result.getYaw() <= 100.0f);
        assertTrue(result.getPitch() >= 80.0f);
        assertTrue(result.getPitch() <= 100.0f);
    }

    @Test
    public void modifyingRelativeAccuracyChangesDirectionDeviation() {
        ItemHolder holderAccurate = mock(ItemHolder.class);
        when(holderAccurate.getRelativeAccuracy()).thenReturn(2.0f);

        ItemHolder holderNotAccurate = mock(ItemHolder.class);
        when(holderNotAccurate.getRelativeAccuracy()).thenReturn(0.5f);

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        RandomSpreadRecoil recoil = new RandomSpreadRecoil();
        recoil.setHorizontalRecoilValues(new Float[] { 5.0f, 10.0f });
        recoil.setVerticalRecoilValues(new Float[] { 5.0f, 10.0f });

        Location result1 = recoil.produceRecoil(holderAccurate, direction.clone());
        Location result2 = recoil.produceRecoil(holderNotAccurate, direction.clone());

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
