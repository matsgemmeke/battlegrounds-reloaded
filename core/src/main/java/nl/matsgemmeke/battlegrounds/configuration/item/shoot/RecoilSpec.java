package nl.matsgemmeke.battlegrounds.configuration.item.shoot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents the immutable, validated configuration for a recoil system loaded from a YAML file.
 *
 * @param type                   the recoil type
 * @param horizontalRecoilValues the list of possible values for horizontal recoil
 * @param verticalRecoilValues   the list of possible values for vertical recoil
 * @param kickbackDuration       the duration in ticks of kickback movements caused by the recoil, will only be
 *                               non-null when type equals to {@code CAMERA_MOVEMENT}
 * @param recoveryRate           the amount of kickback recovery measured between 0-1, will only be non-null when type
 *                               equals to {@code CAMERA_MOVEMENT}
 * @param recoveryDuration       the duration in ticks of kickback recovery, will only be non-null when type equals to
 *                               {@code CAMERA_MOVEMENT}
 */
public record RecoilSpec(
        @NotNull String type,
        @NotNull List<Float> horizontalRecoilValues,
        @NotNull List<Float> verticalRecoilValues,
        @Nullable Long kickbackDuration,
        @Nullable Float recoveryRate,
        @Nullable Long recoveryDuration
) { }
