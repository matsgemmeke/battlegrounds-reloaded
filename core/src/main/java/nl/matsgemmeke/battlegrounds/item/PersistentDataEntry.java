package nl.matsgemmeke.battlegrounds.item;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public record PersistentDataEntry<T, Z>(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
}
