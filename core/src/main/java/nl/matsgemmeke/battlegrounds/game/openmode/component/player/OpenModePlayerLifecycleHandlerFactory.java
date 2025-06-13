package nl.matsgemmeke.battlegrounds.game.openmode.component.player;

import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.ItemLifecycleHandler;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import org.jetbrains.annotations.NotNull;

public interface OpenModePlayerLifecycleHandlerFactory {

    @NotNull
    PlayerLifecycleHandler create(@NotNull ItemLifecycleHandler itemLifecycleHandler, @NotNull PlayerRegistry playerRegistry, @NotNull StatePersistenceHandler statePersistenceHandler);
}
