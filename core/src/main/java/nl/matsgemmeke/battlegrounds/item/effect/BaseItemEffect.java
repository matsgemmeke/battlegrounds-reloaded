package nl.matsgemmeke.battlegrounds.item.effect;

import java.util.*;

public abstract class BaseItemEffect implements ItemEffect {

    private final List<ItemEffectPerformance> performances;

    public BaseItemEffect() {
        this.performances = new ArrayList<>();
    }

    @Override
    public Optional<ItemEffectPerformance> getLatestPerformance() {
        if (performances.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(performances.get(performances.size() - 1));
    }

    protected void startPerformance(ItemEffectPerformance performance, ItemEffectContext context) {
        performances.add(performance);

        performance.setContext(context);
        performance.start();
    }

    @Override
    public void activatePerformances() {
        for (ItemEffectPerformance performance : performances) {
            if (!performance.isPerforming()) {
                performance.cancel();
                performance.start();
            }
        }

        performances.clear();
    }

    @Override
    public void cancelPerformances() {
        for (ItemEffectPerformance performance : performances) {
            if (!performance.isPerforming()) {
                performance.cancel();
            }
        }

        performances.clear();
    }

    @Override
    public void rollbackPerformances() {
        for (ItemEffectPerformance performance : performances) {
            if (performance.isPerforming()) {
                performance.rollback();
            }
        }

        performances.clear();
    }
}
