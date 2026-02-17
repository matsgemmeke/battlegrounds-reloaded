package nl.matsgemmeke.battlegrounds.item.projectile.effect;

import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;

public interface ProjectileEffect {

    void addTriggerExecutor(TriggerExecutor triggerExecutor);

    void onLaunch(DamageSource damageSource, Projectile projectile);
}
