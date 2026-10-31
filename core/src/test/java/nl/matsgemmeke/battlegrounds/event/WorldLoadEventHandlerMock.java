package nl.matsgemmeke.battlegrounds.event;

import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldLoadEventHandlerMock implements EventHandler<WorldLoadEvent> {

    private final Procedure onHandle;

    public WorldLoadEventHandlerMock(Procedure onHandle) {
        this.onHandle = onHandle;
    }

    public void handle(WorldLoadEvent event) {
        onHandle.apply();
    }
}
