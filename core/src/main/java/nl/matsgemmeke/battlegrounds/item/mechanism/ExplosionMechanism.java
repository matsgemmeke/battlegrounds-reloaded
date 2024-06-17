package nl.matsgemmeke.battlegrounds.item.mechanism;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import org.jetbrains.annotations.NotNull;

public class ExplosionMechanism implements ItemMechanism {

    @NotNull
    private GameContext context;

    public ExplosionMechanism(@NotNull GameContext context) {
        this.context = context;
    }

    public void activate() {
        System.out.println("kaboom");
    }
}
