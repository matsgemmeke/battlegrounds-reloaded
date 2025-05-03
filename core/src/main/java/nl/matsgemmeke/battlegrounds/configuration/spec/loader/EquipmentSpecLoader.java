package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.ControlsSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ParticleEffectSpec;
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class EquipmentSpecLoader {

    private static final List<String> ALLOWED_DAMAGE_TYPE_VALUES = List.of("bullet-damage", "explosive-damage", "fire-damage");

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
    private static final String DESTROY_ON_ACTIVATE_ROUTE = "deploy.on-destroy.activate";
    private static final String DESTROY_ON_REMOVE_ROUTE = "deploy.on-destroy.remove";
    private static final String DESTROY_ON_RESET_ROUTE = "deploy.on-destroy.reset";
    private static final String RESISTANCES_ROUTE = "deploy.resistances";
    private static final String DESTROY_PARTICLE_EFFECT_ROUTE = "deploy.on-destroy.particle-effect";

    private static final String THROW_SOUNDS_ROUTE = "deploy.throwing.throw-sounds";
    private static final String THROW_VELOCITY_ROUTE = "deploy.throwing.velocity";
    private static final String THROW_COOLDOWN_ROUTE = "deploy.throwing.cooldown";

    private static final String COOK_SOUNDS_ROUTE = "deploy.throwing.cook-sounds";

    private static final String PLACE_MATERIAL_ROUTE = "deploy.placing.material";
    private static final String PLACE_SOUNDS_ROUTE = "deploy.placing.place-sounds";
    private static final String PLACE_COOLDOWN_ROUTE = "deploy.placing.cooldown";

    private static final String ACTIVATION_DELAY_ROUTE = "deploy.manual-activation.activation-delay";
    private static final String ACTIVATION_SOUNDS_ROUTE = "deploy.manual-activation.activation-sounds";

    private static final String EFFECT_ROUTE = "effect";

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
    private final YamlReader yamlReader;

    public EquipmentSpecLoader(
            @NotNull YamlReader yamlReader,
            @NotNull ItemEffectSpecLoader itemEffectSpecLoader,
            @NotNull ParticleEffectSpecLoader particleEffectSpecLoader
    ) {
        this.yamlReader = yamlReader;
        this.itemEffectSpecLoader = itemEffectSpecLoader;
        this.particleEffectSpecLoader = particleEffectSpecLoader;
    }

    @NotNull
    public EquipmentSpec loadSpec() {
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
        Boolean destroyOnActivate = new FieldSpecResolver<Boolean>()
                .route(DESTROY_ON_ACTIVATE_ROUTE)
                .value(yamlReader.getOptionalBoolean(DESTROY_ON_ACTIVATE_ROUTE).orElse(false))
                .resolve();
        Boolean destroyOnRemove = new FieldSpecResolver<Boolean>()
                .route(DESTROY_ON_REMOVE_ROUTE)
                .value(yamlReader.getOptionalBoolean(DESTROY_ON_REMOVE_ROUTE).orElse(false))
                .resolve();
        Boolean destroyOnReset = new FieldSpecResolver<Boolean>()
                .route(DESTROY_ON_RESET_ROUTE)
                .value(yamlReader.getOptionalBoolean(DESTROY_ON_RESET_ROUTE).orElse(false))
                .resolve();
        Map<String, Double> resistances = new FieldSpecResolver<Map<String, Double>>()
                .route(RESISTANCES_ROUTE)
                .value(resistancesValue)
                .validate(new MapOneOfValidator<>(ALLOWED_DAMAGE_TYPE_VALUES))
                .resolve();

        ParticleEffectSpec destroyEffect = null;

        if (yamlReader.contains(DESTROY_PARTICLE_EFFECT_ROUTE)) {
            destroyEffect = particleEffectSpecLoader.loadSpec(DESTROY_PARTICLE_EFFECT_ROUTE);
        }

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
        ThrowPropertiesSpec throwPropertiesSpec = new ThrowPropertiesSpec(throwSounds, throwVelocity, throwCooldown);

        String cookSounds = new FieldSpecResolver<String>()
                .route(COOK_SOUNDS_ROUTE)
                .value(yamlReader.getString(COOK_SOUNDS_ROUTE))
                .resolve();
        CookPropertiesSpec cookPropertiesSpec = new CookPropertiesSpec(cookSounds);

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
        PlacePropertiesSpec placePropertiesSpec = new PlacePropertiesSpec(placeMaterial, placeSounds, placeCooldown);

        ManualActivationSpec manualActivationSpec = null;

        if (yamlReader.contains("deploy.manual-activation")) {
            Long activationDelay = new FieldSpecResolver<Long>()
                    .route(ACTIVATION_DELAY_ROUTE)
                    .value(yamlReader.getOptionalLong(ACTIVATION_DELAY_ROUTE).orElse(null))
                    .validate(new RequiredValidator<>())
                    .resolve();
            String activationSounds = new FieldSpecResolver<String>()
                    .route(ACTIVATION_SOUNDS_ROUTE)
                    .value(yamlReader.getString(ACTIVATION_SOUNDS_ROUTE))
                    .resolve();

            manualActivationSpec = new ManualActivationSpec(activationDelay, activationSounds);
        }

        DeploymentSpec deploymentSpec = new DeploymentSpec(health, destroyOnActivate, destroyOnRemove, destroyOnReset, destroyEffect, resistances, throwPropertiesSpec, cookPropertiesSpec, placePropertiesSpec, manualActivationSpec);
        ItemEffectSpec effectSpec = itemEffectSpecLoader.loadSpec(EFFECT_ROUTE);

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

        return new EquipmentSpec(name, description, displayItemSpec, activatorItemSpec, throwItemSpec, controlsSpec, deploymentSpec, effectSpec);
    }

    @NotNull
    private Map<String, Double> getResistances() {
        return yamlReader.getStringRouteMappedValues(RESISTANCES_ROUTE, false).entrySet().stream()
                .map(entry -> new SimpleEntry<>(entry.getKey(), ((Number) entry.getValue()).doubleValue()))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
}
