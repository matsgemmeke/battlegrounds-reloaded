package nl.matsgemmeke.battlegrounds.item.shoot;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.WeaponFactoryCreationException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FireModeFactoryTest {

    private Section section;
    private Shootable item;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        section = mock(Section.class);
        item = mock(Shootable.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void makeFireModeInstanceForBurstMode() {
        when(section.getString("type")).thenReturn("BURST_MODE");

        FireModeFactory factory = new FireModeFactory(taskRunner);
        FireMode fireMode = factory.make(item, section);

        assertNotNull(fireMode);
    }

    @Test
    public void makeFireModeInstanceForFullyAutomatic() {
        when(section.getString("type")).thenReturn("FULLY_AUTOMATIC");

        FireModeFactory factory = new FireModeFactory(taskRunner);
        FireMode fireMode = factory.make(item, section);

        assertNotNull(fireMode);
    }

    @Test
    public void makeFireModeInstanceForSemiAutomatic() {
        when(section.getString("type")).thenReturn("SEMI_AUTOMATIC");

        FireModeFactory factory = new FireModeFactory(taskRunner);
        FireMode fireMode = factory.make(item, section);

        assertNotNull(fireMode);
    }

    @Test(expected = WeaponFactoryCreationException.class)
    public void throwErrorWhenUnknownFireModeType() {
        when(section.getString("type")).thenReturn("error");

        FireModeFactory factory = new FireModeFactory(taskRunner);
        factory.make(item, section);
    }
}
