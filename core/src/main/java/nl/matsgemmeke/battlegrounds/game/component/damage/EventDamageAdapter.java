package nl.matsgemmeke.battlegrounds.game.component.damage;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.DamageEventNew;
import nl.matsgemmeke.battlegrounds.game.damage.DamageNew;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * Provides methods for processing damage that originates through event handlers.
 */
public class EventDamageAdapter {

    private final DamageProcessor damageProcessor;
    private final MeleeWeaponRegistry meleeWeaponRegistry;
    private final PlayerRegistry playerRegistry;

    @Inject
    public EventDamageAdapter(DamageProcessor damageProcessor, MeleeWeaponRegistry meleeWeaponRegistry, PlayerRegistry playerRegistry) {
        this.damageProcessor = damageProcessor;
        this.meleeWeaponRegistry = meleeWeaponRegistry;
        this.playerRegistry = playerRegistry;
    }

    public void processMeleeDamage(Entity entity, Entity damager) {
        GamePlayer damagerPlayer = playerRegistry.findByUniqueId(damager.getUniqueId()).orElse(null);

        if (damagerPlayer == null) {
            return;
        }

        ItemStack itemStack = damagerPlayer.getHeldItem();
        MeleeWeapon meleeWeapon = meleeWeaponRegistry.getAssignedMeleeWeapon(damagerPlayer, itemStack).orElse(null);

        if (meleeWeapon == null) {
            return;
        }

        double damageAmount = meleeWeapon.getAttackDamage() * damagerPlayer.getAttackStrength();

        DamageNew damage = new DamageNew(damageAmount, DamageType.MELEE_DAMAGE);
        DamageEventNew damageEvent = new DamageEventNew(entity, damager, damage);

        damageProcessor.processDamage(damageEvent);
    }
}
