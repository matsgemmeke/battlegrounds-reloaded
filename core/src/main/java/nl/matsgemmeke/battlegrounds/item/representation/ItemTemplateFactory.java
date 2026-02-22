package nl.matsgemmeke.battlegrounds.item.representation;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.DataSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.PersistentDataEntry;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemTemplateFactory {

    private static final String TEMPLATE_ID_KEY = "template-id";

    private final NamespacedKeyCreator namespacedKeyCreator;

    @Inject
    public ItemTemplateFactory(NamespacedKeyCreator namespacedKeyCreator) {
        this.namespacedKeyCreator = namespacedKeyCreator;
    }

    public ItemTemplate create(ItemSpec spec) {
        NamespacedKey templateKey = namespacedKeyCreator.create(TEMPLATE_ID_KEY);
        UUID templateId = UUID.randomUUID();
        Material material = Material.valueOf(spec.material);
        TextTemplate displayNameTemplate = new TextTemplate(spec.displayName);
        int damage = spec.damage;
        boolean unbreakable = spec.unbreakable;

        Set<ItemFlag> itemFlags = spec.itemFlags.stream()
                .map(ItemFlag::valueOf)
                .collect(Collectors.toUnmodifiableSet());

        Set<PersistentDataEntry<?, ?>> dataEntries = spec.data.values().stream()
                .map(this::createPersistentDataEntry)
                .collect(Collectors.toUnmodifiableSet());

        return ItemTemplate.builder(templateKey, templateId, material)
                .displayNameTemplate(displayNameTemplate)
                .damage(damage)
                .itemFlags(itemFlags)
                .dataEntries(dataEntries)
                .unbreakable(unbreakable)
                .build();
    }

    private PersistentDataEntry<?, ?> createPersistentDataEntry(DataSpec spec) {
        NamespacedKey namespacedKey = namespacedKeyCreator.create(spec.key);

        return switch (spec.type) {
            case "INTEGER" -> {
                int value = this.parseInteger(spec.value);
                yield new PersistentDataEntry<>(namespacedKey, PersistentDataType.INTEGER, value);
            }
            case "STRING" -> new PersistentDataEntry<>(namespacedKey, PersistentDataType.STRING, spec.value);
            default -> throw new ItemTemplateDefinitionException("Unsupported data type: " + spec.type);
        };
    }

    private int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ItemTemplateDefinitionException("Data entry was defined as an integer, but the value \"%s\" is invalid".formatted(value), e);
        }
    }
}
