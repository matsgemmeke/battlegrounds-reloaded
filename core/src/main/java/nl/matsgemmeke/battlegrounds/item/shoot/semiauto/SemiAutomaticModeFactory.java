package nl.matsgemmeke.battlegrounds.item.shoot.semiauto;

import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
import org.jetbrains.annotations.NotNull;

public interface SemiAutomaticModeFactory {

    @NotNull
    FireMode create(@NotNull Shootable item, long delayBetweenShots);
}
