package nl.matsgemmeke.battlegrounds.item.controls;

import nl.matsgemmeke.battlegrounds.item.ItemUser;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ItemController<T extends ItemUser> {

    private final ConcurrentMap<Action, Set<ActionBinding<T>>> bindings;

    public ItemController() {
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
                .map(ActionBinding::function)
                .filter(Function::isPerforming)
                .forEach(Function::cancel);
    }

    public ActionResult performAction(Action action, T user) {
        if (this.isPerformingBlockingFunction()) {
            return ActionResult.ignore();
        }

        Set<ActionBinding<T>> bindings = this.bindings.get(action);

        if (bindings == null || bindings.isEmpty()) {
            return ActionResult.ignore();
        }

        boolean performed = false;
        boolean cancelEvent = false;

        for (ActionBinding<T> binding : bindings) {
            Function<T> function = binding.function();

            if (!function.isPerforming()) {
                FunctionResult result = function.perform(user);

                performed = performed || result == FunctionResult.SUCCESS;
                cancelEvent = cancelEvent || result == FunctionResult.SUCCESS && binding.cancelsEvent();

                if (result == FunctionResult.SUCCESS && binding.stopsChain()) {
                    break;
                }
            }
        }

        return new ActionResult(performed, cancelEvent);
    }

    private boolean isPerformingBlockingFunction() {
        return bindings.values().stream()
                .flatMap(Set::stream)
                .anyMatch(entry -> entry.function().isPerforming() && entry.blocking());
    }
}
