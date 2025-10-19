package nl.matsgemmeke.battlegrounds.item.gun.controls;

import nl.matsgemmeke.battlegrounds.configuration.item.gun.ControlsSpec;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import nl.matsgemmeke.battlegrounds.item.gun.controls.reload.ReloadFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.ChangeScopeMagnificationFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.StopScopeFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.UseScopeFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.shoot.ShootFunction;

public class GunControlsFactory {

    public ItemControls<GunHolder> create(ControlsSpec spec, Gun gun) {
        ItemControls<GunHolder> controls = new ItemControls<>();

        String useScopeActionValue = spec.scopeUse;
        String stopScopeActionValue = spec.scopeStop;
        String scopeChangeMagnification = spec.scopeChangeMagnification;

        if (useScopeActionValue != null && stopScopeActionValue != null) {
            if (scopeChangeMagnification != null) {
                Action changeScopeMagnificationAction = Action.valueOf(scopeChangeMagnification);
                ChangeScopeMagnificationFunction changeScopeMagnificationFunction = new ChangeScopeMagnificationFunction(gun);

                controls.addControl(changeScopeMagnificationAction, changeScopeMagnificationFunction);
            }

            Action useScopeAction = Action.valueOf(useScopeActionValue);
            Action stopScopeAction = Action.valueOf(stopScopeActionValue);

            UseScopeFunction useScopeFunction = new UseScopeFunction(gun);
            StopScopeFunction stopScopeFunction = new StopScopeFunction(gun);

            controls.addControl(useScopeAction, useScopeFunction);
            controls.addControl(stopScopeAction, stopScopeFunction);
        }

        // Should be safe to directly get an enum since the specification is already validated
        Action reloadAction = Action.valueOf(spec.reload);
        Action shootAction = Action.valueOf(spec.shoot);

        ReloadFunction reloadFunction = new ReloadFunction(gun);
        ShootFunction shootFunction = new ShootFunction(gun);

        controls.addControl(reloadAction, reloadFunction);
        controls.addControl(shootAction, shootFunction);

        return controls;
    }
}
