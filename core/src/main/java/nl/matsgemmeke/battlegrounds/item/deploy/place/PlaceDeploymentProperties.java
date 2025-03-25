package nl.matsgemmeke.battlegrounds.item.deploy.place;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public record PlaceDeploymentProperties(
        @NotNull List<GameSound> placeSounds,
        @NotNull Map<DamageType, Double> resistances,
        @NotNull Material material,
        double health
) { }
