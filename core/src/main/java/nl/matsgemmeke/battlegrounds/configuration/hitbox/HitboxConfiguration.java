package nl.matsgemmeke.battlegrounds.configuration.hitbox;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.BasePluginConfiguration;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;

public class HitboxConfiguration extends BasePluginConfiguration {

    private static final boolean READ_ONLY = true;
    private static final String HITBOX_SECTION_ROOT = "hitbox";

    public HitboxConfiguration(File file, InputStream resource) {
        super(file, resource, READ_ONLY);
    }

    public Optional<VerticalHitbox> getVerticalHitbox(String entity, String position) {
        String sectionRoute = this.createSectionRoute(entity, position);
        Section section = this.getOptionalSection(sectionRoute).orElse(null);

        if (section == null) {
            return Optional.empty();
        }

        String bodyHeightRoute = this.createPropertyRoute(entity, position, "height-body");
        String headHeightRoute = this.createPropertyRoute(entity, position, "height-head");
        String legsHeightRoute = this.createPropertyRoute(entity, position, "height-legs");
        String widthRoute = this.createPropertyRoute(entity, position, "width");

        Double bodyHeight = this.getOptionalDouble(bodyHeightRoute).orElse(0.0);
        Double headHeight = this.getOptionalDouble(headHeightRoute).orElse(0.0);
        Double legsHeight = this.getOptionalDouble(legsHeightRoute).orElse(0.0);
        Double width = this.getOptionalDouble(widthRoute).orElse(0.0);

        return Optional.of(new VerticalHitbox(bodyHeight, headHeight, legsHeight, width));
    }

    private String createSectionRoute(String entity, String position) {
        return HITBOX_SECTION_ROOT + "-" + entity + "." + position;
    }

    private String createPropertyRoute(String entity, String position, String property) {
        return HITBOX_SECTION_ROOT + "-" + entity + "." + position + "." + property;
    }
}
