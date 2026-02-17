package nl.matsgemmeke.battlegrounds.item.reload;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ReloadingSpec;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.manual.ManualInsertionReloadSystemFactory;

import java.util.List;

public class ReloadSystemFactory {

    private final MagazineReloadSystemFactory magazineReloadSystemFactory;
    private final ManualInsertionReloadSystemFactory manualInsertionReloadSystemFactory;

    @Inject
    public ReloadSystemFactory(MagazineReloadSystemFactory magazineReloadSystemFactory, ManualInsertionReloadSystemFactory manualInsertionReloadSystemFactory) {
        this.magazineReloadSystemFactory = magazineReloadSystemFactory;
        this.manualInsertionReloadSystemFactory = manualInsertionReloadSystemFactory;
    }

    /**
     * Creates a new {@link ReloadSystem} instance based on configuration values.
     *
     * @param spec              the reloading specification
     * @param resourceContainer the resource container the reload system will utilize
     * @return                  a new {@link ReloadSystem} instance
     */
    public ReloadSystem create(ReloadingSpec spec, ResourceContainer resourceContainer) {
        ReloadSystemType reloadSystemType = ReloadSystemType.valueOf(spec.type);
        List<GameSound> reloadSounds = DefaultGameSound.parseSounds(spec.reloadSounds);
        long duration = spec.duration;

        ReloadProperties properties = new ReloadProperties(reloadSounds, duration);

        switch (reloadSystemType) {
            case MAGAZINE -> {
                return magazineReloadSystemFactory.create(properties, resourceContainer);
            }
            case MANUAL_INSERTION -> {
                return manualInsertionReloadSystemFactory.create(properties, resourceContainer);
            }
        }

        throw new IllegalArgumentException("Attempted to create a reload system with unexpected type \"" + reloadSystemType + "\"");
    }
}
