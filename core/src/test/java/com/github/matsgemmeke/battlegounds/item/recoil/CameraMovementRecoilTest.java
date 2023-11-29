package com.github.matsgemmeke.battlegounds.item.recoil;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.item.recoil.CameraMovementRecoil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.Timer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CameraMovementRecoilTest {

    private InternalsProvider internals;
    private Timer timer;

    @Before
    public void setUp() {
        this.internals = mock(InternalsProvider.class);
        this.timer = mock(Timer.class);
    }

    @Test
    public void doesNoRecoilToNonPlayerEntities() {
        BattleItemHolder holder = mock(BattleItemHolder.class);

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        CameraMovementRecoil recoil = new CameraMovementRecoil(internals, timer);
        Location result = recoil.produceRecoil(holder, direction, 1.0);

        verify(internals, never()).setPlayerRotation(any(), anyFloat(), anyFloat());

        assertEquals(direction, result);
    }

    @Test
    public void onlySetsPlayerRotationWhenThereIsNoRotationDuration() {
        BattleItemHolder holder = mock(BattlePlayer.class);

        float horizontalRecoil = 1.0f;
        float verticalRecoil = 1.0f;

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        CameraMovementRecoil recoil = new CameraMovementRecoil(internals, timer);
        recoil.setHorizontalRecoil(horizontalRecoil);
        recoil.setVerticalRecoil(verticalRecoil);

        Location result = recoil.produceRecoil(holder, direction, 1.0);

        verify(internals, times(1)).setPlayerRotation(any(), eq(horizontalRecoil), eq(verticalRecoil));

        assertEquals(direction, result);
    }

    @Test
    public void schedulesTaskForSmoothPlayerRotation() {
        BattlePlayer holder = mock(BattlePlayer.class);
        when(holder.getEntity()).thenReturn(mock(Player.class));

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        CameraMovementRecoil recoil = new CameraMovementRecoil(internals, timer);
        recoil.setRotationDuration(100);

        Location result = recoil.produceRecoil(holder, direction, 1.0);

        verify(timer, times(1)).scheduleAtFixedRate(any(), anyLong(), anyLong());

        assertEquals(direction, result);
    }
}
