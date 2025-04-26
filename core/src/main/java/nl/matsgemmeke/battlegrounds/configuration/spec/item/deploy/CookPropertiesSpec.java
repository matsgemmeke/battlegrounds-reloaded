package nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy;

import org.jetbrains.annotations.Nullable;

/**
 * Represents the immutable, validated configuration for cook properties loaded from a YAML file.
 *
 * @param cookSounds the raw value that defines the sounds produced when cooking the item
 */
public record CookPropertiesSpec(@Nullable String cookSounds) { }
