package com.github.matsgemmeke.battlegrounds.item.recoil;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.item.factory.WeaponFactoryCreationException;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;

/**
 * Factory class responsible for instantiating {@link RecoilSystem} implementation classes.
 */
public class RecoilSystemFactory {

    @NotNull
    private InternalsProvider internals;

    public RecoilSystemFactory(@NotNull InternalsProvider internals) {
        this.internals = internals;
    }

    /**
     * Creates a new {@link RecoilSystem} instance based on configuration values.
     *
     * @param section the configuration section
     * @return a new {@link RecoilSystem} instance
     */
    public RecoilSystem make(@NotNull Section section) {
        String type = section.getString("type");
        RecoilSystemType recoilSystemType;

        try {
            recoilSystemType = RecoilSystemType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new WeaponFactoryCreationException("Error while getting recoil system type \"" + type + "\"");
        }

        Float[] horizontalRecoilValues = section.getFloatList("horizontal").toArray(Float[]::new);
        Float[] verticalRecoilValues = section.getFloatList("vertical").toArray(Float[]::new);

        switch (recoilSystemType) {
            case CAMERA_MOVEMENT -> {
                float recoveryRate = section.getFloat("recovery-rate");
                long recoveryDuration = section.getLong("recovery-duration");
                long rotationDuration = section.getLong("rotation-duration");

                Timer timer = new Timer();

                CameraMovementRecoil recoilSystem = new CameraMovementRecoil(internals, timer);
                recoilSystem.setHorizontalRecoilValues(horizontalRecoilValues);
                recoilSystem.setVerticalRecoilValues(verticalRecoilValues);
                recoilSystem.setRecoilDuration(rotationDuration);
                recoilSystem.setRecoveryDuration(recoveryDuration);
                recoilSystem.setRecoveryRate(recoveryRate);

                return recoilSystem;
            }
            case RANDOM_SPREAD -> {
                RandomSpreadRecoil recoilSystem = new RandomSpreadRecoil();
                recoilSystem.setHorizontalRecoilValues(horizontalRecoilValues);
                recoilSystem.setVerticalRecoilValues(verticalRecoilValues);

                return recoilSystem;
            }
        }

        throw new WeaponFactoryCreationException("Invalid recoil system type \"" + type + "\"");
    }
}
