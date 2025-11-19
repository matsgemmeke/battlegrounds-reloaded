package nl.matsgemmeke.battlegrounds.item.shoot.launcher.arrow;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;

public interface ArrowLauncherFactory {

    ProjectileLauncher create(ArrowProperties properties, ItemEffect itemEffect);
}
