package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.Matchable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Activator extends Matchable {

    boolean isReady();

    void prepare(@NotNull ItemHolder holder, @NotNull Map<String, Object> values);
}
