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
import nl.matsgemmeke.battlegrounds.game.damage.*;
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

    public EventDamageResult processMeleeDamage(Entity damager, Entity victim, double damageAmount) {
        GamePlayer damagerGamePlayer = playerRegistry.findByUniqueId(damager.getUniqueId()).orElse(null);
        GameEntity victimGameEntity = this.findGameEntity(victim);

        if (damagerGamePlayer == null || victimGameEntity == null) {
            return new EventDamageResult(damageAmount);
        }

        ItemStack itemStack = damagerGamePlayer.getHeldItem();
        MeleeWeapon meleeWeapon = meleeWeaponRegistry.getAssignedMeleeWeapon(damagerGamePlayer, itemStack).orElse(null);

        if (meleeWeapon == null) {
            return new EventDamageResult(damageAmount);
        }

        double meleeDamageAmount = meleeWeapon.getAttackDamage() * damagerGamePlayer.getAttackStrength();

        Damage damage = new Damage(meleeDamageAmount, DamageType.MELEE_DAMAGE);
        DamageContext damageContext = new DamageContext(damagerGamePlayer, victimGameEntity, damage);

        damageProcessor.processDamage(damageContext);

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
