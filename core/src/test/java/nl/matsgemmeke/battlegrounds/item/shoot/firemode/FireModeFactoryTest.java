package nl.matsgemmeke.battlegrounds.item.shoot.firemode;

import nl.matsgemmeke.battlegrounds.configuration.item.shoot.FireModeSpec;
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
        int amountOfShots = 3;
        int rateOfFire = 600;
        long cycleCooldown = 20L;
        FireModeSpec spec = new FireModeSpec("BURST_MODE", amountOfShots, rateOfFire, cycleCooldown);

        Schedule shotSchedule = mock(Schedule.class);
        Schedule cooldownSchedule = mock(Schedule.class);

        when(scheduler.createRepeatingSchedule(0L, 2L)).thenReturn(shotSchedule);
        when(scheduler.createSingleRunSchedule(cycleCooldown)).thenReturn(cooldownSchedule);

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
        FireModeSpec spec = new FireModeSpec("BURST_MODE", amountOfShots, rateOfFire, cycleCooldown);

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
        FireModeSpec spec = new FireModeSpec("FULLY_AUTOMATIC", null, rateOfFire, null);
        Schedule shotSchedule = mock(Schedule.class);
        Schedule cooldownSchedule = mock(Schedule.class);

        when(scheduler.createRepeatingSchedule(0L, expectedInterval)).thenReturn(shotSchedule);
        when(scheduler.createSingleRunSchedule(expectedCooldownDuration)).thenReturn(cooldownSchedule);

        FireModeFactory factory = new FireModeFactory(scheduler);
        FireMode fireMode = factory.create(spec);

        assertThat(fireMode).isInstanceOf(FullyAutomaticMode.class);
    }

    @Test
    public void createThrowsFireModeCreationExceptionWhenTypeEqualsSemiAutomaticAndDelayBetweenShotsValueInSpecIsNull() {
        FireModeSpec spec = new FireModeSpec("SEMI_AUTOMATIC", null, null, null);

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
    public void createReturnsSemiAutomaticModeInstanceWithCalculatedRateOfFire(long delayBetweenShots, int expectedRateOfFire) {
        FireModeSpec spec = new FireModeSpec("SEMI_AUTOMATIC", null, null, delayBetweenShots);

        Schedule cooldownSchedule = mock(Schedule.class);
        when(scheduler.createSingleRunSchedule(delayBetweenShots)).thenReturn(cooldownSchedule);

        FireModeFactory factory = new FireModeFactory(scheduler);
        FireMode result = factory.create(spec);

        assertThat(result).isInstanceOf(SemiAutomaticMode.class);
        assertThat(result.getRateOfFire()).isEqualTo(expectedRateOfFire);
    }
}
