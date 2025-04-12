package nl.matsgemmeke.battlegrounds.item.shoot;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.FireModeSpecification;
import nl.matsgemmeke.battlegrounds.item.shoot.burst.BurstModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.fullauto.FullyAutomaticModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.semiauto.SemiAutomaticModeFactory;
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
     * @param specification the fire mode specification from the configuration
     * @param item the associated item
     * @return a new {@link FireMode} instance
     */
    @NotNull
    public FireMode create(@NotNull FireModeSpecification specification, @NotNull Shootable item) {
        FireModeType fireModeType = FireModeType.valueOf(specification.type());

        switch (fireModeType) {
            case BURST_MODE -> {
                return burstModeFactory.create(item, specification.amountOfShots(), specification.rateOfFire());
            }
            case FULLY_AUTOMATIC -> {
                return fullyAutomaticModeFactory.create(item, specification.rateOfFire());
            }
            case SEMI_AUTOMATIC -> {
                return semiAutomaticModeFactory.create(item, specification.delayBetweenShots());
            }
        }

        throw new FireModeCreationException("Invalid fire mode type \"" + fireModeType + "\"");
    }
}
