package nl.matsgemmeke.battlegrounds.item.reload.magazine;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadProperties;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import org.jetbrains.annotations.NotNull;

public interface MagazineReloadSystemFactory {

    @NotNull
    ReloadSystem create(@NotNull ReloadProperties properties, @NotNull AmmunitionStorage ammunitionStorage, @NotNull AudioEmitter audioEmitter);
}
