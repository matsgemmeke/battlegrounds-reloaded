package com.github.matsgemmeke.battlegounds.item.mechanics;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.ItemHolder;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import com.github.matsgemmeke.battlegrounds.item.mechanics.SemiAutomaticMode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SemiAutomaticModeTest {

    private Gun gun;
    private ItemHolder holder;
    private long cooldownDuration;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        this.gun = mock(Gun.class);
        this.holder = mock(ItemHolder.class);
        this.taskRunner = mock(TaskRunner.class);
        this.cooldownDuration = 1;
    }

    @Test
    public void shootGunOnceWhenActivated() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(taskRunner, gun, cooldownDuration);
        boolean activated = fireMode.activate(holder);

        verify(gun, times(1)).shoot();

        assertTrue(activated);
    }

    @Test
    public void doNothingIfGunIsCoolingDown() {
        SemiAutomaticMode fireMode = new SemiAutomaticMode(taskRunner, gun, cooldownDuration);
        fireMode.activate(holder);
        boolean activated = fireMode.activate(holder);

        assertFalse(activated);
    }
}
