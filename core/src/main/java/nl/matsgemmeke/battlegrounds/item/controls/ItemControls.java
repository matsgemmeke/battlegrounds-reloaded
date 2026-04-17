package nl.matsgemmeke.battlegrounds.item.controls;

import nl.matsgemmeke.battlegrounds.item.ItemUser;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ItemControls<T extends ItemUser> {

    private final ConcurrentMap<Action, Set<ActionBinding<T>>> bindings;

    public ItemControls() {
        this.bindings = new ConcurrentHashMap<>();
    }

    public void bind(Action action, ActionBinding<T> functionEntry) {
        bindings.computeIfAbsent(action, k -> new TreeSet<>(
                Comparator.comparingInt(ActionBinding<T>::priority)
                        .thenComparing(e -> e.function().hashCode())
        )).add(functionEntry);
    }

    public void cancelAllFunctions() {
        bindings.values().stream()
                .flatMap(Set::stream)
                .filter(entry -> entry.function().isPerforming())
                .forEach(entry -> entry.function().cancel());
    }

    public void performAction(Action action, T user) {
        if (this.isPerformingBlockingFunction()) {
            return;
        }

        Set<ActionBinding<T>> bindings = this.bindings.get(action);

        if (bindings == null || bindings.isEmpty()) {
            return;
        }

        for (ActionBinding<T> binding : bindings) {
            Function<T> function = binding.function();

            if (!function.isPerforming()) {
                FunctionResult result = function.perform(user);

                if (result == FunctionResult.SUCCESS && binding.stopsChain()) {
                    break;
                }
            }
        }
    }

    private boolean isPerformingBlockingFunction() {
        return bindings.values().stream()
                .flatMap(Set::stream)
                .anyMatch(entry -> entry.function().isPerforming() && entry.blocking());
    }
}
