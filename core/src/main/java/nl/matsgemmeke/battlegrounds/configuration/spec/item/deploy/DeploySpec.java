package nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy;

import java.util.Map;

public record DeploySpec(
        Double health,
        Boolean destroyOnActivate,
        Boolean destroyOnRemove,
        Boolean destroyOnReset,
        Map<String, Double> resistances,
        ThrowPropertiesSpec throwPropertiesSpec,
        CookPropertiesSpec cookPropertiesSpec,
        PlacePropertiesSpec placePropertiesSpec
) { }
