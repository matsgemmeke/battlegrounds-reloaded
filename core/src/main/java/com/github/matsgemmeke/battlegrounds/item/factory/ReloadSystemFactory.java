package com.github.matsgemmeke.battlegrounds.item.factory;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.game.GameSound;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import com.github.matsgemmeke.battlegrounds.game.DefaultGameSound;
import com.github.matsgemmeke.battlegrounds.item.mechanics.*;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReloadSystemFactory {

    @NotNull
    private TaskRunner taskRunner;

    public ReloadSystemFactory(@NotNull TaskRunner taskRunner) {
        this.taskRunner = taskRunner;
    }

    /**
     * Creates a new {@link ReloadSystem} instance based on configuration values.
     *
     * @param gun the associated gun
     * @param section the configuration section
     * @return a new {@link ReloadSystem} instance
     */
    @NotNull
    public ReloadSystem make(@NotNull Gun gun, @NotNull Section section) {
        String type = section.getString("type");
        ReloadSystemType reloadSystemType;

        try {
            reloadSystemType = ReloadSystemType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new WeaponFactoryCreationException("Error while getting reload system type \"" + type + "\"");
        }

        List<GameSound> reloadSounds = DefaultGameSound.parseSounds(section.getString("sound"));

        int duration = section.getInt("duration");

        switch (reloadSystemType) {
            case MAGAZINE_RELOAD:
                return new MagazineReload(taskRunner, gun, reloadSounds, duration);
            case MANUAL_RELOAD:
                return new ManualReload(taskRunner, gun, reloadSounds, duration);
        }

        throw new WeaponFactoryCreationException("Invalid reload system type \"" + type + "\"");
    }
}
