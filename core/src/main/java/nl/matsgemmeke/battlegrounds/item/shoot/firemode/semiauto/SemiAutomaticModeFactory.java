package nl.matsgemmeke.battlegrounds.item.shoot.firemode.semiauto;

import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import org.jetbrains.annotations.NotNull;

public interface SemiAutomaticModeFactory {

    @NotNull
    FireMode create(@NotNull Shootable item, long delayBetweenShots);
}
