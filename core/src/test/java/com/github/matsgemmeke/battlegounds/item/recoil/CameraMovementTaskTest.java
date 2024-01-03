package com.github.matsgemmeke.battlegounds.item.recoil;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.item.recoil.CameraMovementTask;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class CameraMovementTaskTest {

    private InternalsProvider internals;
    private Player player;

    @Before
    public void setUp() {
        this.internals = mock(InternalsProvider.class);
        this.player = mock(Player.class);
    }

    @Test
    public void shouldStopTaskWhenPlayerIsDead() {
        when(player.isDead()).thenReturn(true);

        CameraMovementTask task = new CameraMovementTask(player, internals);
        task.run();

        verify(internals, never()).setPlayerRotation(eq(player), anyFloat(), anyFloat());
    }

    @Test
    public void shouldStopTaskWhenPlayerIsOffline() {
        when(player.isOnline()).thenReturn(false);

        CameraMovementTask task = new CameraMovementTask(player, internals);
        task.run();

        verify(internals, never()).setPlayerRotation(eq(player), anyFloat(), anyFloat());
    }

    @Test
    public void shouldOnlyRotatePlayerAsManyTimesAsGivenRotationAmount() {
        when(player.isDead()).thenReturn(false);
        when(player.isOnline()).thenReturn(true);

        CameraMovementTask task = new CameraMovementTask(player, internals);
        task.setRecoilRotations(2);
        task.setPitchRotation(1.0f);
        task.setYawRotation(1.0f);
        task.run();
        task.run();
        task.run();

        verify(internals, times(2)).setPlayerRotation(player, 1.0f, 1.0f);
    }

    @Test
    public void shouldApplyRecoveryAndKeepRotatingWhenRecoveryRateIsSet() {
        when(player.isDead()).thenReturn(false);
        when(player.isOnline()).thenReturn(true);

        CameraMovementTask task = new CameraMovementTask(player, internals);
        task.setRecoilRotations(2);
        task.setRecoveryRate(0.5f);
        task.setRecoveryRotations(4);
        task.setPitchRotation(1.0f);
        task.setYawRotation(1.0f);

        for (int i = 0; i < 6; i++) {
            task.run();
        }

        verify(internals, times(2)).setPlayerRotation(player, 1.0f, 1.0f);
        verify(internals, times(4)).setPlayerRotation(player, -0.25f, -0.25f);
    }
}
