package nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;

public interface FireballLauncherFactory {

    ProjectileLauncher create(FireballProperties fireballProperties,
                              AudioEmitter audioEmitter,
                              ItemEffect itemEffect,
                              ProjectileHitActionRegistry projectileHitActionRegistry);
}
