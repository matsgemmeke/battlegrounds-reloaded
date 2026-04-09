package nl.matsgemmeke.battlegrounds.item.deploy.action;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public record PlaceDeploymentProperties(
        List<GameSound> placeSounds,
        Map<DamageType, Double> resistances,
        Material material,
        double health,
        long cooldown
) { }
