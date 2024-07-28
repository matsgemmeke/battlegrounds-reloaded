package nl.matsgemmeke.battlegrounds.item.mechanism;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemMechanismFactoryTest {

    private Section section;

    @Before
    public void setUp() {
        section = mock(Section.class);
    }

    @Test
    public void shouldCreateInstanceForExplosionMechanismType() {
        when(section.getString("type")).thenReturn("EXPLOSION");

        ItemMechanismFactory factory = new ItemMechanismFactory();
        ItemMechanism mechanism = factory.make(section);

        assertNotNull(mechanism);
        assertTrue(mechanism instanceof ExplosionMechanism);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void shouldThrowExceptionIfGivenActivationTypeIsNotDefined() {
        when(section.getString("type")).thenReturn(null);

        ItemMechanismFactory factory = new ItemMechanismFactory();
        factory.make(section);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void shouldThrowExceptionIfGivenActivationTypeIsIncorrect() {
        when(section.getString("type")).thenReturn("fail");

        ItemMechanismFactory factory = new ItemMechanismFactory();
        factory.make(section);
    }
}
