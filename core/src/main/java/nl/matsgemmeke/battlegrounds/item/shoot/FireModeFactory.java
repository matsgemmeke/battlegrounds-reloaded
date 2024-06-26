package nl.matsgemmeke.battlegrounds.item.shoot;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.WeaponFactoryCreationException;
import org.jetbrains.annotations.NotNull;

/**
 * Factory class responsible for instantiating {@link FireMode} implementation classes.
 */
public class FireModeFactory {

    @NotNull
    private TaskRunner taskRunner;

    public FireModeFactory(@NotNull TaskRunner taskRunner) {
        this.taskRunner = taskRunner;
    }

    /**
     * Creates a new {@link FireMode} instance based on configuration values.
     *
     * @param item the associated item
     * @param section the configuration section
     * @return a new {@link FireMode} instance
     */
    @NotNull
    public FireMode make(@NotNull Shootable item, @NotNull Section section) {
        String type = section.getString("type");
        FireModeType fireModeType;

        try {
            fireModeType = FireModeType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new WeaponFactoryCreationException("Error while getting fire mode type \"" + type + "\"");
        }

        int rateOfFire = section.getInt("rate-of-fire");

        switch (fireModeType) {
            case BURST_MODE:
                int shotAmount = section.getInt("shots");
                return new BurstMode(item, taskRunner, shotAmount, rateOfFire);
            case FULLY_AUTOMATIC:
                return new FullyAutomaticMode(item, taskRunner, rateOfFire);
            case SEMI_AUTOMATIC:
                long delayBetweenShots = section.getLong("delay-between-shots");
                return new SemiAutomaticMode(item, taskRunner, delayBetweenShots);
        }

        throw new WeaponFactoryCreationException("Invalid fire mode type \"" + type + "\"");
    }
}
