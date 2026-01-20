package nl.matsgemmeke.battlegrounds.game.component.entity;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentRouterProvider;

import java.util.Map;

public class GameEntityFinderProvider extends ComponentRouterProvider<GameEntityFinder> {

    @Inject
    public GameEntityFinderProvider(GameScope gameScope, Map<GameContextType, Provider<GameEntityFinder>> implementations, TypeLiteral<GameEntityFinder> typeLiteral) {
        super(gameScope, implementations, typeLiteral);
    }
}
