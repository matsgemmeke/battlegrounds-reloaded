package nl.matsgemmeke.battlegrounds.configuration.spec.item;

import java.util.List;

public record RecoilSpec(
        String type,
        List<Float> horizontalRecoilValues,
        List<Float> verticalRecoilValues,
        Long kickbackDuration,
        Float recoveryRate,
        Long recoveryDuration
) { }
