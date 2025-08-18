package nl.matsgemmeke.battlegrounds.game.component.damage;

import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentRouterProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DamageProcessorProvider extends ComponentRouterProvider<DamageProcessor> {

    public DamageProcessorProvider(
            @NotNull GameScope gameScope,
            @NotNull Map<GameContextType, Provider<DamageProcessor>> implementations,
            @NotNull TypeLiteral<DamageProcessor> typeLiteral
    ) {
        super(gameScope, implementations, typeLiteral);
    }
}
