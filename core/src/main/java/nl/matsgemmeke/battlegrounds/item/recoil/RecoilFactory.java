package nl.matsgemmeke.battlegrounds.item.recoil;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.RecoilSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Timer;

/**
 * Factory class responsible for instantiating {@link Recoil} implementation classes.
 */
public class RecoilFactory {

    @NotNull
    private final BattlegroundsConfiguration config;

    @Inject
    public RecoilFactory(@NotNull BattlegroundsConfiguration config) {
        this.config = config;
    }

    /**
     * Creates a new {@link Recoil} instance based on configuration values.
     *
     * @param spec the specification
     * @return a new recoil instance
     */
    public Recoil create(@NotNull RecoilSpec spec) {
        RecoilType recoilType = RecoilType.valueOf(spec.type);

        Float[] horizontalRecoilValues = spec.horizontal;
        Float[] verticalRecoilValues = spec.vertical;

        switch (recoilType) {
            case CAMERA_MOVEMENT -> {
                long kickbackDuration = Optional.ofNullable(spec.kickbackDuration).orElse(0L);
                float recoveryRate = Optional.ofNullable(spec.recoveryRate).orElse(0.0f);
                long recoveryDuration = Optional.ofNullable(spec.recoveryDuration).orElse(0L);
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

        throw new RecoilCreationException("Invalid recoil type '%s'".formatted(recoilType));
    }
}
