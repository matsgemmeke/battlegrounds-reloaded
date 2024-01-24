package com.github.matsgemmeke.battlegounds.item.mechanics;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.ItemHolder;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.api.game.GameSound;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import com.github.matsgemmeke.battlegrounds.item.DefaultFirearm;
import com.github.matsgemmeke.battlegrounds.item.mechanics.ManualReload;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ManualReloadTest {

    private ItemHolder holder;
    private Iterable<GameSound> reloadSounds;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        this.holder = mock(ItemHolder.class);
        this.taskRunner = mock(TaskRunner.class);
        this.reloadSounds = Collections.singletonList(mock(GameSound.class));
    }

    @Test
    public void performReloadWhenGunHasHolder() {
        GameContext context = mock(GameContext.class);

        Gun gun = new DefaultFirearm(context);
        gun.setHolder(holder);

        ManualReload manualReload = new ManualReload(taskRunner, gun, reloadSounds, 0);
        boolean activated = manualReload.activate(holder);

        assertTrue(activated);
    }

    @Test
    public void cancellingReloadRemovesAllTasksAndResetsGun() {
        GameContext context = mock(GameContext.class);
        BukkitTask task = mock(BukkitTask.class);

        Gun gun = new DefaultFirearm(context);
        gun.setHolder(holder);

        when(taskRunner.runTaskLater(any(Runnable.class), anyLong())).thenReturn(task);
        when(taskRunner.runTaskTimer(any(Runnable.class), anyLong(), anyLong())).thenReturn(task);

        ManualReload manualReload = new ManualReload(taskRunner, gun, reloadSounds, 0);
        manualReload.activate(holder);
        manualReload.cancel(holder);

        verify(task, atLeast(2)).cancel();

        assertNull(gun.getOperatingMode());
    }

    @Test
    public void addsSingleAmmoWithEachIteration() {
        GameContext context = mock(GameContext.class);

        Gun gun = new DefaultFirearm(context);
        gun.setMagazineAmmo(0);
        gun.setMagazineSize(10);
        gun.setReserveAmmo(1);

        ManualReload manualReload = new ManualReload(taskRunner, gun, reloadSounds, 0);
        manualReload.performReload(holder);

        assertEquals(1, gun.getMagazineAmmo());
        assertEquals(0, gun.getReserveAmmo());
    }
}
