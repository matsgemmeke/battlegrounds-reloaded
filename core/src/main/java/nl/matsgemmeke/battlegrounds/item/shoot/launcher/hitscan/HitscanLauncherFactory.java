package nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;

public interface HitscanLauncherFactory {

    ProjectileLauncher create(HitscanProperties properties, AudioEmitter audioEmitter, CollisionDetector collisionDetector, ItemEffect itemEffect, TargetFinder targetFinder);
}
