package nl.matsgemmeke.battlegrounds.game.component.damage;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentRouterProvider;

import java.util.Map;

public class DamageProcessorProvider extends ComponentRouterProvider<DamageProcessor> {

    @Inject
    public DamageProcessorProvider(GameScope gameScope, Map<GameContextType, Provider<DamageProcessor>> implementations, TypeLiteral<DamageProcessor> typeLiteral) {
        super(gameScope, implementations, typeLiteral);
    }
}
