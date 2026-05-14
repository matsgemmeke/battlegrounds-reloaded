package nl.matsgemmeke.battlegrounds.item.melee.controls;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.controls.ControlSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.melee.ControlsSpec;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemControllerRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBinding;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBindingMapper;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;
import nl.matsgemmeke.battlegrounds.item.melee.controls.reload.ReloadFunction;
import nl.matsgemmeke.battlegrounds.item.melee.controls.throwing.ThrowFunction;

import java.util.function.Supplier;

public class MeleeWeaponControllerFactory {

    private final ActionBindingMapper actionBindingMapper;
    private final ItemControllerRegistry itemControllerRegistry;
    private final Supplier<ItemController<MeleeWeaponUser>> controllerSupplier;

    @Inject
    public MeleeWeaponControllerFactory(ActionBindingMapper actionBindingMapper, ItemControllerRegistry itemControllerRegistry, Supplier<ItemController<MeleeWeaponUser>> controllerSupplier) {
        this.actionBindingMapper = actionBindingMapper;
        this.itemControllerRegistry = itemControllerRegistry;
        this.controllerSupplier = controllerSupplier;
    }

    public ItemController<MeleeWeaponUser> create(ControlsSpec spec, MeleeWeapon meleeWeapon) {
        ItemController<MeleeWeaponUser> controller = controllerSupplier.get();

        ControlSpec reloadControlSpec = spec.reload;
        ControlSpec throwingControlSpec = spec.throwing;

        if (reloadControlSpec != null) {
            Action reloadAction = Action.valueOf(reloadControlSpec.action);
            ReloadFunction reloadFunction = new ReloadFunction(meleeWeapon);
            ActionBinding<MeleeWeaponUser> reloadBinding = actionBindingMapper.toBinding(reloadControlSpec, reloadFunction);

            controller.bind(reloadAction, reloadBinding);
        }

        if (throwingControlSpec != null) {
            Action throwAction = Action.valueOf(throwingControlSpec.action);
            ThrowFunction throwFunction = new ThrowFunction(meleeWeapon);
            ActionBinding<MeleeWeaponUser> throwBinding = actionBindingMapper.toBinding(throwingControlSpec, throwFunction);

            controller.bind(throwAction, throwBinding);
        }

        itemControllerRegistry.registerMeleeWeaponController(meleeWeapon.getId(), controller);

        return controller;
    }
}
