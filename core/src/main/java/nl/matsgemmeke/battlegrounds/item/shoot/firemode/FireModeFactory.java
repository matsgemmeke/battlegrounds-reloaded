package nl.matsgemmeke.battlegrounds.item.shoot.firemode;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode.BurstModeSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode.FireModeSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode.FullyAutomaticModeSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.firemode.SemiAutomaticSpec;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.burst.BurstMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.fullauto.FullyAutomaticMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.semiauto.SemiAutomaticMode;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;

/**
 * Factory class responsible for instantiating {@link FireMode} implementation classes.
 */
public class FireModeFactory {

    // The amount of interaction events per second received when holding down the right mouse button
    private static final int INTERACTIONS_PER_SECOND = 5;
    // The amount of ticks between each interaction when holding down the right mouse button
    private static final int TICKS_BETWEEN_INTERACTIONS = 4;
    private static final int TICKS_PER_MINUTE = 1200;
    private static final int TICKS_PER_SECOND = 20;
    private static final long SHOT_SCHEDULE_DELAY = 0L;

    private final Scheduler scheduler;

    @Inject
    public FireModeFactory(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Creates a new {@link FireMode} instance based on the given specification.
     *
     * @param fireModeSpec the fire mode specification
     * @return             a new {@link FireMode} instance
     */
    public FireMode create(FireModeSpec fireModeSpec) {
        FireModeType fireModeType = FireModeType.valueOf(fireModeSpec.type);

        switch (fireModeType) {
            case BURST_MODE -> {
                BurstModeSpec spec = (BurstModeSpec) fireModeSpec;
                int amountOfShots = spec.amountOfShots;
                int rateOfFire = spec.rateOfFire;
                long cycleCooldown = spec.cycleCooldown;

                // Convert rate of fire to amount of rounds fired per second
                int shotsPerSecond = rateOfFire / 60;
                // Amount of ticks between each shot
                long interval = TICKS_PER_SECOND / shotsPerSecond;

                Schedule shotSchedule = scheduler.createRepeatingSchedule(SHOT_SCHEDULE_DELAY, interval);
                Schedule cooldownSchedule = scheduler.createSingleRunSchedule(cycleCooldown);

                return new BurstMode(shotSchedule, cooldownSchedule, amountOfShots, rateOfFire);
            }
            case FULLY_AUTOMATIC -> {
                FullyAutomaticModeSpec spec = (FullyAutomaticModeSpec) fireModeSpec;
                int rateOfFire = spec.rateOfFire;

                // Convert rate of fire to amount of shots fired per second
                int shotsPerSecond = rateOfFire / 60;
                // Amount of shots to be fired for one shooting cycle
                int amountOfShots = shotsPerSecond / INTERACTIONS_PER_SECOND;
                // Amount of ticks between each shot
                long interval = TICKS_BETWEEN_INTERACTIONS / amountOfShots;
                // The cooldown duration for the whole shooting cycle
                long cooldown = amountOfShots * interval;

                Schedule shotSchedule = scheduler.createRepeatingSchedule(SHOT_SCHEDULE_DELAY, interval);
                Schedule cooldownSchedule = scheduler.createSingleRunSchedule(cooldown);

                return new FullyAutomaticMode(shotSchedule, cooldownSchedule, rateOfFire);
            }
            case SEMI_AUTOMATIC -> {
                SemiAutomaticSpec spec = (SemiAutomaticSpec) fireModeSpec;
                long cycleCooldown = spec.cycleCooldown;

                int rateOfFire = Math.floorDiv(TICKS_PER_MINUTE, (int) cycleCooldown + 1);

                Schedule cooldownSchedule = scheduler.createSingleRunSchedule(cycleCooldown);

                return new SemiAutomaticMode(cooldownSchedule, rateOfFire);
            }
        }

        throw new FireModeCreationException("Invalid fire mode type \"" + fireModeType + "\"");
    }
}
