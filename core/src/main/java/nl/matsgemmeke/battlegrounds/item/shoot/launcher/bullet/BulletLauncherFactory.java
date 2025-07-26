package nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.effect.Effect;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import org.jetbrains.annotations.NotNull;

public interface BulletLauncherFactory {

    @NotNull
    ProjectileLauncher create(BulletProperties bulletProperties, AudioEmitter audioEmitter, CollisionDetector collisionDetector, Effect effect, TargetFinder targetFinder);
}
