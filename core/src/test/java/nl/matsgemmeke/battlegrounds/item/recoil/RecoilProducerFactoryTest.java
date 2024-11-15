package nl.matsgemmeke.battlegrounds.item.recoil;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.item.WeaponFactoryCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecoilProducerFactoryTest {

    private BattlegroundsConfiguration config;

    @BeforeEach
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

        assertInstanceOf(RandomSpreadRecoil.class, recoilProducer);
    }

    @Test
    public void isAbleToCreateCameraMovementInstance() {
        Section section = mock(Section.class);
        when(section.getFloatList("horizontal")).thenReturn(Arrays.asList(10.0f, 20.0f));
        when(section.getFloatList("vertical")).thenReturn(Arrays.asList(10.0f, 20.0f));
        when(section.getString("type")).thenReturn("CAMERA_MOVEMENT");

        RecoilProducerFactory factory = new RecoilProducerFactory(config);
        RecoilProducer recoilProducer = factory.make(section);

        assertInstanceOf(CameraMovementRecoil.class, recoilProducer);
    }

    @Test
    public void throwsExceptionWhenCreatingUnknownRecoilProducerType() {
        Section section = mock(Section.class);
        when(section.getString("type")).thenReturn("error");

        RecoilProducerFactory factory = new RecoilProducerFactory(config);

        assertThrows(WeaponFactoryCreationException.class, () -> factory.make(section));
    }
}
