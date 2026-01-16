package nl.matsgemmeke.battlegrounds.item.trigger;

import java.util.Optional;

public interface Trigger {

    boolean activates(TriggerContext context);

    Optional<CheckResult> check(TriggerContext context);
}
