package nl.matsgemmeke.battlegrounds.item.shoot.burst;

import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import org.jetbrains.annotations.NotNull;

public interface BurstModeFactory {

    @NotNull
    FireMode create(@NotNull Shootable item, int amountOfShots, int rateOfFire);
}
