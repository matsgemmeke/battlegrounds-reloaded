package nl.matsgemmeke.battlegrounds.item.shoot.firemode.fullauto;

import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import org.jetbrains.annotations.NotNull;

public interface FullyAutomaticModeFactory {

    @NotNull
    FireMode create(@NotNull Shootable item, int rateOfFire);
}
