package nl.matsgemmeke.battlegrounds.item.effect.damage;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.item.effect.*;

public class DamageEffect extends BaseItemEffect {

    private final DamageEffectPerformanceFactory damageEffectPerformanceFactory;
    private final GameContextProvider gameContextProvider;
    private final GameKey gameKey;
    private final GameScope gameScope;
    private DamageProperties properties;

    @Inject
    public DamageEffect(DamageEffectPerformanceFactory damageEffectPerformanceFactory, GameContextProvider gameContextProvider, GameKey gameKey, GameScope gameScope) {
        this.damageEffectPerformanceFactory = damageEffectPerformanceFactory;
        this.gameContextProvider = gameContextProvider;
        this.gameKey = gameKey;
        this.gameScope = gameScope;
    }

    public void setProperties(DamageProperties properties) {
        this.properties = properties;
    }

    @Override
    public void startPerformance(ItemEffectContext context) {
        if (properties == null) {
            throw new ItemEffectPerformanceException("Unable to perform damage effect: properties not set");
        }

        GameContext gameContext = gameContextProvider.getGameContext(gameKey).orElse(null);

        if (gameContext == null) {
            throw new ItemEffectPerformanceException("Unable to perform damage effect: No game context for game key %s can be found".formatted(gameKey));
        }

        ItemEffectPerformance performance = gameScope.supplyInScope(gameContext, () -> damageEffectPerformanceFactory.create(properties));

        this.startPerformance(performance, context);
    }
}
