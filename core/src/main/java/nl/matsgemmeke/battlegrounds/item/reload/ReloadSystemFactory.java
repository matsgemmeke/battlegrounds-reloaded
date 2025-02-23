package nl.matsgemmeke.battlegrounds.item.reload;

import com.google.inject.Inject;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.WeaponFactoryCreationException;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.manual.ManualInsertionReloadSystemFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReloadSystemFactory {

    @NotNull
    private final MagazineReloadSystemFactory magazineReloadSystemFactory;
    @NotNull
    private final ManualInsertionReloadSystemFactory manualInsertionReloadSystemFactory;

    @Inject
    public ReloadSystemFactory(@NotNull MagazineReloadSystemFactory magazineReloadSystemFactory, @NotNull ManualInsertionReloadSystemFactory manualInsertionReloadSystemFactory) {
        this.magazineReloadSystemFactory = magazineReloadSystemFactory;
        this.manualInsertionReloadSystemFactory = manualInsertionReloadSystemFactory;
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

        ReloadProperties properties = new ReloadProperties(reloadSounds, duration);

        switch (reloadSystemType) {
            case MAGAZINE -> {
                return magazineReloadSystemFactory.create(properties, gun, audioEmitter);
            }
            case MANUAL_INSERTION -> {
                return manualInsertionReloadSystemFactory.create(properties, gun, audioEmitter);
            }
        }

        throw new WeaponFactoryCreationException("Invalid reload system type \"" + type + "\"");
    }
}
