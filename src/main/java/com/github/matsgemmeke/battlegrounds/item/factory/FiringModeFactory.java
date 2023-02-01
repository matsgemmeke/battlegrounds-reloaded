package com.github.matsgemmeke.battlegrounds.item.factory;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import com.github.matsgemmeke.battlegrounds.item.mechanism.*;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.jetbrains.annotations.NotNull;

/**
 * Factory class responsible for instantiating {@link FiringMode} implementation classes.
 */
public class FiringModeFactory {

    @NotNull
    private TaskRunner taskRunner;

    public FiringModeFactory(@NotNull TaskRunner taskRunner) {
        this.taskRunner = taskRunner;
    }

    /**
     * Creates a new {@link FiringMode} instance based on configuration values.
     *
     * @param firearm the associated firearm
     * @param section the configuration section
     * @return a new {@link FiringMode} instance
     */
    @NotNull
    public FiringMode make(@NotNull Firearm firearm, @NotNull Section section) {
        String type = section.getString("type");
        FiringModeType firingModeType;

        try {
            firingModeType = FiringModeType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new WeaponFactoryCreationException("Error while getting firing mode type \"" + type + "\"");
        }

        int rateOfFire = section.getInt("rate-of-fire");

        switch (firingModeType) {
            case BURST_MODE:
                int shotAmount = section.getInt("shots");
                return new BurstMode(taskRunner, firearm, shotAmount, rateOfFire);
            case FULLY_AUTOMATIC:
                return new FullyAutomaticMode(taskRunner, firearm, rateOfFire);
            case SEMI_AUTOMATIC:
                long cooldown = section.getLong("cooldown");
                return new SemiAutomaticMode(taskRunner, firearm, cooldown);
        }

        throw new WeaponFactoryCreationException("Invalid firing mode type \"" + type + "\"");
    }
}
