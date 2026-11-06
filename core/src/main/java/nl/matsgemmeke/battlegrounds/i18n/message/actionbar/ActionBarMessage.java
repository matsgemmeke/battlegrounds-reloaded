package nl.matsgemmeke.battlegrounds.i18n.message.actionbar;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import nl.matsgemmeke.battlegrounds.i18n.message.Message;

public class ActionBarMessage implements Message {

    private final Component component;

    public ActionBarMessage(Component component) {
        this.component = component;
    }

    @Override
    public void send(Audience audience) {
        audience.sendActionBar(component);
    }
}
