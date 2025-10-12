package nl.matsgemmeke.battlegrounds.item.effect.simulation;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformanceException;

public class GunFireSimulationEffect extends BaseItemEffect {

    private final GameContextProvider gameContextProvider;
    private final GameKey gameKey;
    private final GameScope gameScope;
    private final GunFireSimulationEffectPerformanceFactory gunFireSimulationEffectPerformanceFactory;
    private GunFireSimulationProperties properties;

    @Inject
    public GunFireSimulationEffect(GameContextProvider gameContextProvider, GameKey gameKey, GameScope gameScope, GunFireSimulationEffectPerformanceFactory gunFireSimulationEffectPerformanceFactory) {
        this.gameContextProvider = gameContextProvider;
        this.gameKey = gameKey;
        this.gameScope = gameScope;
        this.gunFireSimulationEffectPerformanceFactory = gunFireSimulationEffectPerformanceFactory;
    }

    public void setProperties(GunFireSimulationProperties properties) {
        this.properties = properties;
    }

    @Override
    public void startPerformance(ItemEffectContext context) {
        if (properties == null) {
            throw new ItemEffectPerformanceException("Unable to perform gun fire simulation effect: properties not set");
        }

        GameContext gameContext = gameContextProvider.getGameContext(gameKey).orElse(null);

        if (gameContext == null) {
            throw new ItemEffectPerformanceException("Unable to perform gun fire simulation effect: no game context for game key %s can be found".formatted(gameKey));
        }

        ItemEffectPerformance performance = gameScope.supplyInScope(gameContext, () -> gunFireSimulationEffectPerformanceFactory.create(properties));

        this.startPerformance(performance, context);
    }
}
