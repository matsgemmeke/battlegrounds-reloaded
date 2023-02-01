package com.github.matsgemmeke.battlegounds.item.factory;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import com.github.matsgemmeke.battlegrounds.item.factory.FiringModeFactory;
import com.github.matsgemmeke.battlegrounds.item.factory.WeaponFactoryCreationException;
import com.github.matsgemmeke.battlegrounds.item.mechanism.FiringMode;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FiringModeFactoryTest {

    private Firearm firearm;
    private Section section;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        this.firearm = mock(Firearm.class);
        this.section = mock(Section.class);
        this.taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void makeFiringModeInstanceForBurstMode() {
        when(section.getString("type")).thenReturn("BURST_MODE");

        FiringModeFactory factory = new FiringModeFactory(taskRunner);
        factory.make(firearm, section);
    }

    @Test
    public void makeFiringModeInstanceForFullyAutomatic() {
        when(section.getString("type")).thenReturn("FULLY_AUTOMATIC");

        FiringModeFactory factory = new FiringModeFactory(taskRunner);
        FiringMode firingMode = factory.make(firearm, section);

        assertNotNull(firingMode);
    }

    @Test
    public void makeFiringModeInstanceForSemiAutomatic() {
        when(section.getString("type")).thenReturn("SEMI_AUTOMATIC");

        FiringModeFactory factory = new FiringModeFactory(taskRunner);
        FiringMode firingMode = factory.make(firearm, section);

        assertNotNull(firingMode);
    }

    @Test(expected = WeaponFactoryCreationException.class)
    public void throwErrorWhenUnknownFiringModeType() {
        when(section.getString("type")).thenReturn("error");

        FiringModeFactory factory = new FiringModeFactory(taskRunner);
        FiringMode firingMode = factory.make(firearm, section);

        assertNotNull(firingMode);
    }
}
