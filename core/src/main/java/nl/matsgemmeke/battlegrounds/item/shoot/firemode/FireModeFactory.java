package nl.matsgemmeke.battlegrounds.item.shoot.firemode;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.FireModeSpec;
import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.burst.BurstModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.fullauto.FullyAutomaticModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.semiauto.SemiAutomaticMode;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Factory class responsible for instantiating {@link FireMode} implementation classes.
 */
public class FireModeFactory {

    private static final int TICKS_PER_MINUTE = 1200;

    @NotNull
    private final BurstModeFactory burstModeFactory;
    @NotNull
    private final FullyAutomaticModeFactory fullyAutomaticModeFactory;
    @NotNull
    private final Scheduler scheduler;

    @Inject
    public FireModeFactory(@NotNull BurstModeFactory burstModeFactory, @NotNull FullyAutomaticModeFactory fullyAutomaticModeFactory, @NotNull Scheduler scheduler) {
        this.burstModeFactory = burstModeFactory;
        this.fullyAutomaticModeFactory = fullyAutomaticModeFactory;
        this.scheduler = scheduler;
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
                long delayBetweenShots = this.validateSpecVar(spec.delayBetweenShots(), "delayBetweenShots", fireModeType);
                int rateOfFire = Math.floorDiv(TICKS_PER_MINUTE, (int) delayBetweenShots + 1);

                Schedule cooldownSchedule = scheduler.createSingleRunSchedule(delayBetweenShots);

                return new SemiAutomaticMode(cooldownSchedule, rateOfFire);
            }
        }

        throw new FireModeCreationException("Invalid fire mode type \"" + fireModeType + "\"");
    }

    private <T> T validateSpecVar(@Nullable T value, @NotNull String valueName, @NotNull Object fireModeType) {
        if (value == null) {
            throw new FireModeCreationException("Cannot create %s because of invalid spec: Required '%s' value is missing".formatted(fireModeType, valueName));
        }

        return value;
    }
}
