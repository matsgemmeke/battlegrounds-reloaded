package nl.matsgemmeke.battlegrounds.game.openmode.component.damage;

import nl.matsgemmeke.battlegrounds.entity.GameMob;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.damage.EventDamageResult;
import nl.matsgemmeke.battlegrounds.game.component.entity.MobRegistry;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.DamageContext;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
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
    void processMeleeDamageReturnsOriginalDamageWhenGivenDamagerIsNoRegisteredGamePlayer() {
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
    void processMeleeDamageReturnsOriginalDamageWhenGivenVictimIsNoRegisteredGameEntity() {
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
    void processMeleeDamageReturnsOriginalDamageWhenDamagerHeldItemStackIsNoRegisteredMeleeWeapon() {
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
    void processMeleeDamageCreatesDamageEventForDamageProcessor() {
        GameMob victimGameMob = mock(GameMob.class);

        GamePlayer damagerGamePlayer = mock(GamePlayer.class);
        when(damagerGamePlayer.getAttackStrength()).thenReturn(DAMAGER_ATTACK_STRENGTH);
        when(damagerGamePlayer.getHeldItem()).thenReturn(ITEM_STACK);

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
        });
    }
}
