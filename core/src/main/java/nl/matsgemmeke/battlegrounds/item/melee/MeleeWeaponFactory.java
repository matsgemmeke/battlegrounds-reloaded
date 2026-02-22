package nl.matsgemmeke.battlegrounds.item.melee;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.melee.MeleeWeaponSpec;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.melee.controls.MeleeWeaponControlsFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.representation.ItemTemplateFactory;
import nl.matsgemmeke.battlegrounds.item.representation.Placeholder;
import nl.matsgemmeke.battlegrounds.item.throwing.ThrowHandler;
import nl.matsgemmeke.battlegrounds.item.throwing.ThrowHandlerFactory;

public class MeleeWeaponFactory {

    private final ItemTemplateFactory itemTemplateFactory;
    private final MeleeWeaponControlsFactory controlsFactory;
    private final MeleeWeaponRegistry meleeWeaponRegistry;
    private final ReloadSystemFactory reloadSystemFactory;
    private final ThrowHandlerFactory throwHandlerFactory;

    @Inject
    public MeleeWeaponFactory(
            ItemTemplateFactory itemTemplateFactory,
            MeleeWeaponControlsFactory controlsFactory,
            MeleeWeaponRegistry meleeWeaponRegistry,
            ReloadSystemFactory reloadSystemFactory,
            ThrowHandlerFactory throwHandlerFactory
    ) {
        this.itemTemplateFactory = itemTemplateFactory;
        this.controlsFactory = controlsFactory;
        this.meleeWeaponRegistry = meleeWeaponRegistry;
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

        ItemTemplate displayItemTemplate = itemTemplateFactory.create(spec.items.displayItem);
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
}
