package nl.matsgemmeke.battlegrounds.item.gun.controls;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.controls.ControlSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ControlsSpec;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBinding;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBindingMapper;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import nl.matsgemmeke.battlegrounds.item.gun.controls.reload.ReloadFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.ChangeScopeMagnificationFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.StopScopeFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.UseScopeFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.shoot.ShootFunction;

import java.util.function.Supplier;

public class GunControlsFactory {

    private final ActionBindingMapper actionBindingMapper;
    private final Supplier<ItemControls<GunUser>> controlsSupplier;

    @Inject
    public GunControlsFactory(ActionBindingMapper actionBindingMapper, Supplier<ItemControls<GunUser>> controlsSupplier) {
        this.actionBindingMapper = actionBindingMapper;
        this.controlsSupplier = controlsSupplier;
    }

    public ItemControls<GunUser> create(ControlsSpec spec, Gun gun) {
        ItemControls<GunUser> controls = controlsSupplier.get();

        ControlSpec useScopeSpec = spec.scopeUse;
        ControlSpec stopScopeSpec = spec.scopeStop;
        ControlSpec changeScopeMagnificationSpec = spec.scopeChangeMagnification;

        if (useScopeSpec != null && stopScopeSpec != null) {
            if (changeScopeMagnificationSpec != null) {
                Action changeScopeMagnificationAction = Action.valueOf(changeScopeMagnificationSpec.action);
                ChangeScopeMagnificationFunction changeScopeMagnificationFunction = new ChangeScopeMagnificationFunction(gun);
                ActionBinding<GunUser> changeScopeMagnificationBinding = actionBindingMapper.toBinding(changeScopeMagnificationSpec, changeScopeMagnificationFunction);

                controls.bind(changeScopeMagnificationAction, changeScopeMagnificationBinding);
            }

            Action useScopeAction = Action.valueOf(useScopeSpec.action);
            Action stopScopeAction = Action.valueOf(stopScopeSpec.action);

            UseScopeFunction useScopeFunction = new UseScopeFunction(gun);
            StopScopeFunction stopScopeFunction = new StopScopeFunction(gun);

            ActionBinding<GunUser> useScopeBinding = actionBindingMapper.toBinding(useScopeSpec, useScopeFunction);
            ActionBinding<GunUser> stopScopeBinding = actionBindingMapper.toBinding(stopScopeSpec, stopScopeFunction);

            controls.bind(useScopeAction, useScopeBinding);
            controls.bind(stopScopeAction, stopScopeBinding);
        }

        // Should be safe to directly get an enum since the specification is already validated
        Action reloadAction = Action.valueOf(spec.reload.action);
        Action shootAction = Action.valueOf(spec.shoot.action);

        ReloadFunction reloadFunction = new ReloadFunction(gun);
        ShootFunction shootFunction = new ShootFunction(gun);

        ActionBinding<GunUser> reloadBinding = actionBindingMapper.toBinding(spec.reload, reloadFunction);
        ActionBinding<GunUser> shootBinding = actionBindingMapper.toBinding(spec.shoot, shootFunction);

        controls.bind(reloadAction, reloadBinding);
        controls.bind(shootAction, shootBinding);

        return controls;
    }
}
