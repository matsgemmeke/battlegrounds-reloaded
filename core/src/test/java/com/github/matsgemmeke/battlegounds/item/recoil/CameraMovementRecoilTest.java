package com.github.matsgemmeke.battlegounds.item.recoil;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.entity.ItemHolder;
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
        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getRelativeAccuracy()).thenReturn(1.0);

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        CameraMovementRecoil recoil = new CameraMovementRecoil(internals, timer);
        Location result = recoil.produceRecoil(holder, direction);

        verify(internals, never()).setPlayerRotation(any(), anyFloat(), anyFloat());

        assertEquals(direction, result);
    }

    @Test
    public void onlySetsPlayerRotationWhenThereIsNoRotationDuration() {
        ItemHolder holder = mock(GamePlayer.class);
        when(holder.getRelativeAccuracy()).thenReturn(1.0);

        Float[] horizontalRecoil = new Float[] { 1.0f };
        Float[] verticalRecoil = new Float[] { 1.0f };

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        CameraMovementRecoil recoil = new CameraMovementRecoil(internals, timer);
        recoil.setHorizontalRecoilValues(horizontalRecoil);
        recoil.setVerticalRecoilValues(verticalRecoil);

        Location result = recoil.produceRecoil(holder, direction);

        verify(internals, times(1)).setPlayerRotation(any(), eq(1.0f), eq(1.0f));

        assertEquals(direction, result);
    }

    @Test
    public void schedulesTaskForSmoothPlayerRotation() {
        ItemHolder holder = mock(GamePlayer.class);
        when(holder.getEntity()).thenReturn(mock(Player.class));
        when(holder.getRelativeAccuracy()).thenReturn(1.0);

        Location direction = new Location(null, 0, 0, 0, 90.0f, 90.0f);

        CameraMovementRecoil recoil = new CameraMovementRecoil(internals, timer);
        recoil.setHorizontalRecoilValues(new Float[] { 1.0f });
        recoil.setVerticalRecoilValues(new Float[] { 1.0f });
        recoil.setRecoilDuration(200);
        recoil.setRecoveryDuration(300);
        recoil.setRecoveryRate(0.5f);

        Location result = recoil.produceRecoil(holder, direction);

        verify(timer, times(1)).scheduleAtFixedRate(any(), anyLong(), anyLong());

        assertEquals(direction, result);
    }
}
