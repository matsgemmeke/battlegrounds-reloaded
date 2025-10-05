package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformanceException;

public class MarkSpawnPointEffectNew extends BaseItemEffectNew {

    private final GameContextProvider gameContextProvider;
    private final GameKey gameKey;
    private final GameScope gameScope;
    private final Provider<MarkSpawnPointEffectPerformance> markSpawnPointEffectPerformanceProvider;

    @Inject
    public MarkSpawnPointEffectNew(GameContextProvider gameContextProvider, GameKey gameKey, GameScope gameScope, Provider<MarkSpawnPointEffectPerformance> markSpawnPointEffectPerformanceProvider) {
        this.gameContextProvider = gameContextProvider;
        this.gameKey = gameKey;
        this.gameScope = gameScope;
        this.markSpawnPointEffectPerformanceProvider = markSpawnPointEffectPerformanceProvider;
    }

    @Override
    public void startPerformance(ItemEffectContext context) {
        GameContext gameContext = gameContextProvider.getGameContext(gameKey).orElse(null);

        if (gameContext == null) {
            throw new ItemEffectPerformanceException("Unable to perform mark spawn point effect: no game context for game key %s can be found".formatted(gameKey));
        }

        ItemEffectPerformance performance = gameScope.supplyInScope(gameContext, markSpawnPointEffectPerformanceProvider::get);

        this.startPerformance(performance, context);
    }
}
