package nl.matsgemmeke.battlegrounds.item.equipment.mechanism;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import org.jetbrains.annotations.NotNull;

public class ExplosionMechanism implements EquipmentMechanism {

    @NotNull
    private GameContext context;

    public ExplosionMechanism(@NotNull GameContext context) {
        this.context = context;
    }

    public void activate() {
        System.out.println("kaboom");
    }
}
