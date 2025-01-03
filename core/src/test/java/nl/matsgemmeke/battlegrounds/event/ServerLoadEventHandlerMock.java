package nl.matsgemmeke.battlegrounds.event;

import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.event.server.ServerLoadEvent;
import org.jetbrains.annotations.NotNull;

public class ServerLoadEventHandlerMock implements EventHandler<ServerLoadEvent> {

    private Procedure onHandle;

    public ServerLoadEventHandlerMock(Procedure onHandle) {
        this.onHandle = onHandle;
    }

    public void handle(@NotNull ServerLoadEvent event) {
        onHandle.apply();
    }
}
