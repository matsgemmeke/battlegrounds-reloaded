package nl.matsgemmeke.battlegrounds.item.equipment.mechanism;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.item.InvalidItemConfigurationException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EquipmentMechanismFactoryTest {

    private GameContext context;
    private Section section;

    @Before
    public void setUp() {
        context = mock(GameContext.class);
        section = mock(Section.class);
    }

    @Test
    public void shouldCreateInstanceForExplosionMechanismType() {
        when(section.getString("type")).thenReturn("EXPLOSION");

        EquipmentMechanismFactory factory = new EquipmentMechanismFactory();
        EquipmentMechanism mechanism = factory.make(section, context);

        assertNotNull(mechanism);
        assertTrue(mechanism instanceof ExplosionMechanism);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void shouldThrowExceptionIfGivenActivationTypeIsNotDefined() {
        when(section.getString("type")).thenReturn(null);

        EquipmentMechanismFactory factory = new EquipmentMechanismFactory();
        factory.make(section, context);
    }

    @Test(expected = InvalidItemConfigurationException.class)
    public void shouldThrowExceptionIfGivenActivationTypeIsIncorrect() {
        when(section.getString("type")).thenReturn("fail");

        EquipmentMechanismFactory factory = new EquipmentMechanismFactory();
        factory.make(section, context);
    }
}
