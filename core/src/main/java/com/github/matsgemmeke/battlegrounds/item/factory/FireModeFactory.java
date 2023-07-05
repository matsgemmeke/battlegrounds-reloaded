package com.github.matsgemmeke.battlegrounds.item.factory;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import com.github.matsgemmeke.battlegrounds.item.mechanics.*;
import dev.dejvokep.boostedyaml.block.implementation.Section;
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
     * @param gun the associated gun
     * @param section the configuration section
     * @return a new {@link FireMode} instance
     */
    @NotNull
    public FireMode make(@NotNull Gun gun, @NotNull Section section) {
        String type = section.getString("type");
        FireModeType fireModeType;

        try {
            fireModeType = FireModeType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new WeaponFactoryCreationException("Error while getting firing mode type \"" + type + "\"");
        }

        int rateOfFire = section.getInt("rate-of-fire");

        switch (fireModeType) {
            case BURST_MODE:
                int shotAmount = section.getInt("shots");
                return new BurstMode(taskRunner, gun, shotAmount, rateOfFire);
            case FULLY_AUTOMATIC:
                return new FullyAutomaticMode(taskRunner, gun, rateOfFire);
            case SEMI_AUTOMATIC:
                long cooldown = section.getLong("cooldown");
                return new SemiAutomaticMode(taskRunner, gun, cooldown);
        }

        throw new WeaponFactoryCreationException("Invalid firing mode type \"" + type + "\"");
    }
}
