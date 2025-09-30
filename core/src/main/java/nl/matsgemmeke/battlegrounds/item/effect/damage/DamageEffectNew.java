package nl.matsgemmeke.battlegrounds.item.effect.damage;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.*;

public class DamageEffectNew extends BaseItemEffectNew {

    private final DamageEffectPerformanceFactory damageEffectPerformanceFactory;
    private final GameContextProvider gameContextProvider;
    private final GameKey gameKey;
    private final GameScope gameScope;
    private DamageType damageType;
    private RangeProfile rangeProfile;

    @Inject
    public DamageEffectNew(DamageEffectPerformanceFactory damageEffectPerformanceFactory, GameContextProvider gameContextProvider, GameKey gameKey, GameScope gameScope) {
        this.damageEffectPerformanceFactory = damageEffectPerformanceFactory;
        this.gameContextProvider = gameContextProvider;
        this.gameKey = gameKey;
        this.gameScope = gameScope;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    public void setRangeProfile(RangeProfile rangeProfile) {
        this.rangeProfile = rangeProfile;
    }

    public ItemEffectPerformance start(ItemEffectContext context) {
        DamageType damageType = this.verifyRequiredVariable("damageType", this.damageType);
        RangeProfile rangeProfile = this.verifyRequiredVariable("rangeProfile", this.rangeProfile);

        GameContext gameContext = gameContextProvider.getGameContext(gameKey).orElse(null);

        if (gameContext == null) {
            throw new ItemEffectPerformanceException("Unable to perform damage effect: No game context for game key %s can be found".formatted(gameKey));
        }

        DamageProperties properties = new DamageProperties(rangeProfile, damageType);
        ItemEffectPerformance performance = gameScope.supplyInScope(gameContext, () -> damageEffectPerformanceFactory.create(properties));

        this.startPerformance(performance, context);
        return performance;
    }

    private <T> T verifyRequiredVariable(String name, T variable) {
        if (variable == null) {
            throw new ItemEffectPerformanceException("Unable to perform damage effect: Required variable '%s' is not provided".formatted(name));
        }

        return variable;
    }
}
