package nl.matsgemmeke.battlegrounds.item.deploy.action;

import nl.matsgemmeke.battlegrounds.entity.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;

import java.util.Map;

public record DropDeploymentProperties(
        ItemTemplate itemTemplate,
        Map<DamageType, Double> resistances,
        double health,
        double velocity,
        long cooldown
) { }
