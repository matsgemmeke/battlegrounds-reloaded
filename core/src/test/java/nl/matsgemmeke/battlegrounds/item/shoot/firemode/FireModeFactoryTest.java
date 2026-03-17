package nl.matsgemmeke.battlegrounds.item.shoot.firemode;

import nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode.BurstModeSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode.FullyAutomaticModeSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode.SemiAutomaticSpec;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.burst.BurstMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.fullauto.FullyAutomaticMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.semiauto.SemiAutomaticMode;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FireModeFactoryTest {

    @Mock
    private Scheduler scheduler;
    @InjectMocks
    private FireModeFactory fireModeFactory;

    @Test
    @DisplayName("create returns BurstMode instance")
    void create_burstMode() {
        BurstModeSpec spec = new BurstModeSpec();
        spec.type = "BURST_MODE";
        spec.amountOfShots = 3;
        spec.rateOfFire = 600;
        spec.cycleCooldown = 20L;

        Schedule shotSchedule = mock(Schedule.class);
        Schedule cooldownSchedule = mock(Schedule.class);

        when(scheduler.createRepeatingSchedule(0L, 2L)).thenReturn(shotSchedule);
        when(scheduler.createSingleRunSchedule(20L)).thenReturn(cooldownSchedule);

        FireMode fireMode = fireModeFactory.create(spec);

        assertThat(fireMode).isInstanceOf(BurstMode.class);
    }

    static Stream<Arguments> fullyAutomaticRateOfFireExpectations() {
        return Stream.of(
                arguments(1200, 1L, 4L),
                arguments(600, 2L, 4L),
                arguments(400, 4L, 4L),
                arguments(300, 4L, 4L)
        );
    }

    @ParameterizedTest
    @MethodSource("fullyAutomaticRateOfFireExpectations")
    @DisplayName("create returns FullyAutomaticMode instance")
    void create_fullyAutomatic(int rateOfFire, long expectedInterval, long expectedCooldownDuration) {
        Schedule shotSchedule = mock(Schedule.class);
        Schedule cooldownSchedule = mock(Schedule.class);

        FullyAutomaticModeSpec spec = new FullyAutomaticModeSpec();
        spec.type = "FULLY_AUTOMATIC";
        spec.rateOfFire = rateOfFire;

        when(scheduler.createRepeatingSchedule(0L, expectedInterval)).thenReturn(shotSchedule);
        when(scheduler.createSingleRunSchedule(expectedCooldownDuration)).thenReturn(cooldownSchedule);

        FireMode fireMode = fireModeFactory.create(spec);

        assertThat(fireMode).isInstanceOf(FullyAutomaticMode.class);
    }

    static Stream<Arguments> semiAutomaticRateOfFireExpectations() {
        return Stream.of(
                arguments(0, 1200),
                arguments(1, 600),
                arguments(2, 400),
                arguments(3, 300),
                arguments(9, 120)
        );
    }

    @ParameterizedTest
    @MethodSource("semiAutomaticRateOfFireExpectations")
    @DisplayName("create returns SemiAutomaticMode instance")
    void create_semiAutomatic(long cycleCooldown, int expectedRateOfFire) {
        SemiAutomaticSpec spec = new SemiAutomaticSpec();
        spec.type = "SEMI_AUTOMATIC";
        spec.cycleCooldown = cycleCooldown;

        Schedule cooldownSchedule = mock(Schedule.class);
        when(scheduler.createSingleRunSchedule(cycleCooldown)).thenReturn(cooldownSchedule);

        FireModeFactory factory = new FireModeFactory(scheduler);
        FireMode result = factory.create(spec);

        assertThat(result).isInstanceOf(SemiAutomaticMode.class);
        assertThat(result.getRateOfFire()).isEqualTo(expectedRateOfFire);
    }
}
