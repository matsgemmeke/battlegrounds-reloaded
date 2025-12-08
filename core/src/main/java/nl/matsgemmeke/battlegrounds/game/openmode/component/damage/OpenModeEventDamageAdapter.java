package nl.matsgemmeke.battlegrounds.game.openmode.component.damage;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.damage.EventDamageAdapter;
import nl.matsgemmeke.battlegrounds.game.component.damage.EventDamageResult;
import nl.matsgemmeke.battlegrounds.game.component.entity.MobRegistry;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.DamageNew;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.game.damage.EntityDamageEvent;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class OpenModeEventDamageAdapter implements EventDamageAdapter {

    private final DamageProcessor damageProcessor;
    private final MeleeWeaponRegistry meleeWeaponRegistry;
    private final MobRegistry mobRegistry;
    private final PlayerRegistry playerRegistry;

    @Inject
    public OpenModeEventDamageAdapter(DamageProcessor damageProcessor, MeleeWeaponRegistry meleeWeaponRegistry, MobRegistry mobRegistry, PlayerRegistry playerRegistry) {
        this.damageProcessor = damageProcessor;
        this.mobRegistry = mobRegistry;
        this.meleeWeaponRegistry = meleeWeaponRegistry;
        this.playerRegistry = playerRegistry;
    }

    public EventDamageResult processMeleeDamage(Entity victim, Entity damager, double damageAmount) {
        GameEntity victimGameEntity = this.findGameEntity(victim);
        GamePlayer damagerGamePlayer = playerRegistry.findByUniqueId(damager.getUniqueId()).orElse(null);

        if (victimGameEntity == null || damagerGamePlayer == null) {
            return new EventDamageResult(damageAmount);
        }

        ItemStack itemStack = damagerGamePlayer.getHeldItem();
        MeleeWeapon meleeWeapon = meleeWeaponRegistry.getAssignedMeleeWeapon(damagerGamePlayer, itemStack).orElse(null);

        if (meleeWeapon == null) {
            return new EventDamageResult(damageAmount);
        }

        double meleeDamageAmount = meleeWeapon.getAttackDamage() * damagerGamePlayer.getAttackStrength();

        DamageNew damage = new DamageNew(meleeDamageAmount, DamageType.MELEE_DAMAGE);
        EntityDamageEvent entityDamageEvent = new EntityDamageEvent(victimGameEntity, damagerGamePlayer, damage);

        damageProcessor.processDamage(entityDamageEvent);

        return new EventDamageResult(0);
    }

    private GameEntity findGameEntity(Entity entity) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(entity.getUniqueId()).orElse(null);

        if (gamePlayer != null) {
            return gamePlayer;
        }

        if (entity instanceof LivingEntity livingEntity) {
            return mobRegistry.register(livingEntity);
        }

        return null;
    }
}
