package nl.matsgemmeke.battlegrounds.item.shoot.firemode;

import nl.matsgemmeke.battlegrounds.configuration.item.gun.FireModeSpec;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.burst.BurstMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.fullauto.FullyAutomaticMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.semiauto.SemiAutomaticMode;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FireModeFactoryTest {

    private Scheduler scheduler;

    @BeforeEach
    public void setUp() {
        scheduler = mock(Scheduler.class);
    }

    @Test
    public void createReturnsBurstModeInstance() {
        FireModeSpec spec = new FireModeSpec();
        spec.type = "BURST_MODE";
        spec.amountOfShots = 3;
        spec.rateOfFire = 600;
        spec.cycleCooldown = 20L;

        Schedule shotSchedule = mock(Schedule.class);
        Schedule cooldownSchedule = mock(Schedule.class);

        when(scheduler.createRepeatingSchedule(0L, 2L)).thenReturn(shotSchedule);
        when(scheduler.createSingleRunSchedule(20L)).thenReturn(cooldownSchedule);

        FireModeFactory factory = new FireModeFactory(scheduler);
        FireMode fireMode = factory.create(spec);

        assertThat(fireMode).isInstanceOf(BurstMode.class);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null,null,null,amountOfShots",
            "3,null,null,rateOfFire",
            "3,600,null,cycleCooldown"
    }, nullValues = "null")
    public void createThrowsFireModeCreationExceptionWhenTypeEqualsBurstModeAndRequiredVarsInSpecAreNull(Integer amountOfShots, Integer rateOfFire, Long cycleCooldown, String missingVar) {
        FireModeSpec spec = new FireModeSpec();
        spec.type = "BURST_MODE";
        spec.amountOfShots = amountOfShots;
        spec.rateOfFire = rateOfFire;
        spec.cycleCooldown = cycleCooldown;

        FireModeFactory factory = new FireModeFactory(scheduler);

        assertThatThrownBy(() -> factory.create(spec))
                .isInstanceOf(FireModeCreationException.class)
                .hasMessage("Cannot create BURST_MODE because of invalid spec: Required '" + missingVar + "' value is missing");
    }

    @NotNull
    private static Stream<Arguments> fullyAutomaticRateOfFireExpectations() {
        return Stream.of(
                arguments(1200, 1L, 4L),
                arguments(600, 2L, 4L),
                arguments(400, 4L, 4L),
                arguments(300, 4L, 4L)
        );
    }

    @ParameterizedTest
    @MethodSource("fullyAutomaticRateOfFireExpectations")
    public void createReturnsFullyAutomaticModeInstance(int rateOfFire, long expectedInterval, long expectedCooldownDuration) {
        Schedule shotSchedule = mock(Schedule.class);
        Schedule cooldownSchedule = mock(Schedule.class);

        FireModeSpec spec = new FireModeSpec();
        spec.type = "FULLY_AUTOMATIC";
        spec.rateOfFire = rateOfFire;

        when(scheduler.createRepeatingSchedule(0L, expectedInterval)).thenReturn(shotSchedule);
        when(scheduler.createSingleRunSchedule(expectedCooldownDuration)).thenReturn(cooldownSchedule);

        FireModeFactory factory = new FireModeFactory(scheduler);
        FireMode fireMode = factory.create(spec);

        assertThat(fireMode).isInstanceOf(FullyAutomaticMode.class);
    }

    @Test
    public void createThrowsFireModeCreationExceptionWhenTypeEqualsSemiAutomaticAndDelayBetweenShotsValueInSpecIsNull() {
        FireModeSpec spec = new FireModeSpec();
        spec.type = "SEMI_AUTOMATIC";

        FireModeFactory factory = new FireModeFactory(scheduler);

        assertThatThrownBy(() -> factory.create(spec))
                .isInstanceOf(FireModeCreationException.class)
                .hasMessage("Cannot create SEMI_AUTOMATIC because of invalid spec: Required 'cycleCooldown' value is missing");
    }

    @NotNull
    private static Stream<Arguments> semiAutomaticRateOfFireExpectations() {
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
    public void createReturnsSemiAutomaticModeInstanceWithCalculatedRateOfFire(long cycleCooldown, int expectedRateOfFire) {
        FireModeSpec spec = new FireModeSpec();
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
