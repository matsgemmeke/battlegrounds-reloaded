package nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.effect.Effect;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;

public interface FireballLauncherFactory {

    ProjectileLauncher create(FireballProperties fireballProperties,
                              AudioEmitter audioEmitter,
                              CollisionDetector collisionDetector,
                              Effect effect,
                              TargetFinder targetFinder);
}
