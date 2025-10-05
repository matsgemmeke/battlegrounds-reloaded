package nl.matsgemmeke.battlegrounds.item.effect.combustion;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffectNew;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformanceException;

public class CombustionEffectNew extends BaseItemEffectNew {

    private final CombustionEffectPerformanceFactory combustionEffectPerformanceFactory;
    private final GameContextProvider gameContextProvider;
    private final GameKey gameKey;
    private final GameScope gameScope;
    private CombustionProperties properties;

    @Inject
    public CombustionEffectNew(
            CombustionEffectPerformanceFactory combustionEffectPerformanceFactory,
            GameContextProvider gameContextProvider,
            GameKey gameKey,
            GameScope gameScope
    ) {
        this.combustionEffectPerformanceFactory = combustionEffectPerformanceFactory;
        this.gameContextProvider = gameContextProvider;
        this.gameKey = gameKey;
        this.gameScope = gameScope;
    }

    public void setProperties(CombustionProperties properties) {
        this.properties = properties;
    }

    @Override
    public void startPerformance(ItemEffectContext context) {
        if (properties == null) {
            throw new ItemEffectPerformanceException("Unable to perform combustion effect: properties not set");
        }

        GameContext gameContext = gameContextProvider.getGameContext(gameKey).orElse(null);

        if (gameContext == null) {
            throw new ItemEffectPerformanceException("Unable to perform combustion effect: no game context for game key %s can be found".formatted(gameKey));
        }

        ItemEffectPerformance performance = gameScope.supplyInScope(gameContext, () -> combustionEffectPerformanceFactory.create(properties));

        this.startPerformance(performance, context);
    }
}
