package nl.matsgemmeke.battlegrounds.item.gun.controls;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.Firearm;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import nl.matsgemmeke.battlegrounds.item.gun.controls.reload.ReloadFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.ChangeScopeMagnificationFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.StopScopeFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.scope.UseScopeFunction;
import nl.matsgemmeke.battlegrounds.item.gun.controls.shoot.ShootFunction;
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FirearmControlsFactory {

    @NotNull
    public ItemControls<GunHolder> create(@NotNull Section section, @NotNull Firearm firearm, @NotNull GameKey gameKey) {
        ItemControls<GunHolder> controls = new ItemControls<>();

        Section controlsSection = section.getSection("controls");
        String reloadActionValue = controlsSection.getString("reload");
        String changeScopeMagnificationActionValue = controlsSection.getString("scope-change-magnification");
        String stopScopeActionValue = controlsSection.getString("scope-stop");
        String useScopeActionValue = controlsSection.getString("scope-use");
        String shootActionValue = controlsSection.getString("shoot");

        if (useScopeActionValue != null && stopScopeActionValue != null) {
            if (changeScopeMagnificationActionValue != null) {
                Action changeScopeMagnificationAction = this.getActionFromConfiguration(firearm, "scope-change-magnification", changeScopeMagnificationActionValue);
                ChangeScopeMagnificationFunction changeScopeMagnificationFunction = new ChangeScopeMagnificationFunction(firearm);

                controls.addControl(changeScopeMagnificationAction, changeScopeMagnificationFunction);
            }

            Action useScopeAction = this.getActionFromConfiguration(firearm, "scope-use", useScopeActionValue);
            Action stopScopeAction = this.getActionFromConfiguration(firearm, "scope-stop", stopScopeActionValue);

            UseScopeFunction useScopeFunction = new UseScopeFunction(firearm);
            StopScopeFunction stopScopeFunction = new StopScopeFunction(firearm);

            controls.addControl(useScopeAction, useScopeFunction);
            controls.addControl(stopScopeAction, stopScopeFunction);
        }

        if (reloadActionValue != null) {
            Action reloadAction = this.getActionFromConfiguration(firearm, "reload", reloadActionValue);
            ReloadFunction reloadFunction = new ReloadFunction(firearm);

            controls.addControl(reloadAction, reloadFunction);
        }

        if (shootActionValue != null) {
            Action shootAction = this.getActionFromConfiguration(firearm, "shoot", shootActionValue);
            ShootFunction shootFunction = new ShootFunction(firearm);

            controls.addControl(shootAction, shootFunction);
        }

        return controls;
    }

    @NotNull
    private Action getActionFromConfiguration(@NotNull Firearm firearm, @NotNull String functionName, @NotNull String value) {
        try {
            return Action.valueOf(value);
        } catch (IllegalArgumentException e) {
            TextTemplate textTemplate = new TextTemplate("Error while creating controls for equipment %equipment_name%: " +
                    "the value \"%action_value%\" for function \"%function_name%\" is not a valid action type!");
            Map<String, Object> values = Map.of(
                    "equipment_name", firearm.getName(),
                    "action_value", value,
                    "function_name", functionName
            );
            String message = textTemplate.replace(values);

            throw new FirearmControlsCreationException(message);
        }
    }
}
