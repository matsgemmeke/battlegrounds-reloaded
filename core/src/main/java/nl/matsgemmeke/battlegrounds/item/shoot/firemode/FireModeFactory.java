package nl.matsgemmeke.battlegrounds.item.shoot.firemode;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.FireModeSpec;
import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.burst.BurstModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.fullauto.FullyAutomaticMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.semiauto.SemiAutomaticMode;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Factory class responsible for instantiating {@link FireMode} implementation classes.
 */
public class FireModeFactory {

    // The amount of interaction events per second received when holding down the right mouse button
    private static final int INTERACTIONS_PER_SECOND = 5;
    // The amount of ticks between each interaction when holding down the right mouse button
    private static final int TICKS_BETWEEN_INTERACTIONS = 4;
    private static final int TICKS_PER_MINUTE = 1200;
    private static final long FULLY_AUTOMATIC_MODE_SCHEDULE_DELAY = 0L;

    @NotNull
    private final BurstModeFactory burstModeFactory;
    @NotNull
    private final Scheduler scheduler;

    @Inject
    public FireModeFactory(@NotNull BurstModeFactory burstModeFactory, @NotNull Scheduler scheduler) {
        this.burstModeFactory = burstModeFactory;
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
                int rateOfFire = this.validateSpecVar(spec.rateOfFire(), "rateOfFire", fireModeType);
                // Convert rate of fire to amount of rounds fired per second
                int roundsPerSecond = rateOfFire / 60;
                // Amount of rounds to be fired for one shooting cycle
                int amountOfRounds = roundsPerSecond / INTERACTIONS_PER_SECOND;
                // Amount of ticks between each shot
                long interval = TICKS_BETWEEN_INTERACTIONS / amountOfRounds;
                // The cooldown duration to for the whole shooting cycle
                long cooldown = amountOfRounds * interval;

                Schedule shotSchedule = scheduler.createRepeatingSchedule(FULLY_AUTOMATIC_MODE_SCHEDULE_DELAY, interval);
                Schedule cooldownSchedule = scheduler.createSingleRunSchedule(cooldown);

                return new FullyAutomaticMode(shotSchedule, cooldownSchedule, rateOfFire);
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
