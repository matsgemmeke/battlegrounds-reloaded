package nl.matsgemmeke.battlegrounds.game.component.controls;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.component.controls.handler.GunActionHandler;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;

import java.util.Arrays;

public class ActionDispatcherProvider implements Provider<ActionDispatcher> {

    private final GunActionHandler gunActionHandler;
    private final PlayerRegistry playerRegistry;

    @Inject
    public ActionDispatcherProvider(PlayerRegistry playerRegistry, GunActionHandler gunActionHandler) {
        this.playerRegistry = playerRegistry;
        this.gunActionHandler = gunActionHandler;
    }

    @Override
    public ActionDispatcher get() {
        ActionDispatcher actionDispatcher = new ActionDispatcher(playerRegistry);

        Arrays.stream(Action.values()).forEach(action -> actionDispatcher.registerActionHandler(action, gunActionHandler));

        return actionDispatcher;
    }
}
