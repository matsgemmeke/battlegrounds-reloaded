package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.item.effect.*;
import org.jetbrains.annotations.NotNull;

public class ExplosionEffectNew extends BaseItemEffectNew {

    private final ExplosionEffectPerformanceFactory explosionEffectPerformanceFactory;
    private final GameContextProvider gameContextProvider;
    private final GameKey gameKey;
    private final GameScope gameScope;
    private ExplosionProperties properties;

    @Inject
    public ExplosionEffectNew(ExplosionEffectPerformanceFactory explosionEffectPerformanceFactory, GameContextProvider gameContextProvider, GameKey gameKey, GameScope gameScope) {
        this.explosionEffectPerformanceFactory = explosionEffectPerformanceFactory;
        this.gameContextProvider = gameContextProvider;
        this.gameKey = gameKey;
        this.gameScope = gameScope;
    }

    public void setProperties(ExplosionProperties properties) {
        this.properties = properties;
    }

    @Override
    public ItemEffectPerformance start(@NotNull ItemEffectContext context) {
        if (properties == null) {
            throw new ItemEffectPerformanceException("Unable to perform explosion effect: properties not set");
        }

        GameContext gameContext = gameContextProvider.getGameContext(gameKey).orElse(null);

        if (gameContext == null) {
            throw new ItemEffectPerformanceException("Unable to perform explosion effect: no game context for game key %s can be found".formatted(gameKey));
        }

        ItemEffectPerformance performance = gameScope.supplyInScope(gameContext, () -> explosionEffectPerformanceFactory.create(properties));

        this.startPerformance(performance, context);
        return performance;
    }
}
