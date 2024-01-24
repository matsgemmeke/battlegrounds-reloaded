package com.github.matsgemmeke.battlegounds.item.mechanics;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.ItemHolder;
import com.github.matsgemmeke.battlegrounds.api.game.GameSound;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import com.github.matsgemmeke.battlegrounds.item.mechanics.MagazineReload;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MagazineReloadTest {

    private Gun gun;
    private ItemHolder holder;
    private Iterable<GameSound> reloadSounds;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        this.holder = mock(ItemHolder.class);
        this.gun = mock(Gun.class);
        this.taskRunner = mock(TaskRunner.class);
        this.reloadSounds = Collections.singletonList(mock(GameSound.class));
    }

    @Test
    public void performReloadWhenGunHasHolder() {
        MagazineReload magazineReload = new MagazineReload(taskRunner, gun, reloadSounds, 0);
        boolean activated = magazineReload.activate(holder);

        assertTrue(activated);
    }

    @Test
    public void cancellingReloadRemovesAllTasksAndResetsGun() {
        BukkitTask task = mock(BukkitTask.class);

        when(taskRunner.runTaskLater(any(Runnable.class), anyLong())).thenReturn(task);
        when(taskRunner.runTaskTimer(any(Runnable.class), anyLong(), anyLong())).thenReturn(task);

        MagazineReload magazineReload = new MagazineReload(taskRunner, gun, reloadSounds, 0);
        magazineReload.activate(holder);
        magazineReload.cancel(holder);

        verify(task, atLeast(2)).cancel();

        assertNull(gun.getOperatingMode());
    }

    @Test
    public void refillMagazineCompletelyWhenEnoughReserveAmmo() {
        when(gun.getMagazineAmmo()).thenReturn(0);
        when(gun.getMagazineSize()).thenReturn(30);
        when(gun.getReserveAmmo()).thenReturn(90);

        MagazineReload magazineReload = new MagazineReload(taskRunner, gun, reloadSounds, 0);
        magazineReload.performReload(holder);

        verify(gun).setMagazineAmmo(30);
        verify(gun).setReserveAmmo(60);
        verify(gun).setOperatingMode(null);
    }

    @Test
    public void refillMagazinePartiallyWhenNotEnoughReserveAmmo() {
        when(gun.getMagazineAmmo()).thenReturn(0);
        when(gun.getMagazineSize()).thenReturn(30);
        when(gun.getReserveAmmo()).thenReturn(10);

        MagazineReload magazineReload = new MagazineReload(taskRunner, gun, reloadSounds, 0);
        magazineReload.performReload(holder);

        verify(gun).setMagazineAmmo(10);
        verify(gun).setReserveAmmo(0);
        verify(gun).setOperatingMode(null);
    }
}
