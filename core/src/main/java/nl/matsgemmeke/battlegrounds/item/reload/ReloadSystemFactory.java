package nl.matsgemmeke.battlegrounds.item.reload;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ReloadingSpec;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
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
     * @param spec the reload specification
     * @param item the associated item
     * @param audioEmitter the sound emitter for producing reload sounds effects
     * @return a new {@link ReloadSystem} instance
     */
    @NotNull
    public ReloadSystem create(@NotNull ReloadingSpec spec, @NotNull Reloadable item, @NotNull AudioEmitter audioEmitter) {
        ReloadSystemType reloadSystemType = ReloadSystemType.valueOf(spec.type);
        List<GameSound> reloadSounds = DefaultGameSound.parseSounds(spec.reloadSounds);
        long duration = spec.duration;

        ReloadProperties properties = new ReloadProperties(reloadSounds, duration);
        AmmunitionStorage ammunitionStorage = item.getAmmunitionStorage();

        switch (reloadSystemType) {
            case MAGAZINE -> {
                return magazineReloadSystemFactory.create(properties, ammunitionStorage, audioEmitter);
            }
            case MANUAL_INSERTION -> {
                return manualInsertionReloadSystemFactory.create(properties, ammunitionStorage, audioEmitter);
            }
        }

        throw new IllegalArgumentException("Attempted to create a reload system with unexpected type \"" + reloadSystemType + "\"");
    }
}
