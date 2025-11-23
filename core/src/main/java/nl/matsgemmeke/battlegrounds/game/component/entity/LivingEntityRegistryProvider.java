package nl.matsgemmeke.battlegrounds.game.component.entity;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentRouterProvider;

import java.util.Map;

public class LivingEntityRegistryProvider extends ComponentRouterProvider<LivingEntityRegistry> {

    @Inject
    public LivingEntityRegistryProvider(GameScope gameScope, Map<GameContextType, Provider<LivingEntityRegistry>> implementations, TypeLiteral<LivingEntityRegistry> typeLiteral) {
        super(gameScope, implementations, typeLiteral);
    }
}

