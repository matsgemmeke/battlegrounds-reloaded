package nl.matsgemmeke.battlegrounds.item.shoot.firemode;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.FireModeSpec;
import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.burst.BurstMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.burst.BurstModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.fullauto.FullyAutomaticMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.fullauto.FullyAutomaticModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.semiauto.SemiAutomaticMode;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FireModeFactoryTest {

    private BurstModeFactory burstModeFactory;
    private FullyAutomaticModeFactory fullyAutomaticModeFactory;
    private Scheduler scheduler;
    private Shootable item;

    @BeforeEach
    public void setUp() {
        burstModeFactory = mock(BurstModeFactory.class);
        fullyAutomaticModeFactory = mock(FullyAutomaticModeFactory.class);
        scheduler = mock(Scheduler.class);
        item = mock(Shootable.class);
    }

    @Test
    public void createReturnsBurstModeInstance() {
        int amountOfShots = 3;
        int rateOfFire = 600;

        FireModeSpec spec = new FireModeSpec("BURST_MODE", amountOfShots, rateOfFire, null);

        BurstMode fireMode = mock(BurstMode.class);
        when(burstModeFactory.create(item, amountOfShots, rateOfFire)).thenReturn(fireMode);

        FireModeFactory factory = new FireModeFactory(burstModeFactory, fullyAutomaticModeFactory, scheduler);
        FireMode result = factory.create(spec, item);

        assertThat(result).isEqualTo(fireMode);
    }

    @Test
    public void createReturnsFullyAutomaticModeInstance() {
        int rateOfFire = 600;

        FireModeSpec spec = new FireModeSpec("FULLY_AUTOMATIC", null, rateOfFire, null);

        FullyAutomaticMode fireMode = mock(FullyAutomaticMode.class);
        when(fullyAutomaticModeFactory.create(item, rateOfFire)).thenReturn(fireMode);

        FireModeFactory factory = new FireModeFactory(burstModeFactory, fullyAutomaticModeFactory, scheduler);
        FireMode result = factory.create(spec, item);

        assertThat(result).isEqualTo(fireMode);
    }

    @NotNull
    private static Stream<Arguments> rateOfFireExpectations() {
        return Stream.of(
                arguments(0, 1200),
                arguments(1, 600),
                arguments(2, 400),
                arguments(3, 300),
                arguments(9, 120)
        );
    }

    @ParameterizedTest
    @MethodSource("rateOfFireExpectations")
    public void createReturnsSemiAutomaticModeInstanceWithCalculatedRateOfFire(long delayBetweenShots, int expectedRateOfFire) {
        FireModeSpec spec = new FireModeSpec("SEMI_AUTOMATIC", null, null, delayBetweenShots);

        Schedule cooldownSchedule = mock(Schedule.class);
        when(scheduler.createSingleRunSchedule(delayBetweenShots)).thenReturn(cooldownSchedule);

        FireModeFactory factory = new FireModeFactory(burstModeFactory, fullyAutomaticModeFactory, scheduler);
        FireMode result = factory.create(spec, item);

        assertThat(result).isInstanceOf(SemiAutomaticMode.class);
        assertThat(result.getRateOfFire()).isEqualTo(expectedRateOfFire);
    }
}
