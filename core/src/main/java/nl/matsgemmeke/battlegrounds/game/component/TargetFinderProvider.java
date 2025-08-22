package nl.matsgemmeke.battlegrounds.game.component;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TargetFinderProvider extends ComponentRouterProvider<TargetFinder> {

    @Inject
    public TargetFinderProvider(
            @NotNull GameScope gameScope,
            @NotNull Map<GameContextType, Provider<TargetFinder>> implementations,
            @NotNull TypeLiteral<TargetFinder> typeLiteral
    ) {
        super(gameScope, implementations, typeLiteral);
    }
}
