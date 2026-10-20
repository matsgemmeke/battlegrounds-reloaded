package nl.matsgemmeke.battlegrounds.item.reload;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ReloadingSpec;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.manual.ManualInsertionReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import nl.matsgemmeke.battlegrounds.sound.SoundService;

import java.util.Optional;

public class ReloadSystemFactory {

    private final MagazineReloadSystemFactory magazineReloadSystemFactory;
    private final ManualInsertionReloadSystemFactory manualInsertionReloadSystemFactory;
    private final SoundService soundService;

    @Inject
    public ReloadSystemFactory(
            MagazineReloadSystemFactory magazineReloadSystemFactory,
            ManualInsertionReloadSystemFactory manualInsertionReloadSystemFactory,
            SoundService soundService
    ) {
        this.magazineReloadSystemFactory = magazineReloadSystemFactory;
        this.manualInsertionReloadSystemFactory = manualInsertionReloadSystemFactory;
        this.soundService = soundService;
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

        SoundSequence soundSequence = Optional.ofNullable(spec.soundSequence)
                .map(soundSequenceId -> soundService.getSoundSequence(soundSequenceId)
                        .orElseThrow(() -> new ReloadSystemCreationException("Invalid sound sequence id '%s'".formatted(soundSequenceId))))
                .orElse(null);

        long duration = spec.duration;

        ReloadProperties properties = new ReloadProperties(soundSequence, duration);

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
