package com.github.matsgemmeke.battlegounds.item.mechanism;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import com.github.matsgemmeke.battlegrounds.api.game.BattleSound;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import com.github.matsgemmeke.battlegrounds.item.mechanism.MagazineReload;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MagazineReloadTest {

    private Gun gun;
    private Iterable<BattleSound> reloadSounds;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        this.gun = mock(Gun.class);
        this.taskRunner = mock(TaskRunner.class);
        this.reloadSounds = Collections.singletonList(mock(BattleSound.class));
    }

    @Test
    public void doNothingIfGunHasNoHolder() {
        MagazineReload magazineReload = new MagazineReload(taskRunner, gun, reloadSounds, 0);
        boolean activated = magazineReload.activate();

        assertFalse(activated);
    }

    @Test
    public void performReloadWhenGunHasHolder() {
        BattleItemHolder holder = mock(BattleItemHolder.class);

        when(gun.getHolder()).thenReturn(holder);

        MagazineReload magazineReload = new MagazineReload(taskRunner, gun, reloadSounds, 0);
        boolean activated = magazineReload.activate();

        assertTrue(activated);
    }

    @Test
    public void cancellingReloadRemovesAllTasksAndResetsGun() {
        BattleItemHolder holder = mock(BattleItemHolder.class);
        BukkitTask task = mock(BukkitTask.class);

        when(gun.getHolder()).thenReturn(holder);
        when(taskRunner.runTaskLater(any(Runnable.class), anyLong())).thenReturn(task);
        when(taskRunner.runTaskTimer(any(Runnable.class), anyLong(), anyLong())).thenReturn(task);

        MagazineReload magazineReload = new MagazineReload(taskRunner, gun, reloadSounds, 0);
        magazineReload.activate();
        magazineReload.cancel();

        verify(task, atLeast(2)).cancel();

        assertFalse(gun.isReloading());
    }

    @Test
    public void refillMagazineCompletelyWhenEnoughReserveAmmo() {
        when(gun.getMagazineAmmo()).thenReturn(0);
        when(gun.getMagazineSize()).thenReturn(30);
        when(gun.getReserveAmmo()).thenReturn(90);

        MagazineReload magazineReload = new MagazineReload(taskRunner, gun, reloadSounds, 0);
        magazineReload.performReload();

        verify(gun).setMagazineAmmo(30);
        verify(gun).setReserveAmmo(60);
        verify(gun).setReloading(false);
    }

    @Test
    public void refillMagazinePartiallyWhenNotEnoughReserveAmmo() {
        when(gun.getMagazineAmmo()).thenReturn(0);
        when(gun.getMagazineSize()).thenReturn(30);
        when(gun.getReserveAmmo()).thenReturn(10);

        MagazineReload magazineReload = new MagazineReload(taskRunner, gun, reloadSounds, 0);
        magazineReload.performReload();

        verify(gun).setMagazineAmmo(10);
        verify(gun).setReserveAmmo(0);
        verify(gun).setReloading(false);
    }
}
