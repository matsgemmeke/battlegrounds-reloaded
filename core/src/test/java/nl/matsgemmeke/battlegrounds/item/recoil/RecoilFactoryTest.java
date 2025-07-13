package nl.matsgemmeke.battlegrounds.item.recoil;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.RecoilSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

public class RecoilFactoryTest {

    private BattlegroundsConfiguration config;

    @BeforeEach
    public void setUp() {
        this.config = mock(BattlegroundsConfiguration.class);
    }

    private static Stream<Arguments> cameraMovementRecoilVariables() {
        return Stream.of(
                arguments("CAMERA_MOVEMENT", new Float[] { 0.1f }, new Float[] { 0.2f }, 1L, 0.5f, 1L),
                arguments("CAMERA_MOVEMENT", new Float[] { 0.1f }, new Float[] { 0.2f }, null, null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("cameraMovementRecoilVariables")
    public void createReturnsCameraMovementRecoilInstanceBasedOnSpecification(String type, Float[] horizontal, Float[] vertical, Long kickbackDuration, Float recoveryRate, Long recoveryDuration) {
        RecoilSpec spec = new RecoilSpec();
        spec.type = type;
        spec.horizontal = horizontal;
        spec.vertical = vertical;
        spec.kickbackDuration = kickbackDuration;
        spec.recoveryRate = recoveryRate;
        spec.recoveryDuration = recoveryDuration;

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
