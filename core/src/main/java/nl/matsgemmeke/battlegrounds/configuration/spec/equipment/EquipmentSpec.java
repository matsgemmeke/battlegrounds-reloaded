package nl.matsgemmeke.battlegrounds.configuration.spec.equipment;

import nl.matsgemmeke.battlegrounds.configuration.spec.item.ItemStackSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.DeploymentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.ProjectileEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ItemEffectSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents the immutable, validated configuration for an equipment item loaded from a YAML file.
 * <p>
 * This record is constructed after the corresponding YAML file has been read and all values have been parsed and
 * validated. Instances of this record hold all the necessary configuration values which are guaranteed to be valid at
 * the time of instantiation.
 *
 * @param id                the equipment id
 * @param name              the display name of the equipment item
 * @param description       the description of the equipment item
 * @param displayItem       the item stack specification for the display item, held by the user
 * @param activatorItem     the item stack specification for the activator item
 * @param throwItem         the item stack specification for items used during throw deployments
 * @param controls          the equipment controls specification
 * @param deployment        the deployment specification
 * @param effect            the item effect specification
 * @param projectileEffects the list of projectile effect specifications
 */
public record EquipmentSpec(
        @NotNull String id,
        @NotNull String name,
        @Nullable String description,
        @NotNull ItemStackSpec displayItem,
        @Nullable ItemStackSpec activatorItem,
        @Nullable ItemStackSpec throwItem,
        @NotNull ControlsSpec controls,
        @NotNull DeploymentSpec deployment,
        @NotNull ItemEffectSpec effect,
        @NotNull List<ProjectileEffectSpec> projectileEffects
) { }
