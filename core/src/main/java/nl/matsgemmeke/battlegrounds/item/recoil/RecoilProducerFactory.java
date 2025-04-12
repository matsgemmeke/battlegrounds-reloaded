package nl.matsgemmeke.battlegrounds.item.recoil;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RecoilSpecification;
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
     * @param specification the specification
     * @return a new producer instance
     */
    public RecoilProducer create(@NotNull RecoilSpecification specification) {
        RecoilType recoilType = RecoilType.valueOf(specification.type());

        Float[] horizontalRecoilValues = specification.horizontalRecoilValues().toArray(Float[]::new);
        Float[] verticalRecoilValues = specification.verticalRecoilValues().toArray(Float[]::new);

        switch (recoilType) {
            case CAMERA_MOVEMENT -> {
                long kickbackDuration = specification.kickbackDuration();
                float recoveryRate = specification.recoveryRate();
                long recoveryDuration = specification.recoveryDuration();
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

        throw new RecoilProducerCreationException("Invalid recoil type '%s'".formatted(recoilType));
    }
}
