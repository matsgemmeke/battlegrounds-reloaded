package com.github.matsgemmeke.battlegounds.item.recoil;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import com.github.matsgemmeke.battlegrounds.item.recoil.RandomSpreadRecoil;
import org.bukkit.Location;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RandomSpreadRecoilTest {

    @Test
    public void producesRecoilBasedOnModifyingDirection() {
        BattleItemHolder holder = mock(BattleItemHolder.class);

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        RandomSpreadRecoil recoil = new RandomSpreadRecoil();
        recoil.setHorizontalRecoil(10.0f);
        recoil.setVerticalRecoil(10.0f);

        Location result = recoil.produceRecoil(holder, direction, 1.0);

        assertTrue(result.getYaw() >= 85.0f);
        assertTrue(result.getYaw() <= 95.0f);
        assertTrue(result.getPitch() >= 85.0f);
        assertTrue(result.getPitch() <= 95.0f);
    }

    @Test
    public void modifyingRelativeAccuracyChangesDirectionDeviation() {
        BattleItemHolder holder = mock(BattleItemHolder.class);

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        RandomSpreadRecoil recoil = new RandomSpreadRecoil();
        recoil.setHorizontalRecoil(10.0f);
        recoil.setVerticalRecoil(10.0f);

        Location result1 = recoil.produceRecoil(holder, direction.clone(), 2.0);
        Location result2 = recoil.produceRecoil(holder, direction.clone(), 0.5);

        assertTrue(result1.getYaw() >= 87.5f);
        assertTrue(result1.getYaw() <= 92.5f);
        assertTrue(result1.getPitch() >= 87.5f);
        assertTrue(result1.getPitch() <= 92.5f);
        assertTrue(result2.getYaw() >= 80.0f);
        assertTrue(result2.getYaw() <= 90.0f);
        assertTrue(result2.getPitch() >= 80.0f);
        assertTrue(result2.getPitch() <= 90.0f);
    }
}
