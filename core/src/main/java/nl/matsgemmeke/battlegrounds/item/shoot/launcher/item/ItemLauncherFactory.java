package nl.matsgemmeke.battlegrounds.item.shoot.launcher.item;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;

public interface ItemLauncherFactory {

    ItemLauncher create(ItemLaunchProperties properties, ItemEffect itemEffect);
}
