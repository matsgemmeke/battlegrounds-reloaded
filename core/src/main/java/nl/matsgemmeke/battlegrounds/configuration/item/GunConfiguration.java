package nl.matsgemmeke.battlegrounds.configuration.item;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.BasePluginConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.item.spec.FireModeSpecification;
import nl.matsgemmeke.battlegrounds.configuration.item.spec.GunSpecification;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

public class GunConfiguration extends BasePluginConfiguration {

    public GunConfiguration(@NotNull File file, @Nullable InputStream resource) {
        super(file, resource, true);
    }

    @NotNull
    public GunSpecification createSpec() throws InvalidItemConfigurationException {
        Section root = this.getRoot();

        String name = this.getRequiredValue("name", String.class);
        String description = this.getRequiredValue("description", String.class);

        int magazineSize = this.getRequiredValue("ammo.magazine-size", Integer.class);
        int maxMagazineAmount = this.getRequiredValue("ammo.max-magazine-amount", Integer.class);
        int defaultMagazineAmount = this.getRequiredValue("ammo.default-supply", Integer.class);

        double shortRangeDamage = this.getRequiredValue("shooting.range.short-range.damage", Double.class);
        double shortRangeDistance = this.getRequiredValue("shooting.range.short-range.distance", Double.class);
        double mediumRangeDamage = this.getRequiredValue("shooting.range.medium-range.damage", Double.class);
        double mediumRangeDistance = this.getRequiredValue("shooting.range.medium-range.distance", Double.class);
        double longRangeDamage = this.getRequiredValue("shooting.range.long-range.damage", Double.class);
        double longRangeDistance = this.getRequiredValue("shooting.range.long-range.distance", Double.class);
        double headshotDamageMultiplier = this.getRequiredValue("shooting.headshot-damage-multiplier", Double.class);

        String shotSounds = root.getOptionalString("shooting.shot-sound").orElse(null);

        String fireModeType = this.getRequiredValue("shooting.fire-mode.type", String.class);
        Map<String, Object> fireModeProperties = root.getSection("shooting.fire-mode").getStringRouteMappedValues(true);
        FireModeSpecification fireModeSpecification = new FireModeSpecification(fireModeType, fireModeProperties);

        return new GunSpecification(name, description, magazineSize, maxMagazineAmount, defaultMagazineAmount, shortRangeDamage, shortRangeDistance, mediumRangeDamage, mediumRangeDistance, longRangeDamage, longRangeDistance, headshotDamageMultiplier, shotSounds, fireModeSpecification);
    }

    @NotNull
    private <T> T getRequiredValue(@NotNull String route, @NotNull Class<T> type) {
        return this.getRoot().getAsOptional(route, type)
                .orElseThrow(() -> {
                    String id = this.getString("id");
                    return new InvalidItemConfigurationException("Gun configuration error for %s: Missing required '%s' value".formatted(id, route));
                });
    }
}
