package nl.matsgemmeke.battlegrounds.item.controls;

import nl.matsgemmeke.battlegrounds.entity.ItemHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ItemControls<T extends ItemHolder> {

    @NotNull
    private ConcurrentMap<Action, List<ItemFunction<T>>> controls;
    @NotNull
    private Set<ItemFunction<T>> performingFunctions;

    public ItemControls() {
        this.controls = new ConcurrentHashMap<>();
        this.performingFunctions = new HashSet<>();
    }

    public void addControl(@NotNull Action action, @NotNull ItemFunction<T> function) {
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

    public void performAction(@NotNull Action action, @NotNull T holder) {
        if (this.isPerformingBlockingFunction()) {
            return;
        }

        List<ItemFunction<T>> functions = controls.get(action);

        if (functions == null || functions.isEmpty()) {
            return;
        }

        for (ItemFunction<T> function : functions) {
            if (function.isAvailable()) {
                function.perform(holder);
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
