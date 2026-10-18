package nl.matsgemmeke.battlegrounds.item.deploy.action;

import nl.matsgemmeke.battlegrounds.entity.damage.DamageType;
import nl.matsgemmeke.battlegrounds.sound.api.SoundSequence;
import org.bukkit.Material;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public record PlaceDeploymentProperties(
        Map<DamageType, Double> resistances,
        Material material,
        @Nullable
        SoundSequence soundSequence,
        double health,
        long cooldown
) { }
