package nl.matsgemmeke.battlegrounds.game.damage.check;

import nl.matsgemmeke.battlegrounds.game.damage.DamageEvent;
import org.jetbrains.annotations.NotNull;

public interface DamageCheck {

    void process(@NotNull DamageEvent event);
}
