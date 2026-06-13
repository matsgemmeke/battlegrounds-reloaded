package nl.matsgemmeke.battlegrounds.entity;

import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class EntityKeyTest {

    @ParameterizedTest
    @CsvSource({ "PLAYER,minecraft:player", "IRON_GOLEM,minecraft:iron_golem" })
    @DisplayName("fromEntityType returns new instance for minecraft entity with the given entity type")
    void fromEntityType(EntityType entityType, String expected) {
        EntityKey entityKey = EntityKey.fromEntityType(entityType);

        assertThat(entityKey.getValue()).isEqualTo(expected);
    }

    @Test
    @DisplayName("custom returns new instance for custom entity")
    void custom() {
        EntityKey entityKey = EntityKey.custom("turret");

        assertThat(entityKey.getValue()).isEqualTo("battlegrounds:turret");
    }
}
