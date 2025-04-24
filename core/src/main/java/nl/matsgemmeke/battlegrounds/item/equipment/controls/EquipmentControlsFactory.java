package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.ControlsSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.CookPropertiesSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.DeploymentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.ThrowPropertiesSpec;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.deploy.place.PlaceDeployment;
import nl.matsgemmeke.battlegrounds.item.deploy.place.PlaceDeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.deploy.prime.PrimeDeployment;
import nl.matsgemmeke.battlegrounds.item.deploy.throwing.ThrowDeployment;
import nl.matsgemmeke.battlegrounds.item.deploy.throwing.ThrowDeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.activate.ActivateFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.cook.CookFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.place.PlaceFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.throwing.ThrowFunction;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class EquipmentControlsFactory {

    @NotNull
    private final GameContextProvider contextProvider;

    @Inject
    public EquipmentControlsFactory(@NotNull GameContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @NotNull
    public ItemControls<EquipmentHolder> create(@NotNull ControlsSpec controlsSpec, @NotNull DeploymentSpec deploymentSpec, @NotNull Equipment equipment, @NotNull GameKey gameKey) {
        ItemControls<EquipmentHolder> controls = new ItemControls<>();

        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);

        String throwActionValue = controlsSpec.throwAction();
        String cookActionValue = controlsSpec.cookAction();
        String placeActionValue = controlsSpec.placeAction();
        String activateActionValue = controlsSpec.activateAction();

        if (throwActionValue != null) {
            if (cookActionValue != null) {
                Action cookAction = Action.valueOf(cookActionValue);
                CookPropertiesSpec cookProperties = deploymentSpec.cookPropertiesSpec();

                // The specification files should already be validated, so this acts as a double check
                if (cookProperties == null) {
                    throw new EquipmentControlsCreationException("Cannot create controls for 'cook', the equipment specification does not contain the required properties");
                }

                List<GameSound> cookSounds = DefaultGameSound.parseSounds(cookProperties.cookSounds());
                PrimeDeployment deployment = new PrimeDeployment(audioEmitter, cookSounds);
                CookFunction cookFunction = new CookFunction(equipment, deployment);

                controls.addControl(cookAction, cookFunction);
            }

            Action throwAction = Action.valueOf(throwActionValue);
            ItemTemplate itemTemplate = equipment.getThrowItemTemplate();
            ThrowPropertiesSpec throwProperties = deploymentSpec.throwPropertiesSpec();

            // The specification files should already be validated, so this acts as a double check
            if (itemTemplate == null) {
                throw new EquipmentControlsCreationException("Cannot create controls for 'throw', the equipment specification does not contain the required throw item template");
            }

            if (throwProperties == null) {
                throw new EquipmentControlsCreationException("Cannot create controls for 'throw', the equipment specification does not contain the required throw properties");
            }

            List<GameSound> throwSounds = DefaultGameSound.parseSounds(deploymentSpec.throwPropertiesSpec().throwSounds());
            List<ProjectileEffect> projectileEffects = List.of();
            Map<DamageType, Double> resistances = this.getResistances(deploymentSpec.resistances());
            double health = deploymentSpec.health();
            double velocity = deploymentSpec.throwPropertiesSpec().velocity();
            long cooldown = deploymentSpec.throwPropertiesSpec().cooldown();

            ThrowDeploymentProperties deploymentProperties = new ThrowDeploymentProperties(itemTemplate, throwSounds, projectileEffects, resistances, health, velocity, cooldown);
            ThrowDeployment deployment = new ThrowDeployment(deploymentProperties, audioEmitter);
            ThrowFunction throwFunction = new ThrowFunction(equipment, deployment);

            controls.addControl(throwAction, throwFunction);
        }

        if (placeActionValue != null) {
            Action placeAction = Action.valueOf(placeActionValue);

            List<GameSound> placeSounds = DefaultGameSound.parseSounds(deploymentSpec.placePropertiesSpec().placeSounds());
            Map<DamageType, Double> resistances = this.getResistances(deploymentSpec.resistances());
            Material material = Material.valueOf(deploymentSpec.placePropertiesSpec().material());
            double health = deploymentSpec.health();
            long cooldown = deploymentSpec.placePropertiesSpec().cooldown();

            PlaceDeploymentProperties deploymentProperties = new PlaceDeploymentProperties(placeSounds, resistances, material, health, cooldown);
            PlaceDeployment deployment = new PlaceDeployment(deploymentProperties, audioEmitter);
            PlaceFunction placeFunction = new PlaceFunction(equipment, deployment);

            controls.addControl(placeAction, placeFunction);
        }

        if (activateActionValue != null) {
            Action activateAction = Action.valueOf(activateActionValue);
            ActivateFunction activateFunction = new ActivateFunction(equipment);

            controls.addControl(activateAction, activateFunction);
        }

        return controls;
    }

    @NotNull
    private Map<DamageType, Double> getResistances(@NotNull Map<String, Double> resistancesValue) {
        return resistancesValue.entrySet().stream()
                .map(this::convertResistanceValueToEntry)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (x, y) -> y, HashMap::new));
    }

    @NotNull
    private Entry<DamageType, Double> convertResistanceValueToEntry(@NotNull Entry<String, Double> entry) {
        String damageTypeValue = entry.getKey().replaceAll("-", "_").toUpperCase();
        DamageType damageType = DamageType.valueOf(damageTypeValue);

        return new SimpleEntry<>(damageType, entry.getValue());
    }
}
