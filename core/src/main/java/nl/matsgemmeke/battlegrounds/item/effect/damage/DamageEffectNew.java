package nl.matsgemmeke.battlegrounds.item.effect.damage;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.*;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import org.jetbrains.annotations.NotNull;

public class DamageEffectNew extends BaseItemEffectNew {

    @NotNull
    private final DamageEffectPerformanceFactory damageEffectPerformanceFactory;
    @NotNull
    private final GameContextProvider gameContextProvider;
    @NotNull
    private final GameKey gameKey;
    @NotNull
    private final GameScope gameScope;
    private DamageType damageType;
    private RangeProfile rangeProfile;

    @Inject
    public DamageEffectNew(
            @NotNull DamageEffectPerformanceFactory damageEffectPerformanceFactory,
            @NotNull GameContextProvider gameContextProvider,
            @NotNull GameKey gameKey,
            @NotNull GameScope gameScope
    ) {
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

        for (TriggerExecutor triggerExecutor : triggerExecutors) {
            TriggerContext triggerContext = new TriggerContext(context.getEntity(), context.getSource());
            TriggerRun triggerRun = triggerExecutor.createTriggerRun(triggerContext);

            performance.addTriggerRun(triggerRun);
        }

        performance.perform(context);
        performances.add(performance);
        return performance;
    }

    private <T> T verifyRequiredVariable(String name, T variable) {
        if (variable == null) {
            throw new ItemEffectPerformanceException("Unable to perform damage effect: Required variable '%s' is not provided".formatted(name));
        }

        return variable;
    }
}
