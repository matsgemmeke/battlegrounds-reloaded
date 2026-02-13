package nl.matsgemmeke.battlegrounds.item.representation;

import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ItemRepresentation {

    private static final int DEFAULT_ITEMSTACK_AMOUNT = 1;

    private final ItemTemplate itemTemplate;
    private final Map<Placeholder, String> placeholders;
    private int amount;

    public ItemRepresentation(ItemTemplate itemTemplate) {
        this.itemTemplate = itemTemplate;
        this.placeholders = new HashMap<>();
        this.amount = DEFAULT_ITEMSTACK_AMOUNT;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPlaceholder(Placeholder placeholder, String value) {
        placeholders.put(placeholder, value);
    }

    public ItemStack update() {
        Map<String, Object> placeholderValues = placeholders.entrySet().stream()
                .map(placeholder -> new SimpleEntry<>(placeholder.getKey().getKey(), placeholder.getValue()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        ItemStack itemStack = itemTemplate.createItemStack(placeholderValues);
        itemStack.setAmount(amount);
        return itemStack;
    }
}
