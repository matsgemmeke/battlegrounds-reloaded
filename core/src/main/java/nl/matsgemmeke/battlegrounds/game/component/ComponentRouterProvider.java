package nl.matsgemmeke.battlegrounds.game.component;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;

import java.util.Map;

public abstract class ComponentRouterProvider<T> implements Provider<T> {

    private final GameScope gameScope;
    private final Map<GameContextType, Provider<T>> providers;
    private final TypeLiteral<T> typeLiteral;

    public ComponentRouterProvider(GameScope gameScope, Map<GameContextType, Provider<T>> providers, TypeLiteral<T> typeLiteral) {
        this.gameScope = gameScope;
        this.providers = providers;
        this.typeLiteral = typeLiteral;
    }

    @Override
    public T get() {
        GameContext gameContext = gameScope.getCurrentGameContext().orElseThrow(() -> {
            String message = String.format("Cannot provide instance of %s: the game scope is empty", this.getTypeName());
            return new ComponentProvisionException(message);
        });

        Provider<T> provider = providers.get(gameContext.getType());

        if (provider == null) {
            String message = String.format("Cannot provide instance of %s: no implementation bound for %s", this.getTypeName(), gameContext.getType());
            throw new ComponentProvisionException(message);
        }

        return provider.get();
    }

    private String getTypeName() {
        return typeLiteral.getRawType().getSimpleName();
    }
}
