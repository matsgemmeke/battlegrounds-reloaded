package nl.matsgemmeke.battlegrounds.item.shoot.spread;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SingleProjectileSpreadPattern implements SpreadPattern {

    @NotNull
    public Iterable<Location> getProjectileDirections(@NotNull Location aimDirection) {
        return List.of(aimDirection);
    }
}
