package nl.matsgemmeke.battlegrounds.entity.hitbox.resolve;

import nl.matsgemmeke.battlegrounds.configuration.hitbox.HitboxConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxComponentDefinition;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.entity.hitbox.mapper.HitboxMapper;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.AgeableHitboxProvider;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.PlayerHitboxProvider;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

        Optional<HitboxProvider> hitboxProviderOptional = hitboxResolver.resolveHitboxProvider(entity);

        assertThat(hitboxProviderOptional).isEmpty();
    }

    static List<Arguments> playerHitboxDefinitions() {
        return List.of(
                arguments(null, null, null),
                arguments(createHitboxDefinition(), createHitboxDefinition(), createHitboxDefinition())
        );
    }

    @ParameterizedTest
    @MethodSource("playerHitboxDefinitions")
    void resolveHitboxReturnsPlayerHitboxWithDefaultPositionHitboxWhenNoDefinitionIsNotFound(
            HitboxDefinition standingHitboxDefinition,
            HitboxDefinition sneakingHitboxDefinition,
            HitboxDefinition sleepingHitboxDefinition
    ) {
        Player player = mock(Player.class);
        when(player.getType()).thenReturn(EntityType.PLAYER);

        when(hitboxConfiguration.getHitboxDefinition("player", "standing")).thenReturn(Optional.ofNullable(standingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("player", "sneaking")).thenReturn(Optional.ofNullable(sneakingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("player", "sleeping")).thenReturn(Optional.ofNullable(sleepingHitboxDefinition));

        hitboxResolver.registerHitboxProviders();
        Optional<HitboxProvider> hitboxProviderOptional = hitboxResolver.resolveHitboxProvider(player);

        assertThat(hitboxProviderOptional).hasValueSatisfying(hitbox -> assertThat(hitbox).isInstanceOf(PlayerHitboxProvider.class));
    }

    static List<Arguments> zombieHitboxDefinitions() {
        return List.of(
                arguments(null, null),
                arguments(createHitboxDefinition(), createHitboxDefinition())
        );
    }

    @ParameterizedTest
    @MethodSource("zombieHitboxDefinitions")
    void resolveHitboxReturnsHumanoidHitboxForZombieEntityRegardlessWhetherHitboxDefinitionIsFoundOrNot(HitboxDefinition adultStandingHitboxDefinition, HitboxDefinition babyStandingHitboxDefinition) {
        Zombie zombie = mock(Zombie.class);
        when(zombie.getType()).thenReturn(EntityType.ZOMBIE);

        when(hitboxConfiguration.getHitboxDefinition("zombie", "adult-standing")).thenReturn(Optional.ofNullable(adultStandingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("zombie", "baby-standing")).thenReturn(Optional.ofNullable(babyStandingHitboxDefinition));

        Optional<HitboxProvider> hitboxProviderOptional = hitboxResolver.resolveHitboxProvider(zombie);

        assertThat(hitboxProviderOptional).hasValueSatisfying(hitboxProvider -> assertThat(hitboxProvider).isInstanceOf(AgeableHitboxProvider.class));
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
