package nl.matsgemmeke.battlegrounds.item.melee;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.melee.MeleeWeaponSpec;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.PersistentDataEntry;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.melee.controls.MeleeWeaponControlsFactory;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class MeleeWeaponFactory {

    private static final String ACTION_EXECUTOR_ID_KEY = "action-executor-id";
    private static final String ACTION_EXECUTOR_ID_VALUE = "melee-weapon";
    private static final String TEMPLATE_ID_KEY = "template-id";

    private final MeleeWeaponControlsFactory controlsFactory;
    private final MeleeWeaponRegistry meleeWeaponRegistry;
    private final NamespacedKeyCreator namespacedKeyCreator;

    @Inject
    public MeleeWeaponFactory(MeleeWeaponControlsFactory controlsFactory, MeleeWeaponRegistry meleeWeaponRegistry, NamespacedKeyCreator namespacedKeyCreator) {
        this.controlsFactory = controlsFactory;
        this.meleeWeaponRegistry = meleeWeaponRegistry;
        this.namespacedKeyCreator = namespacedKeyCreator;
    }

    public MeleeWeapon create(MeleeWeaponSpec spec) {
        MeleeWeapon meleeWeapon = this.createInstance(spec);

        meleeWeaponRegistry.register(meleeWeapon);

        return meleeWeapon;
    }

    public MeleeWeapon create(MeleeWeaponSpec spec, MeleeWeaponHolder holder) {
        MeleeWeapon meleeWeapon = this.createInstance(spec);
        meleeWeapon.setHolder(holder);

        meleeWeaponRegistry.register(meleeWeapon, holder);

        return meleeWeapon;
    }

    private MeleeWeapon createInstance(MeleeWeaponSpec spec) {
        DefaultMeleeWeapon meleeWeapon = new DefaultMeleeWeapon(new ItemControls<>());
        meleeWeapon.setName(spec.name);
        meleeWeapon.setDescription(spec.description);
        meleeWeapon.setAttackDamage(spec.damage.meleeDamage);

        ItemTemplate displayItemTemplate = this.createDisplayItemTemplate(spec.items.displayItem);
        meleeWeapon.setDisplayItemTemplate(displayItemTemplate);
        meleeWeapon.update();

        return meleeWeapon;
    }

    private ItemTemplate createDisplayItemTemplate(ItemSpec spec) {
        NamespacedKey templateKey = namespacedKeyCreator.create(TEMPLATE_ID_KEY);
        UUID templateId = UUID.randomUUID();
        Material material = Material.valueOf(spec.material);
        String displayName = spec.displayName;
        int damage = spec.damage;

        NamespacedKey actionExecutorIdKey = namespacedKeyCreator.create(ACTION_EXECUTOR_ID_KEY);
        PersistentDataEntry<String, String> actionExecutorIdDataEntry = new PersistentDataEntry<>(actionExecutorIdKey, PersistentDataType.STRING, ACTION_EXECUTOR_ID_VALUE);

        ItemTemplate itemTemplate = new ItemTemplate(templateKey, templateId, material);
        itemTemplate.addPersistentDataEntry(actionExecutorIdDataEntry);
        itemTemplate.setDamage(damage);
        itemTemplate.setDisplayNameTemplate(new TextTemplate(displayName));
        return itemTemplate;
    }
}
