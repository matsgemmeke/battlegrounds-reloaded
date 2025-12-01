package nl.matsgemmeke.battlegrounds.game.component.damage;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEventNew;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
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
class EventDamageAdapterTest {

    private static final UUID DAMAGER_UNIQUE_ID = UUID.randomUUID();
    private static final ItemStack ITEM_STACK = new ItemStack(Material.IRON_SWORD);
    private static final float DAMAGER_ATTACK_STRENGTH = 1.0F;
    private static final double MELEE_WEAPON_DAMAGE = 50.0;

    @Mock
    private DamageProcessor damageProcessor;
    @Mock
    private MeleeWeaponRegistry meleeWeaponRegistry;
    @Mock
    private PlayerRegistry playerRegistry;
    @InjectMocks
    private EventDamageAdapter eventDamageAdapter;

    @Test
    void processMeleeDamageDoesNothingWhenGivenDamagerIsNoRegisteredGamePlayer() {
        Entity entity = mock(Entity.class);

        Entity damager = mock(Entity.class);
        when(damager.getUniqueId()).thenReturn(DAMAGER_UNIQUE_ID);

        when(playerRegistry.findByUniqueId(DAMAGER_UNIQUE_ID)).thenReturn(Optional.empty());

        eventDamageAdapter.processMeleeDamage(entity, damager);

        verifyNoInteractions(damageProcessor);
    }

    @Test
    void processMeleeDamageDoesNothingWhenDamagerHeldItemStackIsNoRegisteredMeleeWeapon() {
        Entity entity = mock(Entity.class);

        Entity damager = mock(Entity.class);
        when(damager.getUniqueId()).thenReturn(DAMAGER_UNIQUE_ID);

        GamePlayer damagerGamePlayer = mock(GamePlayer.class);
        when(damagerGamePlayer.getHeldItem()).thenReturn(ITEM_STACK);

        when(playerRegistry.findByUniqueId(DAMAGER_UNIQUE_ID)).thenReturn(Optional.of(damagerGamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(damagerGamePlayer, ITEM_STACK)).thenReturn(Optional.empty());

        eventDamageAdapter.processMeleeDamage(entity, damager);

        verifyNoInteractions(damageProcessor);
    }

    @Test
    void processMeleeDamageCreatesDamageEventForDamageProcessor() {
        Entity entity = mock(Entity.class);

        Entity damager = mock(Entity.class);
        when(damager.getUniqueId()).thenReturn(DAMAGER_UNIQUE_ID);

        GamePlayer damagerGamePlayer = mock(GamePlayer.class);
        when(damagerGamePlayer.getAttackStrength()).thenReturn(DAMAGER_ATTACK_STRENGTH);
        when(damagerGamePlayer.getHeldItem()).thenReturn(ITEM_STACK);

        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        when(meleeWeapon.getAttackDamage()).thenReturn(MELEE_WEAPON_DAMAGE);

        when(playerRegistry.findByUniqueId(DAMAGER_UNIQUE_ID)).thenReturn(Optional.of(damagerGamePlayer));
        when(meleeWeaponRegistry.getAssignedMeleeWeapon(damagerGamePlayer, ITEM_STACK)).thenReturn(Optional.of(meleeWeapon));

        eventDamageAdapter.processMeleeDamage(entity, damager);

        ArgumentCaptor<DamageEventNew> damageEventCaptor = ArgumentCaptor.forClass(DamageEventNew.class);
        verify(damageProcessor).processDamage(damageEventCaptor.capture());

        assertThat(damageEventCaptor.getValue()).satisfies(damageEvent -> {
            assertThat(damageEvent.entity()).isEqualTo(entity);
            assertThat(damageEvent.damager()).isEqualTo(damager);
            assertThat(damageEvent.damage()).satisfies(damage -> {
               assertThat(damage.amount()).isEqualTo(MELEE_WEAPON_DAMAGE);
               assertThat(damage.type()).isEqualTo(DamageType.MELEE_DAMAGE);
            });
        });
    }
}
