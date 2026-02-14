package nl.matsgemmeke.battlegrounds.item.melee;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.melee.MeleeWeaponSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.melee.ThrowingSpec;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.PersistentDataEntry;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.melee.controls.MeleeWeaponControlsFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.representation.Placeholder;
import nl.matsgemmeke.battlegrounds.item.throwing.ThrowHandler;
import nl.matsgemmeke.battlegrounds.item.throwing.ThrowHandlerFactory;
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
    private final ReloadSystemFactory reloadSystemFactory;
    private final ThrowHandlerFactory throwHandlerFactory;

    @Inject
    public MeleeWeaponFactory(
            MeleeWeaponControlsFactory controlsFactory,
            MeleeWeaponRegistry meleeWeaponRegistry,
            NamespacedKeyCreator namespacedKeyCreator,
            ReloadSystemFactory reloadSystemFactory,
            ThrowHandlerFactory throwHandlerFactory
    ) {
        this.controlsFactory = controlsFactory;
        this.meleeWeaponRegistry = meleeWeaponRegistry;
        this.namespacedKeyCreator = namespacedKeyCreator;
        this.reloadSystemFactory = reloadSystemFactory;
        this.throwHandlerFactory = throwHandlerFactory;
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
        DefaultMeleeWeapon meleeWeapon = new DefaultMeleeWeapon();
        meleeWeapon.setName(spec.name);
        meleeWeapon.setDescription(spec.description);
        meleeWeapon.setAttackDamage(spec.damage.meleeDamage);

        ItemTemplate displayItemTemplate = this.createDisplayItemTemplate(spec.items.displayItem);
        meleeWeapon.setDisplayItemTemplate(displayItemTemplate);

        ItemRepresentation itemRepresentation = new ItemRepresentation(displayItemTemplate);
        itemRepresentation.setPlaceholder(Placeholder.ITEM_NAME, spec.name);

        int loadedAmount = spec.ammo.loadedAmount;
        int maxLoadedAmount = spec.ammo.maxLoadedAmount;
        int defaultReserveAmount = spec.ammo.defaultReserveAmount;
        int maxReserveAmount = spec.ammo.maxReserveAmount;

        ResourceContainer resourceContainer = new ResourceContainer(maxLoadedAmount, loadedAmount, defaultReserveAmount, maxReserveAmount);
        meleeWeapon.setResourceContainer(resourceContainer);

        ItemControls<MeleeWeaponHolder> controls;

        if (spec.controls != null) {
            controls = controlsFactory.create(spec.controls, meleeWeapon);
        } else {
            controls = new ItemControls<>();
        }

        meleeWeapon.setControls(controls);

        if (spec.reloading != null) {
            ReloadSystem reloadSystem = reloadSystemFactory.create(spec.reloading, resourceContainer);
            meleeWeapon.setReloadSystem(reloadSystem);
        }

        if (spec.throwing != null) {
            ThrowHandler throwHandler = throwHandlerFactory.create(spec.throwing, itemRepresentation, resourceContainer);
            meleeWeapon.configureThrowHandler(throwHandler);
        }

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
