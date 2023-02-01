package com.github.matsgemmeke.battlegounds.item.mechanism;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import com.github.matsgemmeke.battlegrounds.item.mechanism.SemiAutomaticMode;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class SemiAutomaticModeTest {

    private Firearm firearm;
    private long cooldown;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        this.taskRunner = mock(TaskRunner.class);
        this.firearm = mock(Firearm.class);
        this.cooldown = 1;
    }

    @Test
    public void shootsAndExecutesCooldown() {
        SemiAutomaticMode semiAutomaticMode = new SemiAutomaticMode(taskRunner, firearm, cooldown);
        semiAutomaticMode.activate();

        verify(taskRunner).runTaskLater(any(Runnable.class), eq(cooldown));
    }

    @Test
    public void shouldNotShootAgainDuringCooldown() {
        SemiAutomaticMode semiAutomaticMode = new SemiAutomaticMode(taskRunner, firearm, cooldown);
        semiAutomaticMode.activate();
        semiAutomaticMode.activate();

        verify(firearm, times(1)).shoot();
    }
}
