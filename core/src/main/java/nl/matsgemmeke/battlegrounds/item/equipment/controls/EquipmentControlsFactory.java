package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.PlacePropertiesSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.ThrowPropertiesSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.projectile.ProjectileEffectSpec;
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
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffectFactory;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class EquipmentControlsFactory {

    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final ProjectileEffectFactory projectileEffectFactory;
    @NotNull
    private final Provider<PrimeDeployment> primeDeploymentProvider;
    @NotNull
    private final Provider<ThrowDeployment> throwDeploymentProvider;

    @Inject
    public EquipmentControlsFactory(
            @NotNull GameContextProvider contextProvider,
            @NotNull ProjectileEffectFactory projectileEffectFactory,
            @NotNull Provider<PrimeDeployment> primeDeploymentProvider,
            @NotNull Provider<ThrowDeployment> throwDeploymentProvider
    ) {
        this.contextProvider = contextProvider;
        this.projectileEffectFactory = projectileEffectFactory;
        this.primeDeploymentProvider = primeDeploymentProvider;
        this.throwDeploymentProvider = throwDeploymentProvider;
    }

    @NotNull
    public ItemControls<EquipmentHolder> create(@NotNull EquipmentSpec spec, @NotNull Equipment equipment, @NotNull GameKey gameKey) {
        ItemControls<EquipmentHolder> controls = new ItemControls<>();

        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);

        String throwActionValue = spec.controls.throwing;
        String cookActionValue = spec.controls.cook;
        String placeActionValue = spec.controls.place;
        String activateActionValue = spec.controls.activate;

        if (throwActionValue != null) {
            ThrowPropertiesSpec throwProperties = spec.deploy.throwing;

            if (throwProperties == null) {
                throw new EquipmentControlsCreationException("Cannot create controls for 'throw', the equipment specification does not contain the required throw properties");
            }

            if (cookActionValue != null) {
                Action cookAction = Action.valueOf(cookActionValue);

                List<GameSound> cookSounds = DefaultGameSound.parseSounds(throwProperties.cookSounds);

                PrimeDeployment deployment = primeDeploymentProvider.get();
                deployment.configurePrimeSounds(cookSounds);

                CookFunction cookFunction = new CookFunction(equipment, deployment);

                controls.addControl(cookAction, cookFunction);
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
                projectileEffects.add(projectileEffectFactory.create(projectileEffectSpec, gameKey));
            }

            ThrowDeploymentProperties properties = new ThrowDeploymentProperties(itemTemplate, throwSounds, projectileEffects, resistances, health, velocity, cooldown);

            ThrowDeployment deployment = throwDeploymentProvider.get();
            deployment.configureProperties(properties);

            ThrowFunction throwFunction = new ThrowFunction(equipment, deployment);

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
