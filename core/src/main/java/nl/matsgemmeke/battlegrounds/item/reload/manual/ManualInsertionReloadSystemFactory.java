package nl.matsgemmeke.battlegrounds.item.reload.manual;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.AmmunitionHolder;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadProperties;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import org.jetbrains.annotations.NotNull;

public interface ManualInsertionReloadSystemFactory {

    @NotNull
    ReloadSystem create(@NotNull ReloadProperties properties, @NotNull AmmunitionHolder ammunitionHolder, @NotNull AudioEmitter audioEmitter);
}
