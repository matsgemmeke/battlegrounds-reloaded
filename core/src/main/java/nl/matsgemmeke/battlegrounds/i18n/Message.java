package nl.matsgemmeke.battlegrounds.i18n;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class Message {

    private final Component component;

    public Message(Component component) {
        this.component = component;
    }

    public void send(Audience audience) {
        audience.sendMessage(component);
    }
}
