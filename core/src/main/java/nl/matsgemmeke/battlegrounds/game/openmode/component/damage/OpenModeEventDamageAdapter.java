package nl.matsgemmeke.battlegrounds.game.openmode.component.damage;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.damage.EventDamageAdapter;
import nl.matsgemmeke.battlegrounds.game.component.damage.EventDamageResult;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.DamageNew;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.game.damage.EntityDamageEvent;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class OpenModeEventDamageAdapter implements EventDamageAdapter {

    private final DamageProcessor damageProcessor;
    private final MeleeWeaponRegistry meleeWeaponRegistry;
    private final PlayerRegistry playerRegistry;

    @Inject
    public OpenModeEventDamageAdapter(DamageProcessor damageProcessor, MeleeWeaponRegistry meleeWeaponRegistry, PlayerRegistry playerRegistry) {
        this.damageProcessor = damageProcessor;
        this.meleeWeaponRegistry = meleeWeaponRegistry;
        this.playerRegistry = playerRegistry;
    }

    public EventDamageResult processMeleeDamage(Entity entity, Entity damager, double damageAmount) {
        GamePlayer damagerGamePlayer = playerRegistry.findByUniqueId(damager.getUniqueId()).orElse(null);
        GamePlayer entityGamePlayer = playerRegistry.findByUniqueId(entity.getUniqueId()).orElse(null);

        if (damagerGamePlayer == null || entityGamePlayer == null) {
            return new EventDamageResult(damageAmount);
        }

        ItemStack itemStack = damagerGamePlayer.getHeldItem();
        MeleeWeapon meleeWeapon = meleeWeaponRegistry.getAssignedMeleeWeapon(damagerGamePlayer, itemStack).orElse(null);

        if (meleeWeapon == null) {
            return new EventDamageResult(damageAmount);
        }

        double meleeDamageAmount = meleeWeapon.getAttackDamage() * damagerGamePlayer.getAttackStrength();

        DamageNew damage = new DamageNew(meleeDamageAmount, DamageType.MELEE_DAMAGE);
        EntityDamageEvent entityDamageEvent = new EntityDamageEvent(entityGamePlayer, damagerGamePlayer, damage);

        damageProcessor.processDamage(entityDamageEvent);

        return new EventDamageResult(meleeDamageAmount);
    }
}
