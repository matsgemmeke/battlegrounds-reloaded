package nl.matsgemmeke.battlegrounds.configuration.hitbox;

import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;

import java.util.Optional;

public class HitboxDefinitionResult {

    private final HitboxDefinition hitboxDefinition;
    private final String errorMessage;

    private HitboxDefinitionResult(HitboxDefinition hitboxDefinition, String errorMessage) {
        this.hitboxDefinition = hitboxDefinition;
        this.errorMessage = errorMessage;
    }

    public static HitboxDefinitionResult success(HitboxDefinition hitboxDefinition) {
        return new HitboxDefinitionResult(hitboxDefinition, null);
    }

    public static HitboxDefinitionResult notFound() {
        return new HitboxDefinitionResult(null, null);
    }

    public static HitboxDefinitionResult invalid(String errorMessage) {
        return new HitboxDefinitionResult(null, errorMessage);
    }

    public Optional<HitboxDefinition> getHitboxDefinition() {
        return Optional.ofNullable(hitboxDefinition);
    }

    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }
}
