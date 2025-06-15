package nl.matsgemmeke.battlegrounds.item.shoot.firemode;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.FireModeSpec;
import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.burst.BurstModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.fullauto.FullyAutomaticModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.semiauto.SemiAutomaticModeFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Factory class responsible for instantiating {@link FireMode} implementation classes.
 */
public class FireModeFactory {

    @NotNull
    private final BurstModeFactory burstModeFactory;
    @NotNull
    private final FullyAutomaticModeFactory fullyAutomaticModeFactory;
    @NotNull
    private final SemiAutomaticModeFactory semiAutomaticModeFactory;

    @Inject
    public FireModeFactory(@NotNull BurstModeFactory burstModeFactory, @NotNull FullyAutomaticModeFactory fullyAutomaticModeFactory, @NotNull SemiAutomaticModeFactory semiAutomaticModeFactory) {
        this.burstModeFactory = burstModeFactory;
        this.fullyAutomaticModeFactory = fullyAutomaticModeFactory;
        this.semiAutomaticModeFactory = semiAutomaticModeFactory;
    }

    /**
     * Creates a new {@link FireMode} instance based on the given specification.
     *
     * @param spec the fire mode specification
     * @param item the associated item
     * @return a new {@link FireMode} instance
     */
    @NotNull
    public FireMode create(@NotNull FireModeSpec spec, @NotNull Shootable item) {
        FireModeType fireModeType = FireModeType.valueOf(spec.type());

        switch (fireModeType) {
            case BURST_MODE -> {
                return burstModeFactory.create(item, spec.amountOfShots(), spec.rateOfFire());
            }
            case FULLY_AUTOMATIC -> {
                return fullyAutomaticModeFactory.create(item, spec.rateOfFire());
            }
            case SEMI_AUTOMATIC -> {
                return semiAutomaticModeFactory.create(item, spec.delayBetweenShots());
            }
        }

        throw new FireModeCreationException("Invalid fire mode type \"" + fireModeType + "\"");
    }
}
