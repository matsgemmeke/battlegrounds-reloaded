package nl.matsgemmeke.battlegrounds.item.recoil;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.item.WeaponFactoryCreationException;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecoilProducerFactoryTest {

    private BattlegroundsConfiguration config;

    @Before
    public void setUp() {
        this.config = mock(BattlegroundsConfiguration.class);
    }

    @Test
    public void isAbleToCreateRandomSpreadInstance() {
        Section section = mock(Section.class);
        when(section.getFloatList("horizontal")).thenReturn(Arrays.asList(10.0f, 20.0f));
        when(section.getFloatList("vertical")).thenReturn(Arrays.asList(10.0f, 20.0f));
        when(section.getString("type")).thenReturn("RANDOM_SPREAD");

        RecoilProducerFactory factory = new RecoilProducerFactory(config);
        RecoilProducer recoilProducer = factory.make(section);

        assertNotNull(recoilProducer);
        assertTrue(recoilProducer instanceof RandomSpreadRecoil);
    }

    @Test
    public void isAbleToCreateCameraMovementInstance() {
        Section section = mock(Section.class);
        when(section.getFloatList("horizontal")).thenReturn(Arrays.asList(10.0f, 20.0f));
        when(section.getFloatList("vertical")).thenReturn(Arrays.asList(10.0f, 20.0f));
        when(section.getString("type")).thenReturn("CAMERA_MOVEMENT");

        RecoilProducerFactory factory = new RecoilProducerFactory(config);
        RecoilProducer recoilProducer = factory.make(section);

        assertNotNull(recoilProducer);
        assertTrue(recoilProducer instanceof CameraMovementRecoil);
    }

    @Test(expected = WeaponFactoryCreationException.class)
    public void throwsExceptionWhenCreatingUnknownRecoilProducerType() {
        Section section = mock(Section.class);
        when(section.getString("type")).thenReturn("error");

        RecoilProducerFactory factory = new RecoilProducerFactory(config);
        factory.make(section);
    }
}
