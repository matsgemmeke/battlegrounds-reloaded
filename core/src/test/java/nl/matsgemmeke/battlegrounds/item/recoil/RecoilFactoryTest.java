package nl.matsgemmeke.battlegrounds.item.recoil;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.RecoilSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        RecoilSpec spec = new RecoilSpec();
        spec.type = "CAMERA_MOVEMENT";
        spec.horizontal = new Float[] { 0.1f };
        spec.vertical = new Float[] { 0.2f };
        spec.kickbackDuration = 1L;
        spec.recoveryRate = 0.5f;
        spec.recoveryDuration = 1L;

        RecoilFactory factory = new RecoilFactory(config);
        Recoil recoil = factory.create(spec);

        assertThat(recoil).isInstanceOf(CameraMovementRecoil.class);
    }

    @Test
    public void createReturnsRandomSpreadRecoilInstanceBasedOnSpecification() {
        RecoilSpec spec = new RecoilSpec();
        spec.type = "RANDOM_SPREAD";
        spec.horizontal = new Float[] { 0.1f };
        spec.vertical = new Float[] { 0.2f };

        RecoilFactory factory = new RecoilFactory(config);
        Recoil recoil = factory.create(spec);

        assertThat(recoil).isInstanceOf(RandomSpreadRecoil.class);
    }
}
