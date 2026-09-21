package nl.matsgemmeke.battlegrounds.game.component.targeting;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentRouterProvider;

import java.util.Map;

public class TargetFinderProvider extends ComponentRouterProvider<TargetFinder> {

    @Inject
    public TargetFinderProvider(GameScope gameScope, Map<GameContextType, Provider<TargetFinder>> providers, TypeLiteral<TargetFinder> typeLiteral) {
        super(gameScope, providers, typeLiteral);
    }
}
