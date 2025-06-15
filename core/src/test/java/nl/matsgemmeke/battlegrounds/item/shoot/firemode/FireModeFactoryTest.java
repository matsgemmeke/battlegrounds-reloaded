package nl.matsgemmeke.battlegrounds.item.shoot.firemode;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.FireModeSpec;
import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.burst.BurstMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.burst.BurstModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.fullauto.FullyAutomaticMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.fullauto.FullyAutomaticModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.semiauto.SemiAutomaticMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.semiauto.SemiAutomaticModeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FireModeFactoryTest {

    private BurstModeFactory burstModeFactory;
    private FullyAutomaticModeFactory fullyAutomaticModeFactory;
    private SemiAutomaticModeFactory semiAutomaticModeFactory;
    private Shootable item;

    @BeforeEach
    public void setUp() {
        burstModeFactory = mock(BurstModeFactory.class);
        fullyAutomaticModeFactory = mock(FullyAutomaticModeFactory.class);
        semiAutomaticModeFactory = mock(SemiAutomaticModeFactory.class);
        item = mock(Shootable.class);
    }

    @Test
    public void createReturnsBurstModeInstance() {
        int amountOfShots = 3;
        int rateOfFire = 600;

        FireModeSpec spec = new FireModeSpec("BURST_MODE", amountOfShots, rateOfFire, null);

        BurstMode fireMode = mock(BurstMode.class);
        when(burstModeFactory.create(item, amountOfShots, rateOfFire)).thenReturn(fireMode);

        FireModeFactory factory = new FireModeFactory(burstModeFactory, fullyAutomaticModeFactory, semiAutomaticModeFactory);
        FireMode result = factory.create(spec, item);

        assertThat(result).isEqualTo(fireMode);
    }

    @Test
    public void createReturnsFullyAutomaticModeInstance() {
        int rateOfFire = 600;

        FireModeSpec spec = new FireModeSpec("FULLY_AUTOMATIC", null, rateOfFire, null);

        FullyAutomaticMode fireMode = mock(FullyAutomaticMode.class);
        when(fullyAutomaticModeFactory.create(item, rateOfFire)).thenReturn(fireMode);

        FireModeFactory factory = new FireModeFactory(burstModeFactory, fullyAutomaticModeFactory, semiAutomaticModeFactory);
        FireMode result = factory.create(spec, item);

        assertThat(result).isEqualTo(fireMode);
    }

    @Test
    public void createReturnsSemiAutomaticModeInstance() {
        long delayBetweenShots = 4L;

        FireModeSpec spec = new FireModeSpec("SEMI_AUTOMATIC", null, null, delayBetweenShots);

        SemiAutomaticMode fireMode = mock(SemiAutomaticMode.class);
        when(semiAutomaticModeFactory.create(item, delayBetweenShots)).thenReturn(fireMode);

        FireModeFactory factory = new FireModeFactory(burstModeFactory, fullyAutomaticModeFactory, semiAutomaticModeFactory);
        FireMode result = factory.create(spec, item);

        assertThat(result).isEqualTo(fireMode);
    }
}
