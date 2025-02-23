package nl.matsgemmeke.battlegrounds.item.shoot.fullauto;

import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import org.jetbrains.annotations.NotNull;

public interface FullyAutomaticModeFactory {

    @NotNull
    FireMode create(@NotNull Shootable item, int rateOfFire);
}
