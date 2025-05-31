package nl.matsgemmeke.battlegrounds.game.openmode.component.storage;

import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import org.jetbrains.annotations.NotNull;

public interface OpenModeStatePersistenceHandlerFactory {

    @NotNull
    StatePersistenceHandler create(@NotNull GunRegistry gunRegistry, @NotNull PlayerRegistry playerRegistry);
}
