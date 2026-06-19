package nl.matsgemmeke.battlegrounds.game.component.spawn;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentRouterProvider;

import java.util.Map;

public class RespawnHandlerProvider extends ComponentRouterProvider<RespawnHandler> {

    public RespawnHandlerProvider(GameScope gameScope, Map<GameContextType, Provider<RespawnHandler>> implementations, TypeLiteral<RespawnHandler> typeLiteral) {
        super(gameScope, implementations, typeLiteral);
    }
}
