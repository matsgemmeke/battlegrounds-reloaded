package nl.matsgemmeke.battlegrounds.item.recoil;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.RecoilSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class RecoilFactoryTest {

    private BattlegroundsConfiguration config;

    @BeforeEach
    public void setUp() {
        this.config = mock(BattlegroundsConfiguration.class);
    }

    @Test
    public void createReturnsCameraMovementRecoilInstanceBasedOnSpecification() {
        RecoilSpec spec = new RecoilSpec("CAMERA_MOVEMENT", List.of(), List.of(), 1L, 0.5f, 1L);

        RecoilFactory factory = new RecoilFactory(config);
        Recoil recoil = factory.create(spec);

        assertThat(recoil).isInstanceOf(CameraMovementRecoil.class);
    }

    @Test
    public void createReturnsRandomSpreadRecoilInstanceBasedOnSpecification() {
        RecoilSpec spec = new RecoilSpec("RANDOM_SPREAD", List.of(), List.of(), null, null, null);

        RecoilFactory factory = new RecoilFactory(config);
        Recoil recoil = factory.create(spec);

        assertThat(recoil).isInstanceOf(RandomSpreadRecoil.class);
    }
}
