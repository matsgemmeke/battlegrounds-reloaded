package nl.matsgemmeke.battlegrounds.item.gun.controls;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.controls.ControlSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ControlsSpec;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBinding;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBindingMapper;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import nl.matsgemmeke.battlegrounds.item.gun.controls.reload.ReloadFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.ChangeScopeMagnificationFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.StopScopeFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.UseScopeFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.shoot.ShootFunction;

import java.util.function.Supplier;

public class GunControllerFactory {

    private final ActionBindingMapper actionBindingMapper;
    private final Supplier<ItemController<GunUser>> controllerSupplier;

    @Inject
    public GunControllerFactory(ActionBindingMapper actionBindingMapper, Supplier<ItemController<GunUser>> controllerSupplier) {
        this.actionBindingMapper = actionBindingMapper;
        this.controllerSupplier = controllerSupplier;
    }

    public ItemController<GunUser> create(ControlsSpec spec, Gun gun) {
        ItemController<GunUser> controller = controllerSupplier.get();

        ControlSpec useScopeSpec = spec.scopeUse;
        ControlSpec stopScopeSpec = spec.scopeStop;
        ControlSpec changeScopeMagnificationSpec = spec.scopeChangeMagnification;

        if (useScopeSpec != null && stopScopeSpec != null) {
            if (changeScopeMagnificationSpec != null) {
                Action changeScopeMagnificationAction = Action.valueOf(changeScopeMagnificationSpec.action);
                ChangeScopeMagnificationFunction changeScopeMagnificationFunction = new ChangeScopeMagnificationFunction(gun);
                ActionBinding<GunUser> changeScopeMagnificationBinding = actionBindingMapper.toBinding(changeScopeMagnificationSpec, changeScopeMagnificationFunction);

                controller.bind(changeScopeMagnificationAction, changeScopeMagnificationBinding);
            }

            Action useScopeAction = Action.valueOf(useScopeSpec.action);
            Action stopScopeAction = Action.valueOf(stopScopeSpec.action);

            UseScopeFunction useScopeFunction = new UseScopeFunction(gun);
            StopScopeFunction stopScopeFunction = new StopScopeFunction(gun);

            ActionBinding<GunUser> useScopeBinding = actionBindingMapper.toBinding(useScopeSpec, useScopeFunction);
            ActionBinding<GunUser> stopScopeBinding = actionBindingMapper.toBinding(stopScopeSpec, stopScopeFunction);

            controller.bind(useScopeAction, useScopeBinding);
            controller.bind(stopScopeAction, stopScopeBinding);
        }

        // Should be safe to directly get an enum since the specification is already validated
        Action reloadAction = Action.valueOf(spec.reload.action);
        Action shootAction = Action.valueOf(spec.shoot.action);

        ReloadFunction reloadFunction = new ReloadFunction(gun);
        ShootFunction shootFunction = new ShootFunction(gun);

        ActionBinding<GunUser> reloadBinding = actionBindingMapper.toBinding(spec.reload, reloadFunction);
        ActionBinding<GunUser> shootBinding = actionBindingMapper.toBinding(spec.shoot, shootFunction);

        controller.bind(reloadAction, reloadBinding);
        controller.bind(shootAction, shootBinding);

        return controller;
    }
}
