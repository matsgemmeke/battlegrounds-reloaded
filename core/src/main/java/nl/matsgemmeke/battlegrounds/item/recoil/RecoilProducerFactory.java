package nl.matsgemmeke.battlegrounds.item.recoil;

import com.google.inject.Inject;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.item.WeaponFactoryCreationException;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;

/**
 * Factory class responsible for instantiating {@link RecoilProducer} implementation classes.
 */
public class RecoilProducerFactory {

    @NotNull
    private BattlegroundsConfiguration config;

    @Inject
    public RecoilProducerFactory(@NotNull BattlegroundsConfiguration config) {
        this.config = config;
    }

    /**
     * Creates a new {@link RecoilProducer} instance based on configuration values.
     *
     * @param section the configuration section
     * @return a new producer instance
     */
    public RecoilProducer create(@NotNull Section section) {
        String type = section.getString("type");
        RecoilProducerType recoilProducerType;

        try {
            recoilProducerType = RecoilProducerType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new WeaponFactoryCreationException("Error while getting recoil producer type \"" + type + "\"");
        }

        Float[] horizontalRecoilValues = section.getFloatList("horizontal").toArray(Float[]::new);
        Float[] verticalRecoilValues = section.getFloatList("vertical").toArray(Float[]::new);

        switch (recoilProducerType) {
            case CAMERA_MOVEMENT -> {
                long kickbackDuration = section.getLong("kickback-duration");
                float recoveryRate = section.getFloat("recovery-rate");
                long recoveryDuration = section.getLong("recovery-duration");
                long rotationDuration = config.getCameraMovementRecoilDurationInMilliseconds();

                Timer timer = new Timer();

                CameraMovementRecoil recoilProducer = new CameraMovementRecoil(timer);
                recoilProducer.setHorizontalRecoilValues(horizontalRecoilValues);
                recoilProducer.setVerticalRecoilValues(verticalRecoilValues);
                recoilProducer.setKickbackDuration(kickbackDuration);
                recoilProducer.setRecoveryDuration(recoveryDuration);
                recoilProducer.setRecoveryRate(recoveryRate);
                recoilProducer.setRotationDuration(rotationDuration);

                return recoilProducer;
            }
            case RANDOM_SPREAD -> {
                RandomSpreadRecoil recoilProducer = new RandomSpreadRecoil();
                recoilProducer.setHorizontalRecoilValues(horizontalRecoilValues);
                recoilProducer.setVerticalRecoilValues(verticalRecoilValues);

                return recoilProducer;
            }
        }

        throw new WeaponFactoryCreationException("Invalid recoil producer type \"" + type + "\"");
    }
}
