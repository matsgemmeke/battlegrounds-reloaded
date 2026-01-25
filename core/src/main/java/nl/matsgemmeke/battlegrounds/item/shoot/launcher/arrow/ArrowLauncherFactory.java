package nl.matsgemmeke.battlegrounds.item.shoot.launcher.arrow;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;

public interface ArrowLauncherFactory {

    ArrowLauncher create(ArrowProperties properties, ItemEffect itemEffect);
}
