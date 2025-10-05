package nl.matsgemmeke.battlegrounds.item.effect.flash;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformanceException;

public class FlashEffectNew extends BaseItemEffectNew {

    private final FlashEffectPerformanceFactory flashEffectPerformanceFactory;
    private final GameContextProvider gameContextProvider;
    private final GameKey gameKey;
    private final GameScope gameScope;
    private FlashProperties properties;

    @Inject
    public FlashEffectNew(FlashEffectPerformanceFactory flashEffectPerformanceFactory, GameContextProvider gameContextProvider, GameKey gameKey, GameScope gameScope) {
        this.flashEffectPerformanceFactory = flashEffectPerformanceFactory;
        this.gameContextProvider = gameContextProvider;
        this.gameKey = gameKey;
        this.gameScope = gameScope;
    }

    public void setProperties(FlashProperties properties) {
        this.properties = properties;
    }

    @Override
    public void startPerformance(ItemEffectContext context) {
        if (properties == null) {
            throw new ItemEffectPerformanceException("Unable to perform flash effect: properties not set");
        }

        GameContext gameContext = gameContextProvider.getGameContext(gameKey).orElse(null);

        if (gameContext == null) {
            throw new ItemEffectPerformanceException("Unable to perform flash effect: no game context for game key %s can be found".formatted(gameKey));
        }

        ItemEffectPerformance performance = gameScope.supplyInScope(gameContext, () -> flashEffectPerformanceFactory.create(properties));

        this.startPerformance(performance, context);
    }
}
