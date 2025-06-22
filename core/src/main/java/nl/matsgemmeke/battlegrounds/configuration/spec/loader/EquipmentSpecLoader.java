package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpecLoader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.ControlsSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.*;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ItemStackSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.MapOneOfValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredIfFieldExistsValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredValidator;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class EquipmentSpecLoader {

    private static final List<String> ALLOWED_DAMAGE_TYPE_VALUES = List.of("bullet-damage", "explosive-damage", "fire-damage");

    private static final String ID_ROUTE = "id";
    private static final String NAME_ROUTE = "name";
    private static final String DESCRIPTION_ROUTE = "description";

    private static final String DISPLAY_ITEM_MATERIAL_ROUTE = "item.display.material";
    private static final String DISPLAY_ITEM_DISPLAY_NAME_ROUTE = "item.display.display-name";
    private static final String DISPLAY_ITEM_DAMAGE_ROUTE = "item.display.damage";

    private static final String THROW_ACTION_ROUTE = "controls.throw";
    private static final String COOK_ACTION_ROUTE = "controls.cook";
    private static final String PLACE_ACTION_ROUTE = "controls.place";
    private static final String ACTIVATE_ACTION_ROUTE = "controls.activate";

    private static final String HEALTH_ROUTE = "deploy.health";
    private static final String ACTIVATE_EFFECT_ON_DESTRUCTION_ROUTE = "deploy.on-destruction.activate-effect";
    private static final String REMOVE_DEPLOYMENT_ON_DESTRUCTION_ROUTE = "deploy.on-destruction.remove-deployment";
    private static final String UNDO_EFFECT_ON_DESTRUCTION_ROUTE = "deploy.on-destruction.undo-effect";
    private static final String REMOVE_DEPLOYMENT_ON_CLEANUP_ROUTE = "deploy.on-cleanup.remove-deployment";
    private static final String RESISTANCES_ROUTE = "deploy.resistances";
    private static final String DESTRUCTION_PARTICLE_EFFECT_ROUTE = "deploy.on-destruction.particle-effect";

    private static final String THROW_PROPERTIES_ROUTE = "deploy.throwing";
    private static final String THROW_SOUNDS_ROUTE = "deploy.throwing.throw-sounds";
    private static final String THROW_VELOCITY_ROUTE = "deploy.throwing.velocity";
    private static final String THROW_COOLDOWN_ROUTE = "deploy.throwing.cooldown";

    private static final String COOK_SOUNDS_ROUTE = "deploy.throwing.cook-sounds";

    private static final String PLACE_PROPERTIES_ROUTE = "deploy.placing";
    private static final String PLACE_MATERIAL_ROUTE = "deploy.placing.material";
    private static final String PLACE_SOUNDS_ROUTE = "deploy.placing.place-sounds";
    private static final String PLACE_COOLDOWN_ROUTE = "deploy.placing.cooldown";

    private static final String MANUAL_ACTIVATION_PROPERTIES_ROUTE = "deploy.manual-activation";
    private static final String MANUAL_ACTIVATION_DELAY_ROUTE = "deploy.manual-activation.delay";
    private static final String MANUAL_ACTIVATION_SOUNDS_ROUTE = "deploy.manual-activation.sounds";

    private static final String EFFECT_ROUTE = "effect";
    private static final String PROJECTILE_EFFECTS_ROUTE = "projectile.effects";

    private static final String ACTIVATOR_ITEM_MATERIAL_ROUTE = "item.activator.material";
    private static final String ACTIVATOR_ITEM_DISPLAY_NAME_ROUTE = "item.activator.display-name";
    private static final String ACTIVATOR_ITEM_DAMAGE_ROUTE = "item.activator.damage";

    private static final String THROW_ITEM_MATERIAL_ROUTE = "item.throw.material";
    private static final String THROW_ITEM_DISPLAY_NAME_ROUTE = "item.throw.display-name";
    private static final String THROW_ITEM_DAMAGE_ROUTE = "item.throw.damage";

    @NotNull
    private final ItemEffectSpecLoader itemEffectSpecLoader;
    @NotNull
    private final ParticleEffectSpecLoader particleEffectSpecLoader;
    @NotNull
    private final ProjectileEffectSpecLoader projectileEffectSpecLoader;
    @NotNull
    private final YamlReader yamlReader;

    public EquipmentSpecLoader(
            @NotNull YamlReader yamlReader,
            @NotNull ItemEffectSpecLoader itemEffectSpecLoader,
            @NotNull ParticleEffectSpecLoader particleEffectSpecLoader,
            @NotNull ProjectileEffectSpecLoader projectileEffectSpecLoader
    ) {
        this.yamlReader = yamlReader;
        this.itemEffectSpecLoader = itemEffectSpecLoader;
        this.particleEffectSpecLoader = particleEffectSpecLoader;
        this.projectileEffectSpecLoader = projectileEffectSpecLoader;
    }

    @NotNull
    public EquipmentSpec loadSpec() {
        String id = new FieldSpecResolver<String>()
                .route(ID_ROUTE)
                .value(yamlReader.getString(ID_ROUTE))
                .validate(new RequiredValidator<>())
                .resolve();
        String name = new FieldSpecResolver<String>()
                .route(NAME_ROUTE)
                .value(yamlReader.getString(NAME_ROUTE))
                .validate(new RequiredValidator<>())
                .resolve();
        String description = new FieldSpecResolver<String>()
                .route(DESCRIPTION_ROUTE)
                .value(yamlReader.getString(DESCRIPTION_ROUTE))
                .resolve();

        String displayItemMaterial = new FieldSpecResolver<String>()
                .route(DISPLAY_ITEM_MATERIAL_ROUTE)
                .value(yamlReader.getString(DISPLAY_ITEM_MATERIAL_ROUTE))
                .validate(new RequiredValidator<>())
                .validate(new EnumValidator<>(Material.class))
                .resolve();
        String displayItemDisplayName = new FieldSpecResolver<String>()
                .route(DISPLAY_ITEM_DISPLAY_NAME_ROUTE)
                .value(yamlReader.getString(DISPLAY_ITEM_DISPLAY_NAME_ROUTE))
                .validate(new RequiredValidator<>())
                .resolve();
        int displayItemDamage = new FieldSpecResolver<Integer>()
                .route(DISPLAY_ITEM_DAMAGE_ROUTE)
                .value(yamlReader.getOptionalInt(DISPLAY_ITEM_DAMAGE_ROUTE).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        ItemStackSpec displayItemSpec = new ItemStackSpec(displayItemMaterial, displayItemDisplayName, displayItemDamage);

        String throwAction = new FieldSpecResolver<String>()
                .route(THROW_ACTION_ROUTE)
                .value(yamlReader.getString(THROW_ACTION_ROUTE))
                .resolve();
        String cookAction = new FieldSpecResolver<String>()
                .route(COOK_ACTION_ROUTE)
                .value(yamlReader.getString(COOK_ACTION_ROUTE))
                .resolve();
        String placeAction = new FieldSpecResolver<String>()
                .route(PLACE_ACTION_ROUTE)
                .value(yamlReader.getString(PLACE_ACTION_ROUTE))
                .resolve();
        String activateAction = new FieldSpecResolver<String>()
                .route(ACTIVATE_ACTION_ROUTE)
                .value(yamlReader.getString(ACTIVATE_ACTION_ROUTE))
                .resolve();
        ControlsSpec controlsSpec = new ControlsSpec(throwAction, cookAction, placeAction, activateAction);

        Map<String, Double> resistancesValue = this.getResistances();

        Double health = new FieldSpecResolver<Double>()
                .route(HEALTH_ROUTE)
                .value(yamlReader.getOptionalDouble(HEALTH_ROUTE).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        Boolean activateEffectOnDestruction = new FieldSpecResolver<Boolean>()
                .route(ACTIVATE_EFFECT_ON_DESTRUCTION_ROUTE)
                .value(yamlReader.getOptionalBoolean(ACTIVATE_EFFECT_ON_DESTRUCTION_ROUTE).orElse(false))
                .resolve();
        Boolean removeDeploymentOnDestruction = new FieldSpecResolver<Boolean>()
                .route(REMOVE_DEPLOYMENT_ON_DESTRUCTION_ROUTE)
                .value(yamlReader.getOptionalBoolean(REMOVE_DEPLOYMENT_ON_DESTRUCTION_ROUTE).orElse(false))
                .resolve();
        Boolean undoEffectOnDestruction = new FieldSpecResolver<Boolean>()
                .route(UNDO_EFFECT_ON_DESTRUCTION_ROUTE)
                .value(yamlReader.getOptionalBoolean(UNDO_EFFECT_ON_DESTRUCTION_ROUTE).orElse(false))
                .resolve();
        Boolean removeDeploymentOnCleanup = new FieldSpecResolver<Boolean>()
                .route(REMOVE_DEPLOYMENT_ON_CLEANUP_ROUTE)
                .value(yamlReader.getOptionalBoolean(REMOVE_DEPLOYMENT_ON_CLEANUP_ROUTE).orElse(false))
                .resolve();
        Map<String, Double> resistances = new FieldSpecResolver<Map<String, Double>>()
                .route(RESISTANCES_ROUTE)
                .value(resistancesValue)
                .validate(new MapOneOfValidator<>(ALLOWED_DAMAGE_TYPE_VALUES))
                .resolve();

        ParticleEffectSpec destructionParticleEffectSpec = null;

        if (yamlReader.contains(DESTRUCTION_PARTICLE_EFFECT_ROUTE)) {
            destructionParticleEffectSpec = particleEffectSpecLoader.loadSpec(DESTRUCTION_PARTICLE_EFFECT_ROUTE);
        }

        ThrowPropertiesSpec throwPropertiesSpec = null;

        if (yamlReader.contains(THROW_PROPERTIES_ROUTE)) {
            String throwSounds = new FieldSpecResolver<String>()
                    .route(THROW_SOUNDS_ROUTE)
                    .value(yamlReader.getString(THROW_SOUNDS_ROUTE))
                    .resolve();
            Double throwVelocity = new FieldSpecResolver<Double>()
                    .route(THROW_VELOCITY_ROUTE)
                    .value(yamlReader.getOptionalDouble(THROW_VELOCITY_ROUTE).orElse(null))
                    .validate(new RequiredIfFieldExistsValidator<>(THROW_ACTION_ROUTE, throwAction))
                    .resolve();
            Long throwCooldown = new FieldSpecResolver<Long>()
                    .route(THROW_COOLDOWN_ROUTE)
                    .value(yamlReader.getOptionalLong(THROW_COOLDOWN_ROUTE).orElse(null))
                    .validate(new RequiredIfFieldExistsValidator<>(THROW_ACTION_ROUTE, throwAction))
                    .resolve();

            throwPropertiesSpec = new ThrowPropertiesSpec(throwSounds, throwVelocity, throwCooldown);
        }

        String cookSounds = new FieldSpecResolver<String>()
                .route(COOK_SOUNDS_ROUTE)
                .value(yamlReader.getString(COOK_SOUNDS_ROUTE))
                .resolve();
        CookPropertiesSpec cookPropertiesSpec = new CookPropertiesSpec(cookSounds);

        PlacePropertiesSpec placePropertiesSpec = null;

        if (yamlReader.contains(PLACE_PROPERTIES_ROUTE)) {
            String placeMaterial = new FieldSpecResolver<String>()
                    .route(PLACE_MATERIAL_ROUTE)
                    .value(yamlReader.getString(PLACE_MATERIAL_ROUTE))
                    .validate(new RequiredIfFieldExistsValidator<>(PLACE_ACTION_ROUTE, placeAction))
                    .validate(new EnumValidator<>(Material.class))
                    .resolve();
            String placeSounds = new FieldSpecResolver<String>()
                    .route(PLACE_SOUNDS_ROUTE)
                    .value(yamlReader.getString(PLACE_SOUNDS_ROUTE))
                    .resolve();
            Long placeCooldown = new FieldSpecResolver<Long>()
                    .route(PLACE_COOLDOWN_ROUTE)
                    .value(yamlReader.getOptionalLong(PLACE_COOLDOWN_ROUTE).orElse(null))
                    .validate(new RequiredIfFieldExistsValidator<>(PLACE_ACTION_ROUTE, placeAction))
                    .resolve();

            placePropertiesSpec = new PlacePropertiesSpec(placeMaterial, placeSounds, placeCooldown);
        }

        ManualActivationSpec manualActivationSpec = null;

        if (yamlReader.contains(MANUAL_ACTIVATION_PROPERTIES_ROUTE)) {
            Long activationDelay = new FieldSpecResolver<Long>()
                    .route(MANUAL_ACTIVATION_DELAY_ROUTE)
                    .value(yamlReader.getOptionalLong(MANUAL_ACTIVATION_DELAY_ROUTE).orElse(null))
                    .validate(new RequiredValidator<>())
                    .resolve();
            String activationSounds = new FieldSpecResolver<String>()
                    .route(MANUAL_ACTIVATION_SOUNDS_ROUTE)
                    .value(yamlReader.getString(MANUAL_ACTIVATION_SOUNDS_ROUTE))
                    .resolve();

            manualActivationSpec = new ManualActivationSpec(activationDelay, activationSounds);
        }

        DeploymentSpec deploymentSpec = new DeploymentSpec(health, activateEffectOnDestruction, removeDeploymentOnDestruction, undoEffectOnDestruction, removeDeploymentOnCleanup, destructionParticleEffectSpec, resistances, throwPropertiesSpec, cookPropertiesSpec, placePropertiesSpec, manualActivationSpec);
        ItemEffectSpec effectSpec = itemEffectSpecLoader.loadSpec(EFFECT_ROUTE);
        List<ProjectileEffectSpec> projectileEffectSpecs = new ArrayList<>();

        for (String key : yamlReader.getRoutes(PROJECTILE_EFFECTS_ROUTE)) {
            String projectileEffectRoute = PROJECTILE_EFFECTS_ROUTE + "." + key;

            projectileEffectSpecs.add(projectileEffectSpecLoader.loadSpec(projectileEffectRoute));
        }

        ItemStackSpec activatorItemSpec = null;
        ItemStackSpec throwItemSpec = null;

        if (yamlReader.contains("item.activator")) {
            String activatorItemMaterial = new FieldSpecResolver<String>()
                    .route(ACTIVATOR_ITEM_MATERIAL_ROUTE)
                    .value(yamlReader.getString(ACTIVATOR_ITEM_MATERIAL_ROUTE))
                    .validate(new RequiredIfFieldExistsValidator<>(ACTIVATE_ACTION_ROUTE, activateAction))
                    .validate(new EnumValidator<>(Material.class))
                    .resolve();
            String activatorItemDisplayName = new FieldSpecResolver<String>()
                    .route(ACTIVATOR_ITEM_DISPLAY_NAME_ROUTE)
                    .value(yamlReader.getString(ACTIVATOR_ITEM_DISPLAY_NAME_ROUTE))
                    .validate(new RequiredIfFieldExistsValidator<>(ACTIVATE_ACTION_ROUTE, activateAction))
                    .resolve();
            int activatorItemDamage = new FieldSpecResolver<Integer>()
                    .route(ACTIVATOR_ITEM_DAMAGE_ROUTE)
                    .value(yamlReader.getOptionalInt(ACTIVATOR_ITEM_DAMAGE_ROUTE).orElse(null))
                    .validate(new RequiredIfFieldExistsValidator<>(ACTIVATE_ACTION_ROUTE, activateAction))
                    .resolve();

            activatorItemSpec = new ItemStackSpec(activatorItemMaterial, activatorItemDisplayName, activatorItemDamage);
        }

        if (yamlReader.contains("item.throw")) {
            String throwItemMaterial = new FieldSpecResolver<String>()
                    .route(THROW_ITEM_MATERIAL_ROUTE)
                    .value(yamlReader.getString(THROW_ITEM_MATERIAL_ROUTE))
                    .validate(new RequiredIfFieldExistsValidator<>(THROW_ACTION_ROUTE, throwAction))
                    .validate(new EnumValidator<>(Material.class))
                    .resolve();
            String throwItemDisplayName = new FieldSpecResolver<String>()
                    .route(THROW_ITEM_DISPLAY_NAME_ROUTE)
                    .value(yamlReader.getString(THROW_ITEM_DISPLAY_NAME_ROUTE))
                    .validate(new RequiredIfFieldExistsValidator<>(THROW_ACTION_ROUTE, throwAction))
                    .resolve();
            int throwItemDamage = new FieldSpecResolver<Integer>()
                    .route(THROW_ITEM_DAMAGE_ROUTE)
                    .value(yamlReader.getOptionalInt(THROW_ITEM_DAMAGE_ROUTE).orElse(null))
                    .validate(new RequiredIfFieldExistsValidator<>(THROW_ACTION_ROUTE, throwAction))
                    .resolve();

            throwItemSpec = new ItemStackSpec(throwItemMaterial, throwItemDisplayName, throwItemDamage);
        }

        return new EquipmentSpec(id, name, description, displayItemSpec, activatorItemSpec, throwItemSpec, controlsSpec, deploymentSpec, effectSpec, projectileEffectSpecs);
    }

    @NotNull
    private Map<String, Double> getResistances() {
        return yamlReader.getStringRouteMappedValues(RESISTANCES_ROUTE, false).entrySet().stream()
                .map(entry -> new SimpleEntry<>(entry.getKey(), ((Number) entry.getValue()).doubleValue()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
}
