package nl.matsgemmeke.battlegrounds.item.shoot;

import com.google.inject.Inject;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.item.WeaponFactoryCreationException;
import nl.matsgemmeke.battlegrounds.item.shoot.burst.BurstModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.fullauto.FullyAutomaticModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.semiauto.SemiAutomaticModeFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Factory class responsible for instantiating {@link FireMode} implementation classes.
 */
public class FireModeFactory {

    @NotNull
    private final BurstModeFactory burstModeFactory;
    @NotNull
    private final FullyAutomaticModeFactory fullyAutomaticModeFactory;
    @NotNull
    private final SemiAutomaticModeFactory semiAutomaticModeFactory;

    @Inject
    public FireModeFactory(@NotNull BurstModeFactory burstModeFactory, @NotNull FullyAutomaticModeFactory fullyAutomaticModeFactory, @NotNull SemiAutomaticModeFactory semiAutomaticModeFactory) {
        this.burstModeFactory = burstModeFactory;
        this.fullyAutomaticModeFactory = fullyAutomaticModeFactory;
        this.semiAutomaticModeFactory = semiAutomaticModeFactory;
    }

    /**
     * Creates a new {@link FireMode} instance based on configuration values.
     *
     * @param item the associated item
     * @param section the configuration section
     * @return a new {@link FireMode} instance
     */
    @NotNull
    public FireMode create(@NotNull Shootable item, @NotNull Section section) {
        String type = section.getString("type");
        FireModeType fireModeType;

        try {
            fireModeType = FireModeType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new WeaponFactoryCreationException("Error while getting fire mode type \"" + type + "\"");
        }

        int rateOfFire = section.getInt("rate-of-fire");

        switch (fireModeType) {
            case BURST_MODE -> {
                int amountOfShots = section.getInt("amount-of-shots");

                return burstModeFactory.create(item, amountOfShots, rateOfFire);
            }
            case FULLY_AUTOMATIC -> {
                return fullyAutomaticModeFactory.create(item, rateOfFire);
            }
            case SEMI_AUTOMATIC -> {
                long delayBetweenShots = section.getLong("delay-between-shots");

                return semiAutomaticModeFactory.create(item, delayBetweenShots);
            }
        }

        throw new WeaponFactoryCreationException("Invalid fire mode type \"" + type + "\"");
    }
}
