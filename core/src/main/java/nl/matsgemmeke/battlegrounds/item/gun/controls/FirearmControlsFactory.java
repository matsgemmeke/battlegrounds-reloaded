package nl.matsgemmeke.battlegrounds.item.gun.controls;

import nl.matsgemmeke.battlegrounds.configuration.spec.gun.ControlsSpec;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.Firearm;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import nl.matsgemmeke.battlegrounds.item.gun.controls.reload.ReloadFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.ChangeScopeMagnificationFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.StopScopeFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.UseScopeFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.shoot.ShootFunction;
import org.jetbrains.annotations.NotNull;

public class FirearmControlsFactory {

    @NotNull
    public ItemControls<GunHolder> create(@NotNull ControlsSpec spec, @NotNull Firearm firearm) {
        ItemControls<GunHolder> controls = new ItemControls<>();

        String useScopeActionValue = spec.useScopeAction();
        String stopScopeActionValue = spec.stopScopeAction();
        String changeScopeMagnificationActionValue = spec.changeScopeMagnificationAction();

        if (useScopeActionValue != null && stopScopeActionValue != null) {
            if (changeScopeMagnificationActionValue != null) {
                Action changeScopeMagnificationAction = Action.valueOf(changeScopeMagnificationActionValue);
                ChangeScopeMagnificationFunction changeScopeMagnificationFunction = new ChangeScopeMagnificationFunction(firearm);

                controls.addControl(changeScopeMagnificationAction, changeScopeMagnificationFunction);
            }

            Action useScopeAction = Action.valueOf(useScopeActionValue);
            Action stopScopeAction = Action.valueOf(stopScopeActionValue);

            UseScopeFunction useScopeFunction = new UseScopeFunction(firearm);
            StopScopeFunction stopScopeFunction = new StopScopeFunction(firearm);

            controls.addControl(useScopeAction, useScopeFunction);
            controls.addControl(stopScopeAction, stopScopeFunction);
        }

        // Should be safe to directly get an enum since the specification is already validated
        Action reloadAction = Action.valueOf(spec.reloadAction());
        Action shootAction = Action.valueOf(spec.shootAction());

        ReloadFunction reloadFunction = new ReloadFunction(firearm);
        ShootFunction shootFunction = new ShootFunction(firearm);

        controls.addControl(reloadAction, reloadFunction);
        controls.addControl(shootAction, shootFunction);

        return controls;
    }
}
