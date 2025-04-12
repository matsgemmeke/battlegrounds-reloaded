package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import java.util.List;

public record RecoilSpecification(
        String type,
        List<Float> horizontalRecoilValues,
        List<Float> verticalRecoilValues,
        Long kickbackDuration,
        Float recoveryRate,
        Long recoveryDuration
) { }
