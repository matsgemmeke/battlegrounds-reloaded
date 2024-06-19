package nl.matsgemmeke.battlegrounds.item.mechanism;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class ExplosionMechanism implements ItemMechanism {

    @NotNull
    private GameContext context;

    public ExplosionMechanism(@NotNull GameContext context) {
        this.context = context;
    }

    public void activate(@NotNull Location location) {
        System.out.println("kaboom");
    }
}
