package com.github.matsgemmeke.battlegounds.item.mechanism;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import com.github.matsgemmeke.battlegrounds.item.mechanism.AutomaticFireCycleRunnable;
import com.github.matsgemmeke.battlegrounds.item.mechanism.FullyAutomaticMode;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class FullyAutomaticModeTest {

    private Firearm firearm;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        this.taskRunner = mock(TaskRunner.class);
        this.firearm = mock(Firearm.class);
    }

    @Test
    public void activatesWithCorrectDelayAndPeriod() {
        int rateOfFire = 1200;

        FullyAutomaticMode fullyAutomaticMode = new FullyAutomaticMode(taskRunner, firearm, rateOfFire);
        fullyAutomaticMode.activate();

        verify(taskRunner, times(1)).runTaskTimer(any(AutomaticFireCycleRunnable.class), eq(0L), eq(1L));
    }

    @Test
    public void doesNotActivateWhenNotReady() {
        int rateOfFire = 600;

        FullyAutomaticMode fullyAutomaticMode = new FullyAutomaticMode(taskRunner, firearm, rateOfFire);
        fullyAutomaticMode.activate();
        fullyAutomaticMode.activate();

        verify(taskRunner, times(1)).runTaskTimer(any(), anyLong(), anyLong());
    }
}
