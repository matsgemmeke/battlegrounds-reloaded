package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.ShootingSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.ShootingSpecLoader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.*;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.ControlsSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.OneOfValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredValidator;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GunSpecLoader {

    private static final List<String> ALLOWED_ACTION_VALUES = List.of("CHANGE_FROM", "CHANGE_TO", "DROP_ITEM", "LEFT_CLICK", "PICKUP_ITEM", "RIGHT_CLICK", "SWAP_FROM", "SWAP_TO");
    private static final List<String> ALLOWED_RELOAD_TYPE_VALUES = List.of("MAGAZINE", "MANUAL_INSERTION");

    private static final String ID_ROUTE = "id";
    private static final String NAME_ROUTE = "name";
    private static final String DESCRIPTION_ROUTE = "description";

    private static final String MAGAZINE_SIZE_ROUTE = "ammo.magazine-size";
    private static final String MAX_MAGAZINE_AMOUNT_ROUTE = "ammo.max-magazine-amount";
    private static final String DEFAULT_MAGAZINE_AMOUNT_ROUTE = "ammo.default-supply";

    private static final String RANGE_PROFILE_ROUTE = "shooting.range";
    private static final String HEADSHOT_DAMAGE_MULTIPLIER_ROUTE = "shooting.headshot-damage-multiplier";

    private static final String SHOOTING_SECTION_ROUTE = "shooting";

    private static final String RELOAD_TYPE_ROUTE = "reloading.type";
    private static final String RELOAD_SOUNDS_ROUTE = "reloading.reload-sounds";
    private static final String RELOAD_DURATION_ROUTE = "reloading.duration";

    private static final String ITEM_MATERIAL_ROUTE = "item.material";
    private static final String ITEM_DISPLAY_NAME_ROUTE = "item.display-name";
    private static final String ITEM_DAMAGE_ROUTE = "item.damage";

    private static final String RELOAD_ACTION_ROUTE = "controls.reload";
    private static final String SHOOT_ACTION_ROUTE = "controls.shoot";
    private static final String USE_SCOPE_ACTION_ROUTE = "controls.scope-use";
    private static final String STOP_SCOPE_ACTION_ROUTE = "controls.scope-stop";
    private static final String CHANGE_SCOPE_MAGNIFICATION_ACTION_ROUTE = "controls.scope-change-magnification";
    
    private static final String SCOPE_MAGNIFICATIONS_ROUTE = "scope.magnifications";
    private static final String SCOPE_USE_SOUNDS_ROUTE = "scope.use-sounds";
    private static final String SCOPE_STOP_SOUNDS_ROUTE = "scope.stop-sounds";
    private static final String SCOPE_CHANGE_MAGNIFICATION_SOUNDS_ROUTE = "scope.change-magnification-sounds";

    @NotNull
    private final RangeProfileSpecLoader rangeProfileSpecLoader;
    @NotNull
    private final ShootingSpecLoader shootingSpecLoader;
    @NotNull
    private final YamlReader yamlReader;

    public GunSpecLoader(@NotNull YamlReader yamlReader, @NotNull RangeProfileSpecLoader rangeProfileSpecLoader, @NotNull ShootingSpecLoader shootingSpecLoader) {
        this.yamlReader = yamlReader;
        this.rangeProfileSpecLoader = rangeProfileSpecLoader;
        this.shootingSpecLoader = shootingSpecLoader;
    }

    @NotNull
    public GunSpec loadSpec() {
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

        int magazineSize = new FieldSpecResolver<Integer>()
                .route(MAGAZINE_SIZE_ROUTE)
                .value(yamlReader.getOptionalInt(MAGAZINE_SIZE_ROUTE).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        int maxMagazineAmount = new FieldSpecResolver<Integer>()
                .route(MAX_MAGAZINE_AMOUNT_ROUTE)
                .value(yamlReader.getOptionalInt(MAX_MAGAZINE_AMOUNT_ROUTE).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        int defaultMagazineAmount = new FieldSpecResolver<Integer>()
                .route(DEFAULT_MAGAZINE_AMOUNT_ROUTE)
                .value(yamlReader.getOptionalInt(DEFAULT_MAGAZINE_AMOUNT_ROUTE).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();

        RangeProfileSpec rangeProfileSpec = rangeProfileSpecLoader.loadSpec(RANGE_PROFILE_ROUTE);

        double headshotDamageMultiplier = new FieldSpecResolver<Double>()
                .route(HEADSHOT_DAMAGE_MULTIPLIER_ROUTE)
                .value(yamlReader.getDouble(HEADSHOT_DAMAGE_MULTIPLIER_ROUTE))
                .validate(new RequiredValidator<>())
                .resolve();

        ShootingSpec shootingSpec = shootingSpecLoader.loadSpec(SHOOTING_SECTION_ROUTE);

        String reloadType = new FieldSpecResolver<String>()
                .route(RELOAD_TYPE_ROUTE)
                .value(yamlReader.getString(RELOAD_TYPE_ROUTE))
                .validate(new RequiredValidator<>())
                .validate(new OneOfValidator<>(ALLOWED_RELOAD_TYPE_VALUES))
                .resolve();
        String reloadSounds = new FieldSpecResolver<String>()
                .route(RELOAD_SOUNDS_ROUTE)
                .value(yamlReader.getString(RELOAD_SOUNDS_ROUTE))
                .resolve();
        long reloadDuration = new FieldSpecResolver<Long>()
                .route(RELOAD_DURATION_ROUTE)
                .value(yamlReader.getOptionalLong(RELOAD_DURATION_ROUTE).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        ReloadSpec reloadSpec = new ReloadSpec(reloadType, reloadSounds, reloadDuration);

        String itemMaterial = new FieldSpecResolver<String>()
                .route(ITEM_MATERIAL_ROUTE)
                .value(yamlReader.getString(ITEM_MATERIAL_ROUTE))
                .validate(new RequiredValidator<>())
                .validate(new EnumValidator<>(Material.class))
                .resolve();
        String itemDisplayName = new FieldSpecResolver<String>()
                .route(ITEM_DISPLAY_NAME_ROUTE)
                .value(yamlReader.getString(ITEM_DISPLAY_NAME_ROUTE))
                .validate(new RequiredValidator<>())
                .resolve();
        int itemDamage = new FieldSpecResolver<Integer>()
                .route(ITEM_DAMAGE_ROUTE)
                .value(yamlReader.getOptionalInt(ITEM_DAMAGE_ROUTE).orElse(null))
                .validate(new RequiredValidator<>())
                .resolve();
        ItemStackSpec itemSpec = new ItemStackSpec(itemMaterial, itemDisplayName, itemDamage);

        String reloadAction = new FieldSpecResolver<String>()
                .route(RELOAD_ACTION_ROUTE)
                .value(yamlReader.getString(RELOAD_ACTION_ROUTE))
                .validate(new RequiredValidator<>())
                .validate(new OneOfValidator<>(ALLOWED_ACTION_VALUES))
                .resolve();
        String shootAction = new FieldSpecResolver<String>()
                .route(SHOOT_ACTION_ROUTE)
                .value(yamlReader.getString(SHOOT_ACTION_ROUTE))
                .validate(new RequiredValidator<>())
                .validate(new OneOfValidator<>(ALLOWED_ACTION_VALUES))
                .resolve();
        String useScopeAction = new FieldSpecResolver<String>()
                .route(USE_SCOPE_ACTION_ROUTE)
                .value(yamlReader.getString(USE_SCOPE_ACTION_ROUTE))
                .validate(new OneOfValidator<>(ALLOWED_ACTION_VALUES))
                .resolve();
        String stopScopeAction = new FieldSpecResolver<String>()
                .route(STOP_SCOPE_ACTION_ROUTE)
                .value(yamlReader.getString(STOP_SCOPE_ACTION_ROUTE))
                .validate(new OneOfValidator<>(ALLOWED_ACTION_VALUES))
                .resolve();
        String changeScopeMagnificationAction = new FieldSpecResolver<String>()
                .route(CHANGE_SCOPE_MAGNIFICATION_ACTION_ROUTE)
                .value(yamlReader.getString(CHANGE_SCOPE_MAGNIFICATION_ACTION_ROUTE))
                .validate(new OneOfValidator<>(ALLOWED_ACTION_VALUES))
                .resolve();
        ControlsSpec controlsSpec = new ControlsSpec(reloadAction, shootAction, useScopeAction, stopScopeAction, changeScopeMagnificationAction);

        ScopeSpec scopeSpec = null;
        
        if (yamlReader.contains("scope")) {
            List<Float> magnifications = new FieldSpecResolver<List<Float>>()
                    .route(SCOPE_MAGNIFICATIONS_ROUTE)
                    .value(yamlReader.getOptionalFloatList(SCOPE_MAGNIFICATIONS_ROUTE).orElse(null))
                    .validate(new RequiredValidator<>())
                    .resolve();
            String useSounds = new FieldSpecResolver<String>()
                    .route(SCOPE_USE_SOUNDS_ROUTE)
                    .value(yamlReader.getString(SCOPE_USE_SOUNDS_ROUTE))
                    .resolve();
            String stopSounds = new FieldSpecResolver<String>()
                    .route(SCOPE_STOP_SOUNDS_ROUTE)
                    .value(yamlReader.getString(SCOPE_STOP_SOUNDS_ROUTE))
                    .resolve();
            String changeMagnficationSounds = new FieldSpecResolver<String>()
                    .route(SCOPE_CHANGE_MAGNIFICATION_SOUNDS_ROUTE)
                    .value(yamlReader.getString(SCOPE_CHANGE_MAGNIFICATION_SOUNDS_ROUTE))
                    .resolve();

            scopeSpec = new ScopeSpec(magnifications, useSounds, stopSounds, changeMagnficationSounds);
        }

        return new GunSpec(id, name, description, magazineSize, maxMagazineAmount, defaultMagazineAmount, rangeProfileSpec, headshotDamageMultiplier, shootingSpec, reloadSpec, itemSpec, controlsSpec, scopeSpec);
    }
}
