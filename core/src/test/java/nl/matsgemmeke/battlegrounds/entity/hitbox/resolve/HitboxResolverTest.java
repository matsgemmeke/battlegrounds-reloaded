package nl.matsgemmeke.battlegrounds.entity.hitbox.resolve;

import nl.matsgemmeke.battlegrounds.configuration.hitbox.HitboxConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxComponentDefinition;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.impl.HumanoidHitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.impl.PlayerHitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.mapper.HitboxMapper;
import nl.matsgemmeke.battlegrounds.entity.hitbox.resolver.HitboxResolver;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HitboxResolverTest {

    @Mock
    private HitboxConfiguration hitboxConfiguration;
    @Spy
    private HitboxMapper hitboxMapper = new HitboxMapper();
    @InjectMocks
    private HitboxResolver hitboxResolver;

    @Test
    void resolveHitboxReturnsEmptyOptionalWhenGivenEntityTypeHasNoHitboxFactory() {
        Entity entity = mock(Entity.class);
        when(entity.getType()).thenReturn(EntityType.UNKNOWN);

        Optional<Hitbox> hitboxOptional = hitboxResolver.resolveHitbox(entity);

        assertThat(hitboxOptional).isEmpty();
    }

    static List<Arguments> playerHitboxDefinitions() {
        return List.of(arguments(createHitboxDefinition()));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("playerHitboxDefinitions")
    void resolveHitboxReturnsPlayerHitboxWithDefaultPositionHitboxWhenNoDefinitionIsNotFound(HitboxDefinition hitboxDefinition) {
        Player player = mock(Player.class);
        when(player.getType()).thenReturn(EntityType.PLAYER);

        when(hitboxConfiguration.getHitboxDefinition("player", "standing")).thenReturn(Optional.ofNullable(hitboxDefinition));

        Optional<Hitbox> hitboxOptional = hitboxResolver.resolveHitbox(player);

        assertThat(hitboxOptional).hasValueSatisfying(hitbox -> assertThat(hitbox).isInstanceOf(PlayerHitbox.class));
    }

    static List<Arguments> zombieHitboxDefinitions() {
        return List.of(arguments(createHitboxDefinition()));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("zombieHitboxDefinitions")
    void resolveHitboxReturnsHumanoidHitboxForZombieEntityRegardlessWhetherHitboxDefinitionIsFoundOrNot(HitboxDefinition hitboxDefinition) {
        Zombie zombie = mock(Zombie.class);
        when(zombie.getType()).thenReturn(EntityType.ZOMBIE);

        when(hitboxConfiguration.getHitboxDefinition("zombie", "standing")).thenReturn(Optional.ofNullable(hitboxDefinition));

        Optional<Hitbox> hitboxOptional = hitboxResolver.resolveHitbox(zombie);

        assertThat(hitboxOptional).hasValueSatisfying(hitbox -> assertThat(hitbox).isInstanceOf(HumanoidHitbox.class));
    }

    private static HitboxDefinition createHitboxDefinition() {
        HitboxComponentDefinition hitboxComponentDefinition = new HitboxComponentDefinition();
        hitboxComponentDefinition.type = "TORSO";
        hitboxComponentDefinition.size = new Double[] { 0.7, 0.4, 0.2 };
        hitboxComponentDefinition.offset = new Double[] { 0.0, 0.7, 0.0 };

        HitboxDefinition hitboxDefinition = new HitboxDefinition();
        hitboxDefinition.components = List.of(hitboxComponentDefinition);
        return hitboxDefinition;
    }
}
