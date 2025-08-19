package nl.matsgemmeke.battlegrounds.game.component.storage;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentRouterProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class StatePersistenceHandlerProvider extends ComponentRouterProvider<StatePersistenceHandler> {

    @Inject
    public StatePersistenceHandlerProvider(
            @NotNull GameScope gameScope,
            @NotNull Map<GameContextType, Provider<StatePersistenceHandler>> implementations,
            @NotNull TypeLiteral<StatePersistenceHandler> typeLiteral
    ) {
        super(gameScope, implementations, typeLiteral);
    }
}
