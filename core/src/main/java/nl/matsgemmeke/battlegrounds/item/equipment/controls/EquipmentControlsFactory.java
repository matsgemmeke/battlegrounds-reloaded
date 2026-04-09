package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.CookingPropertiesSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.PlacePropertiesSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.ThrowPropertiesSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.deploy.DropPropertiesSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.effect.ProjectileEffectSpec;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.deploy.action.ThrowDeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.deploy.action.ThrowDeploymentAction;
import nl.matsgemmeke.battlegrounds.item.deploy.drop.DropDeploymentAction;
import nl.matsgemmeke.battlegrounds.item.deploy.place.PlaceDeploymentAction;
import nl.matsgemmeke.battlegrounds.item.deploy.place.PlaceDeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.deploy.prime.PrimeDeploymentAction;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.activate.ActivateFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.cook.CookFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.place.PlaceFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.throwing.ThrowFunction;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffectFactory;
import org.bukkit.Material;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EquipmentControlsFactory {

    private final ProjectileEffectFactory projectileEffectFactory;
    private final Provider<DropDeploymentAction> dropDeploymentActionProvider;
    private final Provider<PlaceDeploymentAction> placeDeploymentActionProvider;
    private final Provider<PrimeDeploymentAction> primeDeploymentActionProvider;
    private final Provider<ThrowDeploymentAction> throwDeploymentActionProvider;
    private final Supplier<ItemControls<EquipmentUser>> controlsSupplier;

    @Inject
    public EquipmentControlsFactory(
            ProjectileEffectFactory projectileEffectFactory,
            Provider<DropDeploymentAction> dropDeploymentActionProvider,
            Provider<PlaceDeploymentAction> placeDeploymentActionProvider,
            Provider<PrimeDeploymentAction> primeDeploymentActionProvider,
            Provider<ThrowDeploymentAction> throwDeploymentActionProvider,
            Supplier<ItemControls<EquipmentUser>> controlsSupplier
    ) {
        this.projectileEffectFactory = projectileEffectFactory;
        this.dropDeploymentActionProvider = dropDeploymentActionProvider;
        this.placeDeploymentActionProvider = placeDeploymentActionProvider;
        this.primeDeploymentActionProvider = primeDeploymentActionProvider;
        this.throwDeploymentActionProvider = throwDeploymentActionProvider;
        this.controlsSupplier = controlsSupplier;
    }

    public ItemControls<EquipmentUser> create(EquipmentSpec spec, Equipment equipment) {
        ItemControls<EquipmentUser> controls = controlsSupplier.get();

        String throwActionValue = spec.controls.throwing;
        String placeActionValue = spec.controls.place;
        String cookActionValue = spec.controls.cook;
        String dropActionValue = spec.controls.drop;
        String activateActionValue = spec.controls.activate;

        if (throwActionValue != null) {
            ThrowPropertiesSpec throwProperties = spec.deploy.throwing;

            if (throwProperties == null) {
                throw new EquipmentControlsCreationException("Cannot create controls for 'throw', the equipment specification does not contain the required throw properties");
            }

            Action throwAction = Action.valueOf(throwActionValue);
            ItemTemplate itemTemplate = equipment.getThrowItemTemplate();

            // The specification files should already be validated, so this acts as a double check
            if (itemTemplate == null) {
                throw new EquipmentControlsCreationException("Cannot create controls for 'throw', the equipment specification does not contain the required throw item template");
            }

            List<GameSound> throwSounds = DefaultGameSound.parseSounds(throwProperties.throwSounds);
            Map<DamageType, Double> resistances = this.getResistances(spec.deploy.resistances);
            double health = spec.deploy.health;
            double velocity = throwProperties.velocity;
            long cooldown = throwProperties.cooldown;

            List<ProjectileEffect> projectileEffects = new ArrayList<>();

            for (ProjectileEffectSpec projectileEffectSpec : spec.projectileEffects.values()) {
                projectileEffects.add(projectileEffectFactory.create(projectileEffectSpec));
            }

            ThrowDeploymentProperties properties = new ThrowDeploymentProperties(itemTemplate, throwSounds, projectileEffects, resistances, health, velocity, cooldown);

            ThrowDeploymentAction deploymentAction = throwDeploymentActionProvider.get();
            deploymentAction.configureProperties(properties);

            ThrowFunction throwFunction = new ThrowFunction(equipment, deploymentAction);

            controls.addControl(throwAction, throwFunction);
        }

        if (placeActionValue != null) {
            Action placeAction = Action.valueOf(placeActionValue);
            PlacePropertiesSpec placeProperties = spec.deploy.placing;

            if (placeProperties == null) {
                throw new EquipmentControlsCreationException("Cannot create controls for 'place', the equipment specification does not contain the required place properties");
            }

            List<GameSound> placeSounds = DefaultGameSound.parseSounds(placeProperties.placeSounds);
            Map<DamageType, Double> resistances = this.getResistances(spec.deploy.resistances);
            Material material = Material.valueOf(placeProperties.material);
            double health = spec.deploy.health;
            long cooldown = placeProperties.cooldown;

            PlaceDeploymentProperties properties = new PlaceDeploymentProperties(placeSounds, resistances, material, health, cooldown);

            PlaceDeploymentAction deploymentAction = placeDeploymentActionProvider.get();
            deploymentAction.configureProperties(properties);

            PlaceFunction placeFunction = new PlaceFunction(equipment, deploymentAction);

            controls.addControl(placeAction, placeFunction);
        }

        if (cookActionValue != null) {
            CookingPropertiesSpec cookProperties = spec.deploy.cooking;

            if (cookProperties == null) {
                throw new EquipmentControlsCreationException("Cannot create controls for 'cook', the equipment specification does not contain the required cook properties");
            }

            Action cookAction = Action.valueOf(cookActionValue);
            List<GameSound> cookSounds = DefaultGameSound.parseSounds(cookProperties.cookSounds);

            PrimeDeploymentAction deployment = primeDeploymentActionProvider.get();
            deployment.configurePrimeSounds(cookSounds);

            CookFunction cookFunction = new CookFunction(equipment, deployment);

            controls.addControl(cookAction, cookFunction);
        }

        if (dropActionValue != null) {
            DropPropertiesSpec dropProperties = spec.deploy.dropping;

            if (dropProperties == null) {
                throw new EquipmentControlsCreationException("Cannot create controls for 'drop', the equipment specification does not contain the required drop properties");
            }

            Action dropAction = Action.valueOf(dropActionValue);

            DropDeploymentAction deploymentAction = dropDeploymentActionProvider.get();
            DropFunction dropFunction = new DropFunction(equipment, deploymentAction);

            controls.addControl(dropAction, dropFunction);
        }

        if (activateActionValue != null) {
            Action activateAction = Action.valueOf(activateActionValue);
            ActivateFunction activateFunction = new ActivateFunction(equipment);

            controls.addControl(activateAction, activateFunction);
        }

        return controls;
    }

    private Map<DamageType, Double> getResistances(Map<String, Double> resistancesValue) {
        return resistancesValue.entrySet().stream()
                .map(this::convertResistanceValueToEntry)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (x, y) -> y, HashMap::new));
    }

    private Entry<DamageType, Double> convertResistanceValueToEntry(Entry<String, Double> entry) {
        String damageTypeValue = entry.getKey().replaceAll("-", "_").toUpperCase();
        DamageType damageType = DamageType.valueOf(damageTypeValue);

        return new SimpleEntry<>(damageType, entry.getValue());
    }
}
