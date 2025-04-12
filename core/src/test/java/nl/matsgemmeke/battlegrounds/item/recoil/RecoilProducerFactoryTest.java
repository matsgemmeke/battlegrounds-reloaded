package nl.matsgemmeke.battlegrounds.item.recoil;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RecoilSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class RecoilProducerFactoryTest {

    private BattlegroundsConfiguration config;

    @BeforeEach
    public void setUp() {
        this.config = mock(BattlegroundsConfiguration.class);
    }

    @Test
    public void createReturnsCameraMovementRecoilInstanceBasedOnSpecification() {
        RecoilSpecification specification = new RecoilSpecification("CAMERA_MOVEMENT", List.of(), List.of(), 1L, 0.5f, 1L);

        RecoilProducerFactory factory = new RecoilProducerFactory(config);
        RecoilProducer recoilProducer = factory.create(specification);

        assertThat(recoilProducer).isInstanceOf(CameraMovementRecoil.class);
    }

    @Test
    public void createReturnsRandomSpreadRecoilInstanceBasedOnSpecification() {
        RecoilSpecification specification = new RecoilSpecification("RANDOM_SPREAD", List.of(), List.of(), null, null, null);

        RecoilProducerFactory factory = new RecoilProducerFactory(config);
        RecoilProducer recoilProducer = factory.create(specification);

        assertThat(recoilProducer).isInstanceOf(RandomSpreadRecoil.class);
    }
}
