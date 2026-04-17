package nl.matsgemmeke.battlegrounds.item.melee.controls;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.controls.ControlSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.melee.ControlsSpec;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBinding;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBindingMapper;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;
import nl.matsgemmeke.battlegrounds.item.melee.controls.reload.ReloadFunction;
import nl.matsgemmeke.battlegrounds.item.melee.controls.throwing.ThrowFunction;

import java.util.function.Supplier;

public class MeleeWeaponControlsFactory {

    private final ActionBindingMapper actionBindingMapper;
    private final Supplier<ItemControls<MeleeWeaponUser>> controlsSupplier;

    @Inject
    public MeleeWeaponControlsFactory(ActionBindingMapper actionBindingMapper, Supplier<ItemControls<MeleeWeaponUser>> controlsSupplier) {
        this.actionBindingMapper = actionBindingMapper;
        this.controlsSupplier = controlsSupplier;
    }

    public ItemControls<MeleeWeaponUser> create(ControlsSpec spec, MeleeWeapon meleeWeapon) {
        ItemControls<MeleeWeaponUser> controls = controlsSupplier.get();

        ControlSpec reloadControlSpec = spec.reload;
        ControlSpec throwingControlSpec = spec.throwing;

        if (reloadControlSpec != null) {
            Action reloadAction = Action.valueOf(reloadControlSpec.action);
            ReloadFunction reloadFunction = new ReloadFunction(meleeWeapon);
            ActionBinding<MeleeWeaponUser> reloadBinding = actionBindingMapper.toBinding(reloadControlSpec, reloadFunction);

            controls.bind(reloadAction, reloadBinding);
        }

        if (throwingControlSpec != null) {
            Action throwAction = Action.valueOf(throwingControlSpec.action);
            ThrowFunction throwFunction = new ThrowFunction(meleeWeapon);
            ActionBinding<MeleeWeaponUser> throwBinding = actionBindingMapper.toBinding(throwingControlSpec, throwFunction);

            controls.bind(throwAction, throwBinding);
        }

        return controls;
    }
}
