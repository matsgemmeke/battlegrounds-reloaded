package nl.matsgemmeke.battlegrounds.item.shoot.firemode.burst;

import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import org.jetbrains.annotations.NotNull;

public interface BurstModeFactory {

    @NotNull
    FireMode create(@NotNull Shootable item, @Assisted("AmountOfShots") int amountOfShots, @Assisted("RateOfFire") int rateOfFire);
}
