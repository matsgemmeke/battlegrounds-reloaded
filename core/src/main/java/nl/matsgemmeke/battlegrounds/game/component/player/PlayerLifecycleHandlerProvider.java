package nl.matsgemmeke.battlegrounds.game.component.player;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentRouterProvider;

import java.util.Map;

public class PlayerLifecycleHandlerProvider extends ComponentRouterProvider<PlayerLifecycleHandler> {

    @Inject
    public PlayerLifecycleHandlerProvider(GameScope gameScope, Map<GameContextType, Provider<PlayerLifecycleHandler>> implementations, TypeLiteral<PlayerLifecycleHandler> typeLiteral) {
        super(gameScope, implementations, typeLiteral);
    }
}
