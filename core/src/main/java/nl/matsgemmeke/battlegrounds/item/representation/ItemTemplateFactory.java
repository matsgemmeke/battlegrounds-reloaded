package nl.matsgemmeke.battlegrounds.item.representation;

import com.google.inject.Inject;
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

    private static final String ACTION_EXECUTOR_ID_KEY = "action-executor-id";
    private static final String TEMPLATE_ID_KEY = "template-id";

    private final NamespacedKeyCreator namespacedKeyCreator;

    @Inject
    public ItemTemplateFactory(NamespacedKeyCreator namespacedKeyCreator) {
        this.namespacedKeyCreator = namespacedKeyCreator;
    }

    public ItemTemplate create(ItemSpec spec, String actionExecutorIdValue) {
        NamespacedKey templateKey = namespacedKeyCreator.create(TEMPLATE_ID_KEY);
        UUID templateId = UUID.randomUUID();
        Material material = Material.valueOf(spec.material);
        TextTemplate displayNameTemplate = new TextTemplate(spec.displayName);
        int damage = spec.damage;
        boolean unbreakable = spec.unbreakable;

        Set<ItemFlag> itemFlags = spec.itemFlags.stream()
                .map(ItemFlag::valueOf)
                .collect(Collectors.toSet());

        NamespacedKey actionExecutorIdKey = namespacedKeyCreator.create(ACTION_EXECUTOR_ID_KEY);
        PersistentDataEntry<String, String> actionExecutorIdDataEntry = new PersistentDataEntry<>(actionExecutorIdKey, PersistentDataType.STRING, actionExecutorIdValue);

        return ItemTemplate.builder(templateKey, templateId, material)
                .displayNameTemplate(displayNameTemplate)
                .damage(damage)
                .dataEntries(Set.of(actionExecutorIdDataEntry))
                .itemFlags(itemFlags)
                .unbreakable(unbreakable)
                .build();
    }
}
