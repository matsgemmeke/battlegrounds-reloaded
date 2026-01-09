package nl.matsgemmeke.battlegrounds.item.controls;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ItemControls<T extends ItemHolder> {

    private ConcurrentMap<Action, List<ItemFunction<T>>> controls;
    private Set<ItemFunction<T>> performingFunctions;

    public ItemControls() {
        this.controls = new ConcurrentHashMap<>();
        this.performingFunctions = new HashSet<>();
    }

    public void addControl(Action action, ItemFunction<T> function) {
        controls.putIfAbsent(action, new ArrayList<>());
        controls.get(action).add(function);

        performingFunctions.add(function);
    }

    public void cancelAllFunctions() {
        for (ItemFunction<T> function : performingFunctions) {
            if (function.isPerforming()) {
                function.cancel();
            }
        }
    }

    public void performAction(Action action, T holder) {
        if (this.isPerformingBlockingFunction()) {
            return;
        }

        List<ItemFunction<T>> functions = controls.get(action);

        if (functions == null || functions.isEmpty()) {
            return;
        }

        for (ItemFunction<T> function : functions) {
            if (function.isAvailable() && function.perform(holder)) {
                break;
            }
        }
    }

    private boolean isPerformingBlockingFunction() {
        for (ItemFunction<T> function : performingFunctions) {
            if (function.isPerforming() && function.isBlocking()) {
                return true;
            }
        }
        return false;
    }
}
