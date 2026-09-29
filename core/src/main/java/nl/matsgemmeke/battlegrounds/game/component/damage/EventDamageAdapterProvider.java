package nl.matsgemmeke.battlegrounds.game.component.damage;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentRouterProvider;

import java.util.Map;

public class EventDamageAdapterProvider extends ComponentRouterProvider<EventDamageAdapter> {

    @Inject
    public EventDamageAdapterProvider(GameScope gameScope, Map<GameContextType, Provider<EventDamageAdapter>> providers, TypeLiteral<EventDamageAdapter> typeLiteral) {
        super(gameScope, providers, typeLiteral);
    }
}
