package nl.matsgemmeke.battlegrounds.entity.hitbox.resolve;

import nl.matsgemmeke.battlegrounds.configuration.hitbox.HitboxConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxComponentDefinition;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.entity.hitbox.mapper.HitboxMapper;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.*;
import org.bukkit.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HitboxResolverTest {

    @Mock
    private HitboxConfiguration hitboxConfiguration;
    @Spy
    private HitboxMapper hitboxMapper;
    @Mock
    private Logger logger;

    @Test
    @DisplayName("resolveHitboxProvider returns DefaultEntityHitboxProvider when given entity type has no linked hitbox provider")
    void resolveHitboxProvider_unknownEntityType() {
        Entity entity = mock(Entity.class);
        when(entity.getType()).thenReturn(EntityType.UNKNOWN);

        HitboxResolver hitboxResolver = new HitboxResolver(hitboxConfiguration, hitboxMapper, logger);
        HitboxProvider<Entity> hitboxProvider = hitboxResolver.resolveHitboxProvider(entity);

        assertThat(hitboxProvider).isInstanceOf(DefaultEntityHitboxProvider.class);
    }

    static List<Arguments> creeperHitboxDefinitions() {
        return List.of(arguments(createHitboxDefinition()));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("creeperHitboxDefinitions")
    @DisplayName("resolveHitboxProvider returns SimpleEntityHitboxProvider for creeper entity")
    void resolveHitboxProvider_simpleEntity(HitboxDefinition standingHitboxDefinition) {
        Creeper creeper = mock(Creeper.class);
        when(creeper.getType()).thenReturn(EntityType.CREEPER);

        when(hitboxConfiguration.getHitboxDefinition(anyString(), anyString())).thenReturn(Optional.empty());
        when(hitboxConfiguration.getHitboxDefinition("creeper", "standing")).thenReturn(Optional.ofNullable(standingHitboxDefinition));

        HitboxResolver hitboxResolver = new HitboxResolver(hitboxConfiguration, hitboxMapper, logger);
        HitboxProvider<Creeper> hitboxProvider = hitboxResolver.resolveHitboxProvider(creeper);

        assertThat(hitboxProvider).isInstanceOf(SimpleEntityHitboxProvider.class);
    }

    static List<Arguments> endermanHitboxDefinitions() {
        return List.of(arguments(null, null), arguments(createHitboxDefinition(), createHitboxDefinition()));
    }

    @ParameterizedTest
    @MethodSource("endermanHitboxDefinitions")
    @DisplayName("resolveHitboxProvider returns EndermanHitboxProvider for enderman entity")
    void resolveHitboxProvider_endermanEntity(HitboxDefinition standingHitboxDefinition, HitboxDefinition carryingHitboxDefinition) {
        Enderman enderman = mock(Enderman.class);
        when(enderman.getType()).thenReturn(EntityType.ENDERMAN);

        when(hitboxConfiguration.getHitboxDefinition(anyString(), anyString())).thenReturn(Optional.empty());
        when(hitboxConfiguration.getHitboxDefinition("enderman", "standing")).thenReturn(Optional.ofNullable(standingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("enderman", "carrying")).thenReturn(Optional.ofNullable(carryingHitboxDefinition));

        HitboxResolver hitboxResolver = new HitboxResolver(hitboxConfiguration, hitboxMapper, logger);
        HitboxProvider<Enderman> hitboxProvider = hitboxResolver.resolveHitboxProvider(enderman);

        assertThat(hitboxProvider).isInstanceOf(EndermanHitboxProvider.class);
    }

    static List<Arguments> playerHitboxDefinitions() {
        return List.of(arguments(null, null, null), arguments(createHitboxDefinition(), createHitboxDefinition(), createHitboxDefinition()));
    }

    @ParameterizedTest
    @MethodSource("playerHitboxDefinitions")
    @DisplayName("resolveHitboxProvider returns PlayerHitboxProvider for player entity")
    void resolveHitboxProvider_playerEntity(HitboxDefinition standingHitboxDefinition, HitboxDefinition sneakingHitboxDefinition, HitboxDefinition sleepingHitboxDefinition) {
        Player player = mock(Player.class);
        when(player.getType()).thenReturn(EntityType.PLAYER);

        when(hitboxConfiguration.getHitboxDefinition(anyString(), anyString())).thenReturn(Optional.empty());
        when(hitboxConfiguration.getHitboxDefinition("player", "standing")).thenReturn(Optional.ofNullable(standingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("player", "sneaking")).thenReturn(Optional.ofNullable(sneakingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("player", "sleeping")).thenReturn(Optional.ofNullable(sleepingHitboxDefinition));

        HitboxResolver hitboxResolver = new HitboxResolver(hitboxConfiguration, hitboxMapper, logger);
        HitboxProvider<Player> hitboxProvider = hitboxResolver.resolveHitboxProvider(player);

        assertThat(hitboxProvider).isInstanceOf(PlayerHitboxProvider.class);
    }

    static List<Arguments> slimeHitboxDefinitions() {
        return List.of(arguments(createHitboxDefinition()));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("slimeHitboxDefinitions")
    @DisplayName("resolveHitboxProvider returns SlimeHitboxProvider for slime entity")
    void resolveHitboxProvider_slimeEntity(HitboxDefinition standingHitboxDefinition) {
        Slime slime = mock(Slime.class);
        when(slime.getType()).thenReturn(EntityType.SLIME);

        when(hitboxConfiguration.getHitboxDefinition(anyString(), anyString())).thenReturn(Optional.empty());
        when(hitboxConfiguration.getHitboxDefinition("slime", "standing")).thenReturn(Optional.ofNullable(standingHitboxDefinition));

        HitboxResolver hitboxResolver = new HitboxResolver(hitboxConfiguration, hitboxMapper, logger);
        HitboxProvider<Slime> hitboxProvider = hitboxResolver.resolveHitboxProvider(slime);

        assertThat(hitboxProvider).isInstanceOf(SlimeHitboxProvider.class);
    }

    static List<Arguments> villagerHitboxDefinitions() {
        return List.of(arguments(null, null, null, null), arguments(createHitboxDefinition(), createHitboxDefinition(), createHitboxDefinition(), createHitboxDefinition()));
    }

    @ParameterizedTest
    @MethodSource("villagerHitboxDefinitions")
    @DisplayName("resolveHitboxProvider returns VillagerHitboxProvider for villager entity")
    void resolveHitboxProvider_villagerEntity(
            HitboxDefinition adultStandingHitboxDefinition,
            HitboxDefinition adultSleepingHitboxDefinition,
            HitboxDefinition babyStandingHitboxDefinition,
            HitboxDefinition babySleepingHitboxDefinition
    ) {
        Villager villager = mock(Villager.class);
        when(villager.getType()).thenReturn(EntityType.VILLAGER);

        when(hitboxConfiguration.getHitboxDefinition(anyString(), anyString())).thenReturn(Optional.empty());
        when(hitboxConfiguration.getHitboxDefinition("villager", "adult-standing")).thenReturn(Optional.ofNullable(adultStandingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("villager", "adult-sleeping")).thenReturn(Optional.ofNullable(adultSleepingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("villager", "baby-standing")).thenReturn(Optional.ofNullable(babyStandingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("villager", "baby-sleeping")).thenReturn(Optional.ofNullable(babySleepingHitboxDefinition));

        HitboxResolver hitboxResolver = new HitboxResolver(hitboxConfiguration, hitboxMapper, logger);
        HitboxProvider<Villager> hitboxProvider = hitboxResolver.resolveHitboxProvider(villager);

        assertThat(hitboxProvider).isInstanceOf(VillagerHitboxProvider.class);
    }

    static List<Arguments> wolfHitboxDefinitions() {
        return List.of(arguments(null, null, null, null), arguments(createHitboxDefinition(), createHitboxDefinition(), createHitboxDefinition(), createHitboxDefinition()));
    }

    @ParameterizedTest
    @MethodSource("wolfHitboxDefinitions")
    @DisplayName("resolveHitboxProvider returns SittableAgeableHitboxProvider for wolf entity")
    void resolveHitboxProvider_wolfEntity(
            HitboxDefinition adultStandingHitboxDefinition,
            HitboxDefinition adultSittingHitboxDefinition,
            HitboxDefinition babyStandingHitboxDefinition,
            HitboxDefinition babySittingHitboxDefinition
    ) {
        Wolf wolf = mock(Wolf.class);
        when(wolf.getType()).thenReturn(EntityType.WOLF);

        when(hitboxConfiguration.getHitboxDefinition(anyString(), anyString())).thenReturn(Optional.empty());
        when(hitboxConfiguration.getHitboxDefinition("wolf", "adult-standing")).thenReturn(Optional.ofNullable(adultStandingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("wolf", "adult-sitting")).thenReturn(Optional.ofNullable(adultSittingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("wolf", "baby-standing")).thenReturn(Optional.ofNullable(babyStandingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("wolf", "baby-sitting")).thenReturn(Optional.ofNullable(babySittingHitboxDefinition));

        HitboxResolver hitboxResolver = new HitboxResolver(hitboxConfiguration, hitboxMapper, logger);
        HitboxProvider<Wolf> hitboxProvider = hitboxResolver.resolveHitboxProvider(wolf);

        assertThat(hitboxProvider).isInstanceOf(SittableAgeableHitboxProvider.class);
    }

    static List<Arguments> zombieHitboxDefinitions() {
        return List.of(arguments(null, null), arguments(createHitboxDefinition(), createHitboxDefinition()));
    }

    @ParameterizedTest
    @MethodSource("zombieHitboxDefinitions")
    @DisplayName("resolveHitboxProvider returns AgeableHitboxProvider for zombie entity")
    void resolveHitboxProvider_zombieEntity(HitboxDefinition adultStandingHitboxDefinition, HitboxDefinition babyStandingHitboxDefinition) {
        Zombie zombie = mock(Zombie.class);
        when(zombie.getType()).thenReturn(EntityType.ZOMBIE);

        when(hitboxConfiguration.getHitboxDefinition(anyString(), anyString())).thenReturn(Optional.empty());
        when(hitboxConfiguration.getHitboxDefinition("zombie", "adult-standing")).thenReturn(Optional.ofNullable(adultStandingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("zombie", "baby-standing")).thenReturn(Optional.ofNullable(babyStandingHitboxDefinition));

        HitboxResolver hitboxResolver = new HitboxResolver(hitboxConfiguration, hitboxMapper, logger);
        HitboxProvider<Zombie> hitboxProvider = hitboxResolver.resolveHitboxProvider(zombie);

        assertThat(hitboxProvider).isInstanceOf(AgeableHitboxProvider.class);
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
