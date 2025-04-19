package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpecResolver;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.ControlsSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ItemStackSpec;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredValidator;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class EquipmentConfiguration {

    private static final String NAME_ROUTE = "name";
    private static final String DESCRIPTION_ROUTE = "description";

    private static final String ITEM_MATERIAL_ROUTE = "item.material";
    private static final String ITEM_DISPLAY_NAME_ROUTE = "item.display-name";
    private static final String ITEM_DAMAGE_ROUTE = "item.damage";

    private static final String ACTIVATOR_ITEM_MATERIAL_ROUTE = "item.activator.material";
    private static final String ACTIVATOR_ITEM_DISPLAY_NAME_ROUTE = "item.activator.display-name";
    private static final String ACTIVATOR_ITEM_DAMAGE_ROUTE = "item.activator.damage";

    private static final String THROW_ITEM_MATERIAL_ROUTE = "item.throw-item.material";
    private static final String THROW_ITEM_DISPLAY_NAME_ROUTE = "item.throw-item.display-name";
    private static final String THROW_ITEM_DAMAGE_ROUTE = "item.throw-item.damage";

    private static final String THROW_ACTION_ROUTE = "controls.throw";
    private static final String COOK_ACTION_ROUTE = "controls.cook";
    private static final String PLACE_ACTION_ROUTE = "controls.place";
    private static final String ACTIVATE_ACTION_ROUTE = "controls.activate";

    @NotNull
    private final YamlReader yamlReader;

    public EquipmentConfiguration(@NotNull YamlReader yamlReader) {
        this.yamlReader = yamlReader;
    }

    @NotNull
    public EquipmentSpec createSpec() {
        String name = new FieldSpecResolver<String>()
                .route(NAME_ROUTE)
                .value(yamlReader.getString(NAME_ROUTE))
                .validate(new RequiredValidator<>())
                .resolve();
        String description = new FieldSpecResolver<String>()
                .route(DESCRIPTION_ROUTE)
                .value(yamlReader.getString(DESCRIPTION_ROUTE))
                .resolve();

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

        ItemStackSpec activatorItemSpec = null;
        ItemStackSpec throwItemSpec = null;

        if (yamlReader.contains("item.activator")) {
            String activatorItemMaterial = new FieldSpecResolver<String>()
                    .route(ACTIVATOR_ITEM_MATERIAL_ROUTE)
                    .value(yamlReader.getString(ACTIVATOR_ITEM_MATERIAL_ROUTE))
                    .validate(new RequiredValidator<>())
                    .validate(new EnumValidator<>(Material.class))
                    .resolve();
            String activatorItemDisplayName = new FieldSpecResolver<String>()
                    .route(ACTIVATOR_ITEM_DISPLAY_NAME_ROUTE)
                    .value(yamlReader.getString(ACTIVATOR_ITEM_DISPLAY_NAME_ROUTE))
                    .validate(new RequiredValidator<>())
                    .resolve();
            int activatorItemDamage = new FieldSpecResolver<Integer>()
                    .route(ACTIVATOR_ITEM_DAMAGE_ROUTE)
                    .value(yamlReader.getOptionalInt(ACTIVATOR_ITEM_DAMAGE_ROUTE).orElse(null))
                    .validate(new RequiredValidator<>())
                    .resolve();

            activatorItemSpec = new ItemStackSpec(activatorItemMaterial, activatorItemDisplayName, activatorItemDamage);
        }

        if (yamlReader.contains("item.throw-item")) {
            String throwItemMaterial = new FieldSpecResolver<String>()
                    .route(THROW_ITEM_MATERIAL_ROUTE)
                    .value(yamlReader.getString(THROW_ITEM_MATERIAL_ROUTE))
                    .validate(new RequiredValidator<>())
                    .validate(new EnumValidator<>(Material.class))
                    .resolve();
            String throwItemDisplayName = new FieldSpecResolver<String>()
                    .route(THROW_ITEM_DISPLAY_NAME_ROUTE)
                    .value(yamlReader.getString(THROW_ITEM_DISPLAY_NAME_ROUTE))
                    .validate(new RequiredValidator<>())
                    .resolve();
            int throwItemDamage = new FieldSpecResolver<Integer>()
                    .route(THROW_ITEM_DAMAGE_ROUTE)
                    .value(yamlReader.getOptionalInt(THROW_ITEM_DAMAGE_ROUTE).orElse(null))
                    .validate(new RequiredValidator<>())
                    .resolve();

            throwItemSpec = new ItemStackSpec(throwItemMaterial, throwItemDisplayName, throwItemDamage);
        }

        return new EquipmentSpec(name, description, itemSpec, activatorItemSpec, throwItemSpec, controlsSpec);
    }
}
