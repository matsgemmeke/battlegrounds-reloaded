package nl.matsgemmeke.battlegrounds.configuration.spec.equipment;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.ItemStackSpec;

public record EquipmentSpec(
        String name,
        String description,
        ItemStackSpec itemSpec,
        ItemStackSpec activatorItemSpec,
        ItemStackSpec throwItemSpec,
        ControlsSpec controlsSpec
) { }
