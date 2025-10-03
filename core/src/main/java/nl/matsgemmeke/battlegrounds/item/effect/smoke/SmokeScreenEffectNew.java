package nl.matsgemmeke.battlegrounds.item.effect.smoke;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformanceException;

public class SmokeScreenEffectNew extends BaseItemEffectNew {

    private final GameContextProvider gameContextProvider;
    private final GameKey gameKey;
    private final GameScope gameScope;
    private final SmokeScreenEffectPerformanceFactory smokeScreenEffectPerformanceFactory;
    private SmokeScreenProperties properties;

    @Inject
    public SmokeScreenEffectNew(GameContextProvider gameContextProvider, GameKey gameKey, GameScope gameScope, SmokeScreenEffectPerformanceFactory smokeScreenEffectPerformanceFactory) {
        this.gameContextProvider = gameContextProvider;
        this.gameKey = gameKey;
        this.gameScope = gameScope;
        this.smokeScreenEffectPerformanceFactory = smokeScreenEffectPerformanceFactory;
    }

    public void setProperties(SmokeScreenProperties properties) {
        this.properties = properties;
    }

    @Override
    public ItemEffectPerformance start(ItemEffectContext context) {
        if (properties == null) {
            throw new ItemEffectPerformanceException("Unable to perform smoke screen effect: properties not set");
        }

        GameContext gameContext = gameContextProvider.getGameContext(gameKey).orElse(null);

        if (gameContext == null) {
            throw new ItemEffectPerformanceException("Unable to perform smoke screen effect: no game context for game key %s can be found".formatted(gameKey));
        }

        ItemEffectPerformance performance = gameScope.supplyInScope(gameContext, () -> smokeScreenEffectPerformanceFactory.create(properties));

        this.startPerformance(performance, context);
        return performance;
    }
}
