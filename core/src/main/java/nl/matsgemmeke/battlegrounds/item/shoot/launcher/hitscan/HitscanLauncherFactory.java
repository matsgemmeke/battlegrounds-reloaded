package nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;

public interface HitscanLauncherFactory {

    ProjectileLauncher create(HitscanProperties properties, ItemEffect itemEffect);
}
