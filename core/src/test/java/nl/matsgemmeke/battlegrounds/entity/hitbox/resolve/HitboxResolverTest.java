package nl.matsgemmeke.battlegrounds.entity.hitbox.resolve;

import nl.matsgemmeke.battlegrounds.configuration.hitbox.HitboxConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxComponentDefinition;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.entity.hitbox.mapper.HitboxMapper;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.*;
import org.bukkit.entity.*;
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

    @Test
    void resolveHitboxReturnsBoundingBoxHitboxProviderWhenGivenEntityTypeHasNoLinkedHitboxProvider() {
        Entity entity = mock(Entity.class);
        when(entity.getType()).thenReturn(EntityType.UNKNOWN);

        HitboxResolver hitboxResolver = new HitboxResolver(hitboxConfiguration, hitboxMapper);
        HitboxProviderNew<Entity> hitboxProvider = hitboxResolver.resolveHitboxProviderNew(entity);

        assertThat(hitboxProvider).isInstanceOf(BoundingBoxHitboxProvider.class);
    }

    static List<Arguments> creeperHitboxDefinitions() {
        return List.of(arguments(createHitboxDefinition()));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("creeperHitboxDefinitions")
    void resolveHitboxReturnsDefaultHitboxProviderForCreeperEntity(HitboxDefinition standingHitboxDefinition) {
        Creeper creeper = mock(Creeper.class);
        when(creeper.getType()).thenReturn(EntityType.CREEPER);

        when(hitboxConfiguration.getHitboxDefinition("creeper", "standing")).thenReturn(Optional.ofNullable(standingHitboxDefinition));

        HitboxResolver hitboxResolver = new HitboxResolver(hitboxConfiguration, hitboxMapper);
        HitboxProvider hitboxProvider = hitboxResolver.resolveHitboxProvider(creeper);

        assertThat(hitboxProvider).isInstanceOf(DefaultHitboxProvider.class);
    }

    static List<Arguments> endermanHitboxDefinitions() {
        return List.of(arguments(null, null), arguments(createHitboxDefinition(), createHitboxDefinition()));
    }

    @ParameterizedTest
    @MethodSource("endermanHitboxDefinitions")
    void resolveHitboxReturnsEndermanHitboxProviderForEndermanEntity(HitboxDefinition standingHitboxDefinition, HitboxDefinition carryingHitboxDefinition) {
        Enderman enderman = mock(Enderman.class);
        when(enderman.getType()).thenReturn(EntityType.ENDERMAN);

        when(hitboxConfiguration.getHitboxDefinition("enderman", "standing")).thenReturn(Optional.ofNullable(standingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("enderman", "carrying")).thenReturn(Optional.ofNullable(carryingHitboxDefinition));

        HitboxResolver hitboxResolver = new HitboxResolver(hitboxConfiguration, hitboxMapper);
        HitboxProvider hitboxProvider = hitboxResolver.resolveHitboxProvider(enderman);

        assertThat(hitboxProvider).isInstanceOf(EndermanHitboxProvider.class);
    }

    static List<Arguments> playerHitboxDefinitions() {
        return List.of(arguments(null, null, null), arguments(createHitboxDefinition(), createHitboxDefinition(), createHitboxDefinition()));
    }

    @ParameterizedTest
    @MethodSource("playerHitboxDefinitions")
    void resolveHitboxReturnsPlayerHitboxProviderForPlayerEntity(HitboxDefinition standingHitboxDefinition, HitboxDefinition sneakingHitboxDefinition, HitboxDefinition sleepingHitboxDefinition) {
        Player player = mock(Player.class);
        when(player.getType()).thenReturn(EntityType.PLAYER);

        when(hitboxConfiguration.getHitboxDefinition("player", "standing")).thenReturn(Optional.ofNullable(standingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("player", "sneaking")).thenReturn(Optional.ofNullable(sneakingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("player", "sleeping")).thenReturn(Optional.ofNullable(sleepingHitboxDefinition));

        HitboxResolver hitboxResolver = new HitboxResolver(hitboxConfiguration, hitboxMapper);
        HitboxProviderNew<Player> hitboxProvider = hitboxResolver.resolveHitboxProviderNew(player);

        assertThat(hitboxProvider).isInstanceOf(PlayerHitboxProvider.class);
    }

    static List<Arguments> slimeHitboxDefinitions() {
        return List.of(arguments(createHitboxDefinition()));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("slimeHitboxDefinitions")
    void resolveHitboxReturnsSlimeHitboxProviderForSlimeEntity(HitboxDefinition standingHitboxDefinition) {
        Slime slime = mock(Slime.class);
        when(slime.getType()).thenReturn(EntityType.SLIME);

        when(hitboxConfiguration.getHitboxDefinition("slime", "standing")).thenReturn(Optional.ofNullable(standingHitboxDefinition));

        HitboxResolver hitboxResolver = new HitboxResolver(hitboxConfiguration, hitboxMapper);
        HitboxProvider hitboxProvider = hitboxResolver.resolveHitboxProvider(slime);

        assertThat(hitboxProvider).isInstanceOf(SlimeHitboxProvider.class);
    }

    static List<Arguments> villagerHitboxDefinitions() {
        return List.of(arguments(null, null, null, null), arguments(createHitboxDefinition(), createHitboxDefinition(), createHitboxDefinition(), createHitboxDefinition()));
    }

    @ParameterizedTest
    @MethodSource("villagerHitboxDefinitions")
    void resolveHitboxReturnsVillagerHitboxProviderForVillagerEntity(
            HitboxDefinition adultStandingHitboxDefinition,
            HitboxDefinition adultSleepingHitboxDefinition,
            HitboxDefinition babyStandingHitboxDefinition,
            HitboxDefinition babySleepingHitboxDefinition
    ) {
        Villager villager = mock(Villager.class);
        when(villager.getType()).thenReturn(EntityType.VILLAGER);

        when(hitboxConfiguration.getHitboxDefinition("villager", "adult-standing")).thenReturn(Optional.ofNullable(adultStandingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("villager", "adult-sleeping")).thenReturn(Optional.ofNullable(adultSleepingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("villager", "baby-standing")).thenReturn(Optional.ofNullable(babyStandingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("villager", "baby-sleeping")).thenReturn(Optional.ofNullable(babySleepingHitboxDefinition));

        HitboxResolver hitboxResolver = new HitboxResolver(hitboxConfiguration, hitboxMapper);
        HitboxProvider hitboxProvider = hitboxResolver.resolveHitboxProvider(villager);

        assertThat(hitboxProvider).isInstanceOf(VillagerHitboxProvider.class);
    }

    static List<Arguments> wolfHitboxDefinitions() {
        return List.of(arguments(null, null, null, null), arguments(createHitboxDefinition(), createHitboxDefinition(), createHitboxDefinition(), createHitboxDefinition()));
    }

    @ParameterizedTest
    @MethodSource("wolfHitboxDefinitions")
    void resolveHitboxReturnsSittableAgeableHitboxProviderForWolfEntity(
            HitboxDefinition adultStandingHitboxDefinition,
            HitboxDefinition adultSittingHitboxDefinition,
            HitboxDefinition babyStandingHitboxDefinition,
            HitboxDefinition babySittingHitboxDefinition
    ) {
        Wolf wolf = mock(Wolf.class);
        when(wolf.getType()).thenReturn(EntityType.WOLF);

        when(hitboxConfiguration.getHitboxDefinition("wolf", "adult-standing")).thenReturn(Optional.ofNullable(adultStandingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("wolf", "adult-sitting")).thenReturn(Optional.ofNullable(adultSittingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("wolf", "baby-standing")).thenReturn(Optional.ofNullable(babyStandingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("wolf", "baby-sitting")).thenReturn(Optional.ofNullable(babySittingHitboxDefinition));

        HitboxResolver hitboxResolver = new HitboxResolver(hitboxConfiguration, hitboxMapper);
        HitboxProviderNew<Wolf> hitboxProvider = hitboxResolver.resolveHitboxProviderNew(wolf);

        assertThat(hitboxProvider).isInstanceOf(SittableAgeableHitboxProvider.class);
    }

    static List<Arguments> zombieHitboxDefinitions() {
        return List.of(arguments(null, null), arguments(createHitboxDefinition(), createHitboxDefinition()));
    }

    @ParameterizedTest
    @MethodSource("zombieHitboxDefinitions")
    void resolveHitboxReturnsAgeableHitboxProviderForZombieEntity(HitboxDefinition adultStandingHitboxDefinition, HitboxDefinition babyStandingHitboxDefinition) {
        Zombie zombie = mock(Zombie.class);
        when(zombie.getType()).thenReturn(EntityType.ZOMBIE);

        when(hitboxConfiguration.getHitboxDefinition("zombie", "adult-standing")).thenReturn(Optional.ofNullable(adultStandingHitboxDefinition));
        when(hitboxConfiguration.getHitboxDefinition("zombie", "baby-standing")).thenReturn(Optional.ofNullable(babyStandingHitboxDefinition));

        HitboxResolver hitboxResolver = new HitboxResolver(hitboxConfiguration, hitboxMapper);
        HitboxProvider hitboxProvider = hitboxResolver.resolveHitboxProvider(zombie);

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
