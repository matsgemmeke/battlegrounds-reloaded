package com.github.matsgemmeke.battlegounds.item.mechanics;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import com.github.matsgemmeke.battlegrounds.item.mechanics.AutomaticFireCycleRunnable;
import com.github.matsgemmeke.battlegrounds.item.mechanics.BurstMode;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class BurstModeTest {

    private Firearm firearm;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        this.taskRunner = mock(TaskRunner.class);
        this.firearm = mock(Firearm.class);
    }

    @Test
    public void activatesWithCorrectDelayAndPeriod() {
        int roundAmount = 3;
        int rateOfFire = 600;

        BurstMode burstMode = new BurstMode(taskRunner, firearm, roundAmount, rateOfFire);
        burstMode.activate();

        verify(taskRunner, times(1)).runTaskTimer(any(AutomaticFireCycleRunnable.class), eq(0L), eq(2L));
    }

    @Test
    public void doesNotActivateWhenNotReady() {
        int roundAmount = 3;
        int rateOfFire = 600;

        BurstMode burstMode = new BurstMode(taskRunner, firearm, roundAmount, rateOfFire);
        burstMode.activate();
        burstMode.activate();

        verify(taskRunner, times(1)).runTaskTimer(any(), anyLong(), anyLong());
    }
}
