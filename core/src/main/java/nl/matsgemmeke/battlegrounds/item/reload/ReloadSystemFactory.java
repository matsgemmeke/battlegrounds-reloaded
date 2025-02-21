package nl.matsgemmeke.battlegrounds.item.reload;

import com.google.inject.Inject;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.WeaponFactoryCreationException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReloadSystemFactory {

    @NotNull
    private TaskRunner taskRunner;

    @Inject
    public ReloadSystemFactory(@NotNull TaskRunner taskRunner) {
        this.taskRunner = taskRunner;
    }

    /**
     * Creates a new {@link ReloadSystem} instance based on configuration values.
     *
     * @param gun the associated gun
     * @param section the configuration section
     * @param audioEmitter the sound emitter for producing reload sounds effects
     * @return a new {@link ReloadSystem} instance
     */
    @NotNull
    public ReloadSystem create(@NotNull Gun gun, @NotNull Section section, @NotNull AudioEmitter audioEmitter) {
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
            case MAGAZINE -> {
                MagazineReloadSystem reloadSystem = new MagazineReloadSystem(gun, audioEmitter, taskRunner, duration);
                reloadSystem.setReloadSounds(reloadSounds);

                return reloadSystem;
            }
            case MANUAL_INSERTION -> {
                ManualInsertionReloadSystem reloadSystem = new ManualInsertionReloadSystem(gun, audioEmitter, taskRunner, duration);
                reloadSystem.setReloadSounds(reloadSounds);

                return reloadSystem;
            }
        }

        throw new WeaponFactoryCreationException("Invalid reload system type \"" + type + "\"");
    }
}
