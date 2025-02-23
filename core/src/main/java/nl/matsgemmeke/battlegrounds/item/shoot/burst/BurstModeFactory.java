package nl.matsgemmeke.battlegrounds.item.shoot.burst;

import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import org.jetbrains.annotations.NotNull;

public interface BurstModeFactory {

    @NotNull
    FireMode create(@NotNull Shootable item, @Assisted("AmountOfShots") int amountOfShots, @Assisted("RateOfFire") int rateOfFire);
}
