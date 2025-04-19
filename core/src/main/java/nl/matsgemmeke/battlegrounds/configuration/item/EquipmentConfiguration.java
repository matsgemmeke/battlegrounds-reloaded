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

        return new EquipmentSpec(name, description, itemSpec, controlsSpec);
    }
}
