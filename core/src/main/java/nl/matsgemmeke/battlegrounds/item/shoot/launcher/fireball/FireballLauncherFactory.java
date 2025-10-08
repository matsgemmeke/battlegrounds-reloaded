package nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball;

import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectNew;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;

public interface FireballLauncherFactory {

    ProjectileLauncher create(FireballProperties fireballProperties, ItemEffectNew itemEffect);
}
