package com.github.matsgemmeke.battlegounds.item.mechanism;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.game.BattleSound;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import com.github.matsgemmeke.battlegrounds.item.DefaultFirearm;
import com.github.matsgemmeke.battlegrounds.item.mechanism.ManualReload;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ManualReloadTest {

    private Iterable<BattleSound> reloadSounds;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        this.taskRunner = mock(TaskRunner.class);
        this.reloadSounds = Collections.singletonList(mock(BattleSound.class));
    }

    @Test
    public void doNothingIfGunHasNoHolder() {
        Gun gun = mock(Gun.class);

        ManualReload manualReload = new ManualReload(taskRunner, gun, reloadSounds, 0);
        boolean activated = manualReload.activate();

        assertFalse(activated);
    }

    @Test
    public void performReloadWhenGunHasHolder() {
        BattleContext context = mock(BattleContext.class);
        BattleItemHolder holder = mock(BattleItemHolder.class);

        Gun gun = new DefaultFirearm("id", "name", context);
        gun.setHolder(holder);

        ManualReload manualReload = new ManualReload(taskRunner, gun, reloadSounds, 0);
        boolean activated = manualReload.activate();

        assertTrue(activated);
    }

    @Test
    public void cancellingReloadRemovesAllTasksAndResetsGun() {
        BattleContext context = mock(BattleContext.class);
        BattleItemHolder holder = mock(BattleItemHolder.class);
        BukkitTask task = mock(BukkitTask.class);

        Gun gun = new DefaultFirearm("id", "name", context);
        gun.setHolder(holder);

        when(taskRunner.runTaskLater(any(Runnable.class), anyLong())).thenReturn(task);
        when(taskRunner.runTaskTimer(any(Runnable.class), anyLong(), anyLong())).thenReturn(task);

        ManualReload manualReload = new ManualReload(taskRunner, gun, reloadSounds, 0);
        manualReload.activate();
        manualReload.cancel();

        verify(task, atLeast(2)).cancel();

        assertFalse(gun.isReloading());
    }

    @Test
    public void addsSingleAmmoWithEachIteration() {
        BattleContext context = mock(BattleContext.class);

        Gun gun = new DefaultFirearm("id", "name", context);
        gun.setMagazineAmmo(0);
        gun.setMagazineSize(10);
        gun.setReserveAmmo(1);

        ManualReload manualReload = new ManualReload(taskRunner, gun, reloadSounds, 0);
        manualReload.performReload();

        assertEquals(1, gun.getMagazineAmmo());
        assertEquals(0, gun.getReserveAmmo());
    }
}
