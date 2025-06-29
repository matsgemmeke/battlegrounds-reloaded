package nl.matsgemmeke.battlegrounds.item.representation;

import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ItemRepresentation {

    @NotNull
    private final ItemTemplate itemTemplate;
    @NotNull
    private final Map<Placeholder, String> placeholders;

    public ItemRepresentation(@NotNull ItemTemplate itemTemplate) {
        this.itemTemplate = itemTemplate;
        this.placeholders = new HashMap<>();
    }

    public void setPlaceholder(@NotNull Placeholder placeholder, @NotNull String value) {
        placeholders.put(placeholder, value);
    }

    @NotNull
    public ItemStack update() {
        Map<String, Object> placeholderValues = placeholders.entrySet().stream()
                .map(placeholder -> new SimpleEntry<>(placeholder.getKey().getKey(), placeholder.getValue()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        
        return itemTemplate.createItemStack(placeholderValues);
    }
}
