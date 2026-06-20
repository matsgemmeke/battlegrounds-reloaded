package nl.matsgemmeke.battlegrounds.game.freeplay.component.damage;

import nl.matsgemmeke.battlegrounds.entity.GameMob;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageType;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.damage.EventDamageResult;
import nl.matsgemmeke.battlegrounds.game.component.entity.MobRegistry;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.DamageContext;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenModeEventDamageAdapterTest {

    private static final UUID DAMAGER_UNIQUE_ID = UUID.randomUUID();
    private static final UUID VICTIM_UNIQUE_ID = UUID.randomUUID();
    private static final ItemStack ITEM_STACK = new ItemStack(Material.IRON_SWORD);
    private static final float DAMAGER_ATTACK_STRENGTH = 1.0F;
    private static final double EVENT_DAMAGE = 10.0;
    private static final double MELEE_WEAPON_DAMAGE = 50.0;

    @Mock
    private DamageProcessor damageProcessor;
    @Mock
    private MeleeWeaponRegistry meleeWeaponRegistry;
    @Mock
    private MobRegistry mobRegistry;
    @Mock
    private PlayerRegistry playerRegistry;
    @InjectMocks
    private OpenModeEventDamageAdapter eventDamageAdapter;

    @Test
    @DisplayName("processMeleeDamage returns original damage when given damager not a registered player")
    void processMeleeDamager_damagerNotRegistered() {
        GamePlayer victimGamePlayer = mock(GamePlayer.class);

        Entity damager = mock(Entity.class);
        when(damager.getUniqueId()).thenReturn(DAMAGER_UNIQUE_ID);

        Entity victim = mock(Entity.class);
        when(victim.getUniqueId()).thenReturn(VICTIM_UNIQUE_ID);

        when(playerRegistry.findByUniqueId(DAMAGER_UNIQUE_ID)).thenReturn(Optional.empty());
        when(playerRegistry.findByUniqueId(VICTIM_UNIQUE_ID)).thenReturn(Optional.of(victimGamePlayer));

        EventDamageResult eventDamageResult = eventDamageAdapter.processMeleeDamage(damager, victim, EVENT_DAMAGE);

        assertThat(eventDamageResult.damageAmount()).isEqualTo(EVENT_DAMAGE);

        verifyNoInteractions(damageProcessor);
    }

    @Test
    @DisplayName("processMeleeDamage returns original damage when given victim not a registered entity")
    void processMeleeDamage_victimNotRegistered() {
        GamePlayer damagerGamePlayer = mock(GamePlayer.class);

        Entity victim = mock(Entity.class);
        when(victim.getUniqueId()).thenReturn(VICTIM_UNIQUE_ID);

        Entity damager = mock(Entity.class);
        when(damager.getUniqueId()).thenReturn(DAMAGER_UNIQUE_ID);

        when(playerRegistry.findByUniqueId(DAMAGER_UNIQUE_ID)).thenReturn(Optional.of(damagerGamePlayer));
        when(playerRegistry.findByUniqueId(VICTIM_UNIQUE_ID)).thenReturn(Optional.empty());

        EventDamageResult eventDamageResult = eventDamageAdapter.processMeleeDamage(damager, victim, EVENT_DAMAGE);

        assertThat(eventDamageResult.damageAmount()).isEqualTo(EVENT_DAMAGE);

        verifyNoInteractions(damageProcessor);
    }

    @Test
    @DisplayName("processMeleeDamage returns original damage when damager held item is not a registered melee weapon")
    void processMeleeDamage_victimHeldItemIsNoMeleeWeapon() {
        GamePlayer victimGamePlayer = mock(GamePlayer.class);

        GamePlayer damagerGamePlayer = mock(GamePlayer.class);
        when(damagerGamePlayer.getHeldItem()).thenReturn(ITEM_STACK);

        Entity victim = mock(Entity.class);
        when(victim.getUniqueId()).thenReturn(VICTIM_UNIQUE_ID);

        Entity damager = mock(Entity.class);
        when(damager.getUniqueId()).thenReturn(DAMAGER_UNIQUE_ID);

        when(playerRegistry.findByUniqueId(DAMAGER_UNIQUE_ID)).thenReturn(Optional.of(damagerGamePlayer));
        when(playerRegistry.findByUniqueId(VICTIM_UNIQUE_ID)).thenReturn(Optional.of(victimGamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(damagerGamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        EventDamageResult eventDamageResult = eventDamageAdapter.processMeleeDamage(damager, victim, EVENT_DAMAGE);

        assertThat(eventDamageResult.damageAmount()).isEqualTo(EVENT_DAMAGE);

        verifyNoInteractions(damageProcessor);
    }

    @Test
    @DisplayName("processMeleeDamage creates damage event for damage processor")
    void processMeleeDamage_successful() {
        World world = mock(World.class);
        Location damagerLocation = new Location(world, 0, 0, 0);
        Location victimLocation = new Location(world, 10, 0, 0);

        GamePlayer damagerGamePlayer = mock(GamePlayer.class);
        when(damagerGamePlayer.getAttackStrength()).thenReturn(DAMAGER_ATTACK_STRENGTH);
        when(damagerGamePlayer.getHeldItem()).thenReturn(ITEM_STACK);
        when(damagerGamePlayer.getLocation()).thenReturn(damagerLocation);

        GameMob victimGameMob = mock(GameMob.class);
        when(victimGameMob.getLocation()).thenReturn(victimLocation);

        LivingEntity victim = mock(LivingEntity.class);
        when(victim.getUniqueId()).thenReturn(VICTIM_UNIQUE_ID);

        Entity damager = mock(Entity.class);
        when(damager.getUniqueId()).thenReturn(DAMAGER_UNIQUE_ID);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getAttackDamage()).thenReturn(MELEE_WEAPON_DAMAGE);

        when(playerRegistry.findByUniqueId(DAMAGER_UNIQUE_ID)).thenReturn(Optional.of(damagerGamePlayer));
        when(playerRegistry.findByUniqueId(VICTIM_UNIQUE_ID)).thenReturn(Optional.empty());
        when(mobRegistry.register(victim)).thenReturn(victimGameMob);
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(damagerGamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        EventDamageResult eventDamageResult = eventDamageAdapter.processMeleeDamage(damager, victim, EVENT_DAMAGE);

        assertThat(eventDamageResult.damageAmount()).isZero();

        ArgumentCaptor<DamageContext> damageContextCaptor = ArgumentCaptor.forClass(DamageContext.class);
        verify(damageProcessor).processDamage(damageContextCaptor.capture());

        assertThat(damageContextCaptor.getValue()).satisfies(damageContext -> {
            assertThat(damageContext.source()).isEqualTo(damagerGamePlayer);
            assertThat(damageContext.target()).isEqualTo(victimGameMob);
            assertThat(damageContext.damage()).satisfies(damage -> {
               assertThat(damage.amount()).isEqualTo(MELEE_WEAPON_DAMAGE);
               assertThat(damage.type()).isEqualTo(DamageType.MELEE_DAMAGE);
            });
            assertThat(damageContext.distance()).isEqualTo(10.0);
        });
    }
}
