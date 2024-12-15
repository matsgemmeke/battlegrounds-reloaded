package nl.matsgemmeke.battlegrounds.game.component.info.gun;

import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GunInfoProvider {

    @Nullable
    GunFireSimulationInfo getGunFireSimulationInfo(@NotNull GunHolder holder);
}
