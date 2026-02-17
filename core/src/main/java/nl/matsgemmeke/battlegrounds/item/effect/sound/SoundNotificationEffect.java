package nl.matsgemmeke.battlegrounds.item.effect.sound;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.item.effect.BaseItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformance;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectPerformanceException;

public class SoundNotificationEffect extends BaseItemEffect {

    private final GameContextProvider gameContextProvider;
    private final GameKey gameKey;
    private final GameScope gameScope;
    private final SoundNotificationEffectPerformanceFactory soundNotificationEffectPerformanceFactory;
    private SoundNotificationProperties properties;

    @Inject
    public SoundNotificationEffect(GameContextProvider gameContextProvider, GameKey gameKey, GameScope gameScope, SoundNotificationEffectPerformanceFactory soundNotificationEffectPerformanceFactory) {
        this.gameContextProvider = gameContextProvider;
        this.gameKey = gameKey;
        this.gameScope = gameScope;
        this.soundNotificationEffectPerformanceFactory = soundNotificationEffectPerformanceFactory;
    }

    public void setProperties(SoundNotificationProperties properties) {
        this.properties = properties;
    }

    @Override
    public void startPerformance(ItemEffectContext context) {
        if (properties == null) {
            throw new ItemEffectPerformanceException("Unable to perform sound notification effect: properties not set");
        }

        GameContext gameContext = gameContextProvider.getGameContext(gameKey).orElse(null);

        if (gameContext == null) {
            throw new ItemEffectPerformanceException("Unable to perform sound notification effect: no game context for game key %s can be found".formatted(gameKey));
        }

        ItemEffectPerformance performance = gameScope.supplyInScope(gameContext, () -> soundNotificationEffectPerformanceFactory.create(properties));

        this.startPerformance(performance, context);
    }
}
