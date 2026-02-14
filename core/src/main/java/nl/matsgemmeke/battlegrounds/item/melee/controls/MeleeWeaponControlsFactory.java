package nl.matsgemmeke.battlegrounds.item.melee.controls;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.melee.ControlsSpec;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponHolder;
import nl.matsgemmeke.battlegrounds.item.melee.controls.reload.ReloadFunction;
import nl.matsgemmeke.battlegrounds.item.melee.controls.throwing.ThrowFunction;

import java.util.function.Supplier;

public class MeleeWeaponControlsFactory {

    private final Supplier<ItemControls<MeleeWeaponHolder>> controlsSupplier;

    @Inject
    public MeleeWeaponControlsFactory(Supplier<ItemControls<MeleeWeaponHolder>> controlsSupplier) {
        this.controlsSupplier = controlsSupplier;
    }

    public ItemControls<MeleeWeaponHolder> create(ControlsSpec spec, MeleeWeapon meleeWeapon) {
        ItemControls<MeleeWeaponHolder> controls = controlsSupplier.get();

        String reloadActionValue = spec.reload;
        String throwingActionValue = spec.throwing;

        if (reloadActionValue != null) {
            Action reloadAction = Action.valueOf(reloadActionValue);
            ReloadFunction reloadFunction = new ReloadFunction(meleeWeapon);

            controls.addControl(reloadAction, reloadFunction);
        }

        if (throwingActionValue != null) {
            Action throwingAction = Action.valueOf(throwingActionValue);
            ThrowFunction throwFunction = new ThrowFunction(meleeWeapon);

            controls.addControl(throwingAction, throwFunction);
        }

        return controls;
    }
}
